package dev.gyozok.pool;

/**
 * Types of thread pool strategies to benchmark.
 */
public enum PoolType {
    /**
     * Classic ThreadPoolExecutor with configurable core/max size and queue capacity.
     * Good baseline for traditional thread pools.
     */
    FIXED,

    /**
     * ForkJoinPool: work-stealing, ideal for divide-and-conquer tasks.
     * Less configurable than ThreadPoolExecutor but better for uneven workloads.
     */
    FORK_JOIN,

    /**
     * Virtual thread executor (Java 21+): one lightweight virtual thread per task,
     * no pool sizing needed. Ideal for I/O-bound work.
     */
    VIRTUAL
}