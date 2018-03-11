package com.github.nstdio.libpgn.core.internal;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {
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
