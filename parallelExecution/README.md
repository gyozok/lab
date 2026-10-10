# Parallel execution banchmark

## Architecture
```
                 ┌──────────────────┐
                 │  BenchmarkRunner │  (orchestrates experiments)
                 └───────┬──────────┘
        picks config     │      runs
   ┌─────────────────────┼──────────────────────┐
   ▼                     ▼                      ▼
PoolConfig ──► PoolFactory ──► ExecutorService   Workload (interface)
(type, size,   (builds:         ├ ThreadPoolExecutor   ├ CpuBoundWorkload
 queue, policy) fixed / FJP /   ├ ForkJoinPool         ├ BlockingIoWorkload
                virtual)        └ newVirtualThread…    └ MixedWorkload
                                     │                       │
                                     │ executes tasks        │ records via
                                     ▼                       ▼
                               TaskWrapper ─────────► MetricsCollector
                          (timestamps submit/start/end)  (LongAdder counters,
                                                          latency histogram)
                                                              │
                                    ┌─────────────────────────┤
                                    ▼                         ▼
                              ResultReporter            Tuner (optional)
                              (table/CSV)        (sweeps sizes or adapts
                                                  pool size from metrics)
```

## Responsibilities
- Workload: Runnable/Callable factory with a known profile (CPU, I/O, mixed). It is the variable under test.
- PoolConfig: an immutable record (type, core and max size, queue capacity, rejection policy).
- PoolFactory: turns a PoolConfig into an ExecutorService with named threads via ThreadFactory.
- TaskWrapper: decorates each task to record queue wait (start − submit) and run time (end − start).
- MetricsCollector: thread-safe stats: throughput, p50/p95/p99 latency, queue depth, rejected count, and CPU utilization via OperatingSystemMXBean.
- BenchmarkRunner: warmup, then measure, then shutdown; loops over configs and workloads.
- Tuner: the “optimizing” part. Start with a brute-force sweep (sizes 1 to 4×cores) and pick the knee of the throughput curve. An adaptive version can grow or shrink setCorePoolSize() at runtime based on queue depth and CPU use.
- ResultReporter: prints a comparison table so you can see the Amdahl/USL curve for your machine.

## What the application does
- Generates workloads. It creates thousands or millions of small tasks of a known type: CPU-heavy (e.g. hashing), blocking I/O (simulated with Thread.sleep), or a mix of both.
- Runs them on different pool setups. The same workload is executed on a fixed ThreadPoolExecutor, a ForkJoinPool, and a virtual-thread executor, with varying thread counts, queue sizes, and rejection policies.
- Measures what happens. For each run it records throughput, latency percentiles (p50/p95/p99), time spent waiting in the queue, rejected tasks, and CPU utilization.

## Implementation
- ~~Workload + CpuBoundWorkload and a single fixed pool to get a baseline.~~
- ~~TaskWrapper + MetricsCollector, so you can measure before you optimize.~~
- ~~PoolFactory with three strategies: fixed ThreadPoolExecutor, ForkJoinPool, and Executors.newVirtualThreadPerTaskExecutor().~~
- ~~BlockingIoWorkload (Thread.sleep) to show where the cores-based formula fails and virtual threads win.~~
- BenchmarkRunner sweep plus ResultReporter.
- Optional adaptive Tuner, and a bounded queue with backpressure experiment.