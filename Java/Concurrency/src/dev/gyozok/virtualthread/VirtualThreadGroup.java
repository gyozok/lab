package dev.gyozok.virtualthread;

import java.util.HashSet;
import java.util.Set;

public class VirtualThreadGroup {

    static void main() throws InterruptedException {
        Set<ThreadGroup> threadGroups = new HashSet<>();

        //create 100 virtual thread
        for (int i=0; i<100; i++) {
            Thread vThread = Thread.ofVirtual().start(() -> {
               try {
                   Thread.sleep(10);
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               }
            });
            threadGroups.add(vThread.getThreadGroup());

        }

        Thread.sleep(1000);
        System.out.println("Unique thread groups: " +threadGroups.size());
        System.out.println("Thread group: " + threadGroups.iterator().next());
    }
}
