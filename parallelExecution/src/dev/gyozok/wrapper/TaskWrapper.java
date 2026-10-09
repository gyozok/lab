package dev.gyozok.wrapper;

import dev.gyozok.metrics.MetricsCollector;

import java.util.concurrent.Callable;

/**
 * Wraps a Callable to measure its queue wait and execution time.
 *
 * Timeline:
 *   submit time (when TaskWrapper is created)
 *   ↓
 *   [queue wait]
 *   ↓
 *   start time (when call() is invoked)
 *   ↓
 *   [execution time]
 *   ↓
 *   end time (when call() returns)
 *
 * Queue wait = start - submit
 * Run time = end - start
 *
 * These timings are reported to the MetricsCollector.
 */
public class TaskWrapper<T> implements Callable<T> {

    private final Callable<T> delegate;
    private final MetricsCollector metrics;
    private final long submitTimeNanos;

    public TaskWrapper(Callable<T> delegate, MetricsCollector metrics) {
        this.delegate = delegate;
        this.metrics = metrics;
        this.submitTimeNanos = System.nanoTime();
    }

    @Override
    public T call() throws Exception {
        long startTimeNanos = System.nanoTime();
        long queueWaitNanos = startTimeNanos - submitTimeNanos;
        metrics.recordQueueWait(queueWaitNanos);

        long runStartNanos = System.nanoTime();
        try {
            return delegate.call();
        } finally {
            long runEndNanos = System.nanoTime();
            long runTimeNanos = runEndNanos - runStartNanos;
            metrics.recordRunTime(runTimeNanos);
        }
    }
}