package dev.gyozok.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory for creating different types of thread pools based on a PoolConfig.
 *
 * Provides three strategies:
 * 1. FIXED: traditional ThreadPoolExecutor with configurable sizing and queue
 * 2. FORK_JOIN: work-stealing ForkJoinPool
 * 3. VIRTUAL: lightweight virtual-thread executor (one thread per task)
 */
public class PoolFactory {

    /**
     * Create an ExecutorService according to the given configuration.
     */
    public static ExecutorService create(PoolConfig config) {
        return switch (config.type()) {
            case FIXED -> createFixedThreadPool(config);
            case FORK_JOIN -> createForkJoinPool(config);
            case VIRTUAL -> createVirtualThreadPool(config);
        };
    }

    /**
     * Traditional ThreadPoolExecutor with a bounded or unbounded queue.
     */
    private static ExecutorService createFixedThreadPool(PoolConfig config) {
        ThreadFactory threadFactory = namedThreads(config.name());
        var queue = new LinkedBlockingQueue<Runnable>(config.queueCapacity());

        return new ThreadPoolExecutor(
                config.corePoolSize(),
                config.maxPoolSize(),
                0L,
                TimeUnit.MILLISECONDS,
                queue,
                threadFactory,
                config.rejectionPolicy()
        );
    }

    /**
     * ForkJoinPool with configurable parallelism.
     * Uses the corePoolSize as the parallelism level.
     */
    private static ExecutorService createForkJoinPool(PoolConfig config) {
        ThreadFactory threadFactory = namedThreads(config.name());
        return new ForkJoinPool(
                config.corePoolSize(),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,  // no uncaught exception handler
                false  // asyncMode = false (FIFO, not LIFO work stealing)
        );
    }

    /**
     * Virtual thread executor: creates a new lightweight virtual thread per task.
     * No explicit pool size—the JVM handles creation and scheduling.
     * Ideal for I/O-bound or mixed workloads where thousands of threads are acceptable.
     */
    private static ExecutorService createVirtualThreadPool(PoolConfig config) {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Creates a ThreadFactory that names threads with a prefix and counter.
     */
    private static ThreadFactory namedThreads(String prefix) {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> {
            Thread t = new Thread(runnable, prefix + "-" + counter.incrementAndGet());
            t.setDaemon(false);
            return t;
        };
    }
}