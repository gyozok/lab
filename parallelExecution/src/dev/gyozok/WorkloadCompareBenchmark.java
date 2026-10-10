package dev.gyozok;

import dev.gyozok.metrics.MetricsCollector;
import dev.gyozok.pool.PoolConfig;
import dev.gyozok.pool.PoolFactory;
import dev.gyozok.pool.PoolType;
import dev.gyozok.workload.BlockingIoWorkload;
import dev.gyozok.workload.CpuBoundWorkload;
import dev.gyozok.workload.Workload;
import dev.gyozok.wrapper.TaskWrapper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Compares CPU-bound vs blocking I/O workloads across different pool sizes.
 *
 * Shows:
 * 1. CPU-bound: throughput peaks near cores, adding more threads hurts (context switching)
 * 2. Blocking I/O: throughput keeps scaling with thread count because threads block
 * 3. Virtual threads: ideal for blocking I/O, scales without explicit pool sizing
 *
 * Usage: java WorkloadCompareBenchmark [tasks]
 *   defaults: tasks = 10_000 (smaller to complete in reasonable time)
 */
public class WorkloadCompareBenchmark {

    public static void main(String[] args) throws Exception {
        int tasks = args.length > 0 ? Integer.parseInt(args[0]) : 5_000;
        int cores = Runtime.getRuntime().availableProcessors();

        System.out.printf("Java %s | cores=%d | tasks=%d%n%n", Runtime.version(), cores, tasks);

        // Define workloads to test
        var workloads = new Workload[]{
                new CpuBoundWorkload(500),  // CPU-bound
                new BlockingIoWorkload(100, 5)  // 5ms sleep (I/O-bound) - shorter to complete faster
        };

        for (Workload workload : workloads) {
            System.out.printf("=== %s ===%n", workload.name());
            testWorkloadAcrossPoolSizes(workload, tasks, cores);
            System.out.println();
        }
    }

    private static void testWorkloadAcrossPoolSizes(Workload workload, int tasks, int cores) throws Exception {
        // Warmup
        ExecutorService warmupPool = PoolFactory.create(
                PoolConfig.builder().type(PoolType.FIXED).corePoolSize(cores).build()
        );
        MetricsCollector warmupMetrics = new MetricsCollector();
        submitAndWait(warmupPool, workload, Math.min(tasks / 5, 500), warmupMetrics);
        warmupPool.shutdown();
        warmupPool.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);

        // Test thread counts from 1 to 2x cores (skip 4x cores, it's too slow)
        int[] threadCounts = workload.name().contains("blocking")
                ? new int[]{1, cores, cores * 2}  // For I/O, test more threads
                : new int[]{1, cores / 2, cores}; // For CPU, test up to cores

        System.out.printf("threads | throughput  | p50 queue | p99 queue | p50 run | p99 run%n");
        System.out.printf("--------|-------------|-----------|-----------|---------|---------%n");

        for (int threadCount : threadCounts) {
            PoolConfig config = PoolConfig.builder()
                    .type(PoolType.FIXED)
                    .corePoolSize(threadCount)
                    .maxPoolSize(threadCount)
                    .build();

            ExecutorService pool = PoolFactory.create(config);
            MetricsCollector metrics = new MetricsCollector();

            submitAndWait(pool, workload, tasks, metrics);

            pool.shutdown();
            pool.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS);

            MetricsCollector.Snapshot snap = metrics.snapshot();
            System.out.printf("%7d | %10.0f | %8.1f   | %8.1f   | %6.2f  | %6.2f%n",
                    threadCount,
                    snap.throughputTasksPerSecond(),
                    snap.queueWaitP50Nanos() / 1e6,
                    snap.queueWaitP99Nanos() / 1e6,
                    snap.runTimeP50Nanos() / 1e6,
                    snap.runTimeP99Nanos() / 1e6
            );
        }

        // Also test virtual threads for the blocking workload
        if (workload.name().contains("blocking")) {
            PoolConfig virtualConfig = PoolConfig.builder().type(PoolType.VIRTUAL).build();
            ExecutorService virtualPool = PoolFactory.create(virtualConfig);
            MetricsCollector virtualMetrics = new MetricsCollector();

            submitAndWait(virtualPool, workload, tasks, virtualMetrics);

            virtualPool.shutdown();
            virtualPool.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS);

            MetricsCollector.Snapshot snap = virtualMetrics.snapshot();
            System.out.printf("virtual | %10.0f | %8.1f   | %8.1f   | %6.2f  | %6.2f%n",
                    snap.throughputTasksPerSecond(),
                    snap.queueWaitP50Nanos() / 1e6,
                    snap.queueWaitP99Nanos() / 1e6,
                    snap.runTimeP50Nanos() / 1e6,
                    snap.runTimeP99Nanos() / 1e6
            );
        }
    }

    private static void submitAndWait(ExecutorService pool, Workload workload,
                                      int taskCount, MetricsCollector metrics) throws Exception {
        var futures = new ArrayList<Future<Long>>(taskCount);

        for (int i = 0; i < taskCount; i++) {
            var wrappedTask = new TaskWrapper<>(workload.createTask(i), metrics);
            try {
                futures.add(pool.submit(wrappedTask));
            } catch (java.util.concurrent.RejectedExecutionException e) {
                metrics.recordRejected();
            }
        }

        for (var f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}