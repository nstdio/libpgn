package com.github.nstdio.libpgn.common;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {
    static ThrowingRunnable empty() {
        return () -> {
        };
    }

    @Override
    default void run() {
        try {
            runThrowing();
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    void runThrowing() throws Throwable;
}