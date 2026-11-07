package dev.gyozok.virtualthread.semaphore;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResourcePoolWithTimeoutTest {

    static void main(String[] args) throws Exception {
        int maxConcurrentThreads = 10;
        int totalRequests = 150;
        var pool = new ResourcePoolWithTimeout(maxConcurrentThreads);
        var futures = new ArrayList<Future<Optional<String>>>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < totalRequests; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> pool.useResource("Query " + taskId)));
            }
            int successCount = 0;
            int timeoutCount = 0;
            for (Future<Optional<String>> future : futures) {
                Optional<String> result = future.get();
                if (result.isPresent()) {
                    successCount++;
                } else {
                    timeoutCount++;
                }
            }
            System.out.printf("""
                requests : %d
                successful: %d
                timed-out : %d
                peak usage: %d%n""",
                    totalRequests, successCount, timeoutCount, pool.getPeakConnections());
        }

        assert pool.getPeakConnections() <= maxConcurrentThreads
                : "Peak connections exceeded limit!";
    }
}
