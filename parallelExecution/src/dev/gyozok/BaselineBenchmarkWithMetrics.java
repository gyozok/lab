package dev.gyozok;

import dev.gyozok.metrics.MetricsCollector;
import dev.gyozok.workload.CpuBoundWorkload;
import dev.gyozok.workload.Workload;
import dev.gyozok.wrapper.TaskWrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Updated baseline that uses TaskWrapper and MetricsCollector to measure latencies.
 *
 * Usage: java BaselineBenchmarkWithMetrics [threads] [tasks] [iterationsPerTask]
 *   defaults: threads = available cores, tasks = 50_000, iterationsPerTask = 1_000
 */
public class BaselineBenchmarkWithMetrics {

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : cores;
        int tasks = args.length > 1 ? Integer.parseInt(args[1]) : 50_000;
        int iterations = args.length > 2 ? Integer.parseInt(args[2]) : 1_000;

        Workload workload = new CpuBoundWorkload(iterations);

        System.out.printf("Java %s | cores=%d | pool=fixed(%d) | workload=%s | tasks=%d%n",
                Runtime.version(), cores, threads, workload.name(), tasks);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads, namedThreads("baseline"))) {

            // Warmup
            MetricsCollector warmupMetrics = new MetricsCollector();
            int warmupTasks = Math.max(tasks / 5, 1_000);
            submitAndWait(pool, workload, warmupTasks, warmupMetrics);
            System.out.printf("warmup done (%d tasks)%n", warmupTasks);

            // Measured run
            MetricsCollector metrics = new MetricsCollector();
            submitAndWait(pool, workload, tasks, metrics);

            MetricsCollector.Snapshot snap = metrics.snapshot();
            snap.print("Results");
        }
    }

    private static void submitAndWait(ExecutorService pool, Workload workload,
                                      int taskCount, MetricsCollector metrics) throws Exception {
        var futures = new java.util.ArrayList<Future<Long>>(taskCount);

        for (int i = 0; i < taskCount; i++) {
            var wrappedTask = new TaskWrapper<>(workload.createTask(i), metrics);
            futures.add(pool.submit(wrappedTask));
        }

        long checksum = 0;
        for (var f : futures) {
            checksum ^= f.get();
        }
        System.out.printf("checksum: %d%n", checksum);
    }

    private static ThreadFactory namedThreads(String prefix) {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> {
            Thread t = new Thread(runnable, prefix + "-" + counter.incrementAndGet());
            t.setDaemon(false);
            return t;
        };
    }
}