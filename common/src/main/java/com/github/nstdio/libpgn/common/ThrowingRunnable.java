package com.github.nstdio.libpgn.common;

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
