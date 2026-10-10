package dev.gyozok.workload;

import java.util.concurrent.Callable;

/**
 * Simulates blocking I/O work: some CPU, then a blocking wait (like network or disk).
 *
 * This is the case where the cores-based formula (N ≈ cores) fails:
 *   - With CPU-bound work: 2 cores × 2 threads is enough, more is wasted context switching.
 *   - With I/O-bound work: threads block, so you need many more threads to keep the cores busy.
 *
 * Formula: N_threads ≈ N_cores × (1 + W/C)
 *   where W = wait time, C = compute time
 *
 * Example: if wait time is 100ms and compute is 10ms, you need cores × (1 + 10) = cores × 11 threads.
 *
 * Virtual threads solve this by being lightweight: thousands can run without OS thread overhead.
 */
public final class BlockingIoWorkload implements Workload {

    private final int computeIterations;  // CPU work before blocking
    private final long blockTimeMillis;   // How long to sleep (simulating I/O)

    public BlockingIoWorkload(int computeIterations, long blockTimeMillis) {
        if (computeIterations <= 0 || blockTimeMillis <= 0) {
            throw new IllegalArgumentException("iterations and blockTime must be > 0");
        }
        this.computeIterations = computeIterations;
        this.blockTimeMillis = blockTimeMillis;
    }

    @Override
    public String name() {
        return String.format("blocking-io(compute=%d, sleep=%dms)", computeIterations, blockTimeMillis);
    }

    @Override
    public Callable<Long> createTask(int taskId) {
        return () -> doWork(taskId);
    }

    private long doWork(int taskId) throws InterruptedException {
        // Short CPU work: a few hash iterations
        long result = taskId;
        for (int i = 0; i < computeIterations; i++) {
            result = result * 31 + 17;  // simple computation
        }

        // Simulate blocking I/O: a network call, database query, or file read
        Thread.sleep(blockTimeMillis);

        // Return a value that depends on both compute and ID, so result can't be optimized away
        return result ^ System.nanoTime();
    }
}