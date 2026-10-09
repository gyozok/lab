package dev.gyozok;

import dev.gyozok.workload.CpuBoundWorkload;
import dev.gyozok.workload.RunResult;
import dev.gyozok.workload.Workload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Baseline: one workload on ONE fixed thread pool. Establishes the reference numbers
 * that every later pool configuration will be compared against.
 *
 * Usage: java BaselineBenchmark [threads] [tasks] [iterationsPerTask]
 *   defaults: threads = available cores, tasks = 50_000, iterationsPerTask = 1_000
 */
public class BaselineBenchmark {

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : cores;
        int tasks = args.length > 1 ? Integer.parseInt(args[1]) : 50_000;
        int iterations = args.length > 2 ? Integer.parseInt(args[2]) : 1_000;

        Workload workload = new CpuBoundWorkload(iterations);

        System.out.printf("Java %s | cores=%d | pool=fixed(%d) | workload=%s | tasks=%d%n",
                Runtime.version(), cores, threads, workload.name(), tasks);

        // ExecutorService is AutoCloseable since Java 19: close() waits for running tasks.
        try (ExecutorService pool = Executors.newFixedThreadPool(threads, namedThreads("baseline"))) {

            // Warmup: lets the JIT compile the hot loop and spins up all pool threads.
            int warmupTasks = Math.max(tasks / 5, 1_000);
            run(pool, workload, warmupTasks);
            System.out.printf("warmup done (%d tasks)%n", warmupTasks);

            // Measured runs. Several repetitions show the run-to-run noise.
            int repetitions = 5;
            double[] throughputs = new double[repetitions];
            for (int r = 0; r < repetitions; r++) {
                RunResult result = run(pool, workload, tasks);
                double seconds = result.elapsedNanos() / 1e9;
                throughputs[r] = tasks / seconds;
                System.out.printf("run %d: %,8.0f ms | %,10.0f tasks/s | checksum=%d%n",
                        r + 1, result.elapsedNanos() / 1e6, throughputs[r], result.checksum());
            }

            double best = 0, sum = 0;
            for (double t : throughputs) {
                best = Math.max(best, t);
                sum += t;
            }
            System.out.printf("baseline: mean %,.0f tasks/s | best %,.0f tasks/s%n",
                    sum / repetitions, best);
        }
    }

    /** Submits all tasks, waits for all of them, returns elapsed time and a checksum of the results. */
    private static RunResult run(ExecutorService pool, Workload workload, int taskCount) throws Exception {
        List<Future<Long>> futures = new ArrayList<>(taskCount);

        long start = System.nanoTime();
        for (int i = 0; i < taskCount; i++) {
            futures.add(pool.submit(workload.createTask(i)));
        }
        long checksum = 0;
        for (Future<Long> f : futures) {
            checksum ^= f.get(); // XOR: order-independent, so it is stable across runs
        }
        long elapsed = System.nanoTime() - start;

        return new RunResult(elapsed, checksum);
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