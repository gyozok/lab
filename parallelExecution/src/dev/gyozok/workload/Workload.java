package dev.gyozok.workload;

import java.util.concurrent.Callable;

/**
 * A workload is a factory of tasks with a known profile (CPU-bound, blocking I/O, mixed).
 * It is the "variable under test": the pool configuration changes, the workload stays fixed.
 *
 * Tasks return a long so the JIT cannot eliminate the work as dead code,
 * and so a checksum can be used to verify that every task really ran.
 */
public interface Workload {

    /** Human-readable name, used in reports. */
    String name();

    /** Creates the task with the given id. Must be thread-safe and deterministic per id. */
    Callable<Long> createTask(int taskId);
}