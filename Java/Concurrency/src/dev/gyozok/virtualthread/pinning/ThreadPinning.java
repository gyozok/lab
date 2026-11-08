package dev.gyozok.virtualthread.pinning;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//This pinning behaviour was fixed in Java 24
//the printout identifier is same if you compile with earlier Java version
public class ThreadPinning {
    private static final Object lock = new Object();

    public static void main(String[] args) {
        List<Thread> threadList = IntStream.range(0, 10)
                .mapToObj(i -> Thread.ofVirtual().unstarted(
                        () -> {
                            if (i == 0) {
                                System.out.println(Thread.currentThread());
                            }
                            synchronized(lock) {
                                try {
                                    Thread.sleep(25);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                            if (i == 0) {
                                System.out.println(Thread.currentThread());
                            }
                        }
                ))
                .collect(Collectors.toList());

        threadList.forEach(Thread::start);
        threadList.forEach( t-> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
