package dev.gyozok.virtualthread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StartVirtualThread {

    static void main() throws InterruptedException, ExecutionException {
        Thread vThread = Thread.startVirtualThread(() -> System.out.println("Unleash massive parallelism with virtual threads! Here’s a taste."));
        vThread.join();

        Thread startedThread = Thread.ofVirtual().start(() -> System.out.println("Virtual threads make concurrency effortless! See for yourself."));
        startedThread.join();

        Thread unstartedThread = Thread.ofVirtual().unstarted(() -> System.out.println("Hello World"));
        System.out.println("Unstarted thread is created");
        Thread.sleep(1000);
        unstartedThread.start();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> "Callable service");
            System.out.println(future.get());
        }
    }
}
