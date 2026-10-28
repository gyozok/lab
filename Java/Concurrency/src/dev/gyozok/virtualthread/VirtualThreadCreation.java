package dev.gyozok.virtualthread;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class VirtualThreadCreation {

    static void main() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000_000)
                    .forEach(i -> {
                        executor.submit( () -> {
                            Thread.sleep(Duration.ofSeconds(1));
                            System.out.println(i);
                            return i;
                        }
                        );
                    });
        }

        System.out.println("All virtual thread has finished");

        try (var executor = Executors.newFixedThreadPool(20)) {
            IntStream.range(0, 1_000)
                    .forEach(i -> {
                        executor.submit( () -> {
                                    Thread.sleep(Duration.ofSeconds(1));
                                    System.out.println(i);
                                    return i;
                                }
                        );
                    });
        }
    }
}
