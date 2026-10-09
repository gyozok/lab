package dev.gyozok;

import dev.gyozok.metrics.MetricsCollector;
import dev.gyozok.pool.PoolConfig;
import dev.gyozok.pool.PoolFactory;
import dev.gyozok.pool.PoolType;
import dev.gyozok.workload.CpuBoundWorkload;
import dev.gyozok.workload.Workload;
import dev.gyozok.wrapper.TaskWrapper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Compares the three pool types side-by-side on the same workload.
 *
 * Usage: java PoolCompareBenchmark [tasks] [iterationsPerTask]
 *   defaults: tasks = 50_000, iterationsPerTask = 1_000
 */
public class PoolCompareBenchmark {

    public static void main(String[] args) throws Exception {
        int tasks = args.length > 0 ? Integer.parseInt(args[0]) : 50_000;
        int iterations = args.length > 1 ? Integer.parseInt(args[1]) : 1_000;
        int cores = Runtime.getRuntime().availableProcessors();

        Workload workload = new CpuBoundWorkload(iterations);

        System.out.printf("Java %s | cores=%d | workload=%s | tasks=%d%n%n",
                Runtime.version(), cores, workload.name(), tasks);

        // Test configurations
        var configs = new PoolConfig[]{
                // FIXED with different sizes
                PoolConfig.builder()
                        .type(PoolType.FIXED)
                        .corePoolSize(cores / 2)
                        .maxPoolSize(cores / 2)
                        .name("fixed-" + (cores / 2))
                        .build(),

                PoolConfig.builder()
                        .type(PoolType.FIXED)
                        .corePoolSize(cores)
                        .maxPoolSize(cores)
                        .name("fixed-" + cores)
                        .build(),

                PoolConfig.builder()
                        .type(PoolType.FIXED)
                        .corePoolSize(cores * 2)
                        .maxPoolSize(cores * 2)
                        .name("fixed-" + (cores * 2))
                        .build(),

                // ForkJoinPool
                PoolConfig.builder()
                        .type(PoolType.FORK_JOIN)
                        .corePoolSize(cores)
                        .name("fjp-" + cores)
                        .build(),

                // Virtual threads
                PoolConfig.builder()
                        .type(PoolType.VIRTUAL)
                        .name("virtual")
                        .build()
        };

        // Warmup on the first config
        System.out.println("Warmup...");
        ExecutorService warmupPool = PoolFactory.create(configs[0]);
        MetricsCollector warmupMetrics = new MetricsCollector();
        int warmupTasks = Math.max(tasks / 5, 1_000);
        submitAndWait(warmupPool, workload, warmupTasks, warmupMetrics);
        warmupPool.shutdown();
        warmupPool.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("Warmup done\n");

        // Run each configuration
        for (PoolConfig config : configs) {
            ExecutorService pool = PoolFactory.create(config);
            MetricsCollector metrics = new MetricsCollector();

            System.out.printf("Testing: %s%n", config);
            long start = System.currentTimeMillis();
            submitAndWait(pool, workload, tasks, metrics);
            long elapsed = System.currentTimeMillis() - start;

            pool.shutdown();
            pool.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);

            MetricsCollector.Snapshot snap = metrics.snapshot();
            System.out.printf("  elapsed: %.2f s | throughput: %,.0f tasks/s%n",
                    elapsed / 1000.0, snap.throughputTasksPerSecond());
            System.out.printf("  queue wait: p50=%.2f p99=%.2f ms%n",
                    snap.queueWaitP50Nanos() / 1e6, snap.queueWaitP99Nanos() / 1e6);
            System.out.printf("  run time:   p50=%.3f p99=%.3f ms%n%n",
                    snap.runTimeP50Nanos() / 1e6, snap.runTimeP99Nanos() / 1e6);
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

        long checksum = 0;
        for (var f : futures) {
            try {
                checksum ^= f.get();
            } catch (Exception e) {
                System.err.printf("Task failed: %s%n", e.getMessage());
            }
        }
    }
}