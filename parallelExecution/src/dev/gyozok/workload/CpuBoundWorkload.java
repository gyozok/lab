package dev.gyozok.workload;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

/**
 * Pure CPU work: repeated SHA-256 hashing. No I/O, no shared mutable state, no locks,
 * so the only thing limiting scalability is the number of cores.
 *
 * The cost of one task is controlled by {@code iterations}.
 */
public final class CpuBoundWorkload implements Workload {

    private final int iterations;

    public CpuBoundWorkload(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be > 0");
        }
        this.iterations = iterations;
    }

    @Override
    public String name() {
        return "cpu-bound(sha256 x" + iterations + ")";
    }

    @Override
    public Callable<Long> createTask(int taskId) {
        return () -> hashChain(taskId);
    }

    private long hashChain(int taskId) throws NoSuchAlgorithmException {
        // MessageDigest is NOT thread-safe, so every task gets its own instance.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = ByteBuffer.allocate(Long.BYTES).putLong(taskId).array();
        for (int i = 0; i < iterations; i++) {
            data = digest.digest(data); // output feeds the next round: no shortcuts for the JIT
        }
        return ByteBuffer.wrap(data).getLong();
    }
}