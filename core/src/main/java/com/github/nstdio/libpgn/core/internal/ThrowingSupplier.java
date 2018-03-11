package com.github.nstdio.libpgn.core.internal;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T> extends Supplier<T> {
    @Override
    default T get() {
        try {
            return getThrows();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    T getThrows() throws Throwable;
}
