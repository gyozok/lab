package dev.gyozok.virtualthread.semaphore;

import java.time.Duration;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ResourcePool {
    private final Semaphore semaphore;

    public ResourcePool(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public Optional<String> useResource(String query) {
        try {
            semaphore.acquire();
            try {
                return queryDatabase(query);
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private Optional<String> queryDatabase(String query) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }

        return Optional.of("Result for: " + query);
    }

    static void main() {
        Semaphore s = new Semaphore(4);
        ResourcePool sut = new ResourcePool(s);
        System.out.println(sut.useResource("SQL query1"));
        System.out.println(sut.useResource("SQL query2"));
        System.out.println(sut.useResource("SQL query3"));
        System.out.println(sut.useResource("SQL query4"));
        System.out.println(sut.useResource("SQL query5"));
        System.out.println(sut.useResource("SQL query6"));
        System.out.println(sut.useResource("SQL query7"));
        System.out.println(sut.useResource("SQL query8"));
        System.out.println(sut.useResource("SQL query9"));
        System.out.println(sut.useResource("SQL query10"));
        System.out.println(sut.useResource("SQL query11"));
        System.out.println(sut.useResource("SQL query12"));
        System.out.println(sut.useResource("SQL query13"));
        System.out.println(sut.useResource("SQL query14"));
        System.out.println(sut.useResource("SQL query15"));
        System.out.println(sut.useResource("SQL query16"));
        System.out.println(sut.useResource("SQL query17"));
        System.out.println(sut.useResource("SQL query18"));
        System.out.println(sut.useResource("SQL query19"));
    }
}
