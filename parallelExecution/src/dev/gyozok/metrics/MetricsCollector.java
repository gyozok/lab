package dev.gyozok.metrics;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Thread-safe collector for task latency metrics: queue wait, run time, and percentiles.
 *
 * Uses synchronized lists for latencies (acceptable for modest task counts)
 * and atomics for counters.
 *
 * Percentiles are computed on-demand when {@link #snapshot()} is called.
 */
public class MetricsCollector {

    private final List<Long> queueWaitNanos = Collections.synchronizedList(new ArrayList<>());
    private final List<Long> runTimeNanos = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong taskCount = new AtomicLong(0);
    private final AtomicLong rejectedCount = new AtomicLong(0);
    private final LongAdder totalQueueWaitNanos = new LongAdder();
    private final LongAdder totalRunTimeNanos = new LongAdder();
    private final long startTimeNanos;

    public MetricsCollector() {
        this.startTimeNanos = System.nanoTime();
    }

    /** Called by TaskWrapper when a task enters the queue. */
    public void recordQueueWait(long nanos) {
        queueWaitNanos.add(nanos);
        totalQueueWaitNanos.add(nanos);
    }

    /** Called by TaskWrapper when a task finishes execution. */
    public void recordRunTime(long nanos) {
        runTimeNanos.add(nanos);
        totalRunTimeNanos.add(nanos);
        taskCount.incrementAndGet();
    }

    /** Called when a task is rejected by the executor. */
    public void recordRejected() {
        rejectedCount.incrementAndGet();
    }

    /** Return a frozen snapshot of metrics at this instant. */
    public Snapshot snapshot() {
        long elapsed = System.nanoTime() - startTimeNanos;
        long completed = taskCount.get();
        long rejected = rejectedCount.get();

        // Make a copy to compute percentiles without holding the lock.
        List<Long> queueWaitCopy = new ArrayList<>(queueWaitNanos);
        List<Long> runTimeCopy = new ArrayList<>(runTimeNanos);

        Collections.sort(queueWaitCopy);
        Collections.sort(runTimeCopy);

        double queueP50 = percentile(queueWaitCopy, 0.50);
        double queueP90 = percentile(queueWaitCopy, 0.90);
        double queueP99 = percentile(queueWaitCopy, 0.99);

        double runP50 = percentile(runTimeCopy, 0.50);
        double runP90 = percentile(runTimeCopy, 0.90);
        double runP99 = percentile(runTimeCopy, 0.99);

        double throughput = completed > 0 ? (completed * 1e9) / elapsed : 0;

        com.sun.management.OperatingSystemMXBean osBean =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getProcessCpuLoad();

        return new Snapshot(
                elapsed,
                completed,
                rejected,
                throughput,
                queueP50, queueP90, queueP99,
                runP50, runP90, runP99,
                totalQueueWaitNanos.sum(),
                totalRunTimeNanos.sum(),
                cpuLoad
        );
    }

    /** Compute the p-th percentile of a sorted list. Uses linear interpolation. */
    private static double percentile(List<Long> sorted, double p) {
        if (sorted.isEmpty()) {
            return 0;
        }
        if (sorted.size() == 1) {
            return sorted.get(0);
        }
        double rank = p * (sorted.size() - 1);
        int lower = (int) Math.floor(rank);
        int upper = (int) Math.ceil(rank);
        double fraction = rank - lower;
        return sorted.get(lower) * (1 - fraction) + sorted.get(upper) * fraction;
    }

    /**
     * Immutable snapshot of metrics at a point in time.
     */
    public record Snapshot(
            long elapsedNanos,
            long completedTasks,
            long rejectedTasks,
            double throughputTasksPerSecond,
            double queueWaitP50Nanos, double queueWaitP90Nanos, double queueWaitP99Nanos,
            double runTimeP50Nanos, double runTimeP90Nanos, double runTimeP99Nanos,
            long totalQueueWaitNanos,
            long totalRunTimeNanos,
            double processedCpuLoad) {

        public void print(String label) {
            System.out.printf("%n=== %s ===%n", label);
            System.out.printf("elapsed: %.3f s%n", elapsedNanos / 1e9);
            System.out.printf("completed: %,d | rejected: %,d%n", completedTasks, rejectedTasks);
            System.out.printf("throughput: %,.0f tasks/s%n", throughputTasksPerSecond);
            System.out.printf("cpu load: %.1f%%%n", processedCpuLoad * 100);
            System.out.printf("queue wait (ms): p50=%.2f p90=%.2f p99=%.2f%n",
                    queueWaitP50Nanos / 1e6, queueWaitP90Nanos / 1e6, queueWaitP99Nanos / 1e6);
            System.out.printf("run time (ms):   p50=%.2f p90=%.2f p99=%.2f%n",
                    runTimeP50Nanos / 1e6, runTimeP90Nanos / 1e6, runTimeP99Nanos / 1e6);
        }
    }
}