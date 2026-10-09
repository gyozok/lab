package dev.gyozok.pool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Immutable configuration for a thread pool.
 *
 * Not all fields apply to all pool types:
 * - FIXED: uses corePoolSize, maxPoolSize, queueCapacity, rejectionPolicy
 * - FORK_JOIN: uses parallelism (derived from corePoolSize)
 * - VIRTUAL: ignores sizing, creates one thread per task
 */
public record PoolConfig(
        PoolType type,
        int corePoolSize,
        int maxPoolSize,
        int queueCapacity,
        RejectedExecutionHandler rejectionPolicy,
        String name
) {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for convenient PoolConfig creation with sensible defaults.
     */
    public static class Builder {
        private PoolType type = PoolType.FIXED;
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize = corePoolSize;
        private int queueCapacity = 10_000;
        private RejectedExecutionHandler rejectionPolicy = new ThreadPoolExecutor.AbortPolicy();
        private String name = "pool";

        public Builder type(PoolType type) {
            this.type = type;
            return this;
        }

        public Builder corePoolSize(int size) {
            this.corePoolSize = size;
            return this;
        }

        public Builder maxPoolSize(int size) {
            this.maxPoolSize = size;
            return this;
        }

        public Builder queueCapacity(int capacity) {
            this.queueCapacity = capacity;
            return this;
        }

        public Builder rejectionPolicy(RejectedExecutionHandler policy) {
            this.rejectionPolicy = policy;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public PoolConfig build() {
            return new PoolConfig(type, corePoolSize, maxPoolSize, queueCapacity, rejectionPolicy, name);
        }
    }

    @Override
    public String toString() {
        return switch (type) {
            case FIXED -> String.format("%s(fixed %d-%d, queue=%d)", name, corePoolSize, maxPoolSize, queueCapacity);
            case FORK_JOIN -> String.format("%s(fjp parallelism=%d)", name, corePoolSize);
            case VIRTUAL -> String.format("%s(virtual)", name);
        };
    }
}