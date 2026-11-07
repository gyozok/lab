package dev.gyozok.virtualthread.semaphore;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourcePoolWithTimeout {

    private final Semaphore semaphore;
    private final AtomicInteger activeConnections;
    private final AtomicInteger peakConnections;

    public ResourcePoolWithTimeout(int resourceCount) {
        this.semaphore = new Semaphore(resourceCount, true);
        this.activeConnections = new AtomicInteger(0);
        this.peakConnections = new AtomicInteger(0);
    }

    public Optional<String> useResource(String query) {
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(5, TimeUnit.SECONDS);
            if (!acquired) {
                return Optional.empty();
            }
            int current = activeConnections.incrementAndGet();
            peakConnections.updateAndGet(peak -> Math.max(peak, current));

            return queryDatabase(query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            if (acquired) {
                acquired = false;
                activeConnections.decrementAndGet();
                semaphore.release();
            }
        }
    }

    public int getCurrentActiveConnections() {
        return activeConnections.get();
    }

    public int getPeakConnections() {
        return peakConnections.get();
    }

    private Optional<String> queryDatabase(String query) {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500) + 600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }

        return Optional.of("Result for: " + query);
    }
}
