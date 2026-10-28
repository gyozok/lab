package dev.gyozok.basics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class MaxThreadSize {

    static void main() {
        var threadCount = new AtomicInteger(0);
        try {
            while (true) {
                Thread thread = Thread.ofPlatform().start(
                        () -> {
                            threadCount.incrementAndGet();
                            LockSupport.park();
                        }
                );
            }
        } catch (OutOfMemoryError error) {
            System.out.println("Reached thread limit: " + threadCount);
            error.printStackTrace();
        }
    }

}
