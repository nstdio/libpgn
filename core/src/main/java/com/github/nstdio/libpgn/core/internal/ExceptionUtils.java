package com.github.nstdio.libpgn.core.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionUtils {

    public static <T> T wrapChecked(final ThrowingSupplier<T> throwsIO) {
        try {
            return throwsIO.get();
        } catch (RuntimeException e) {
            throw tryConvertUncheckedIO(e);
        }
    }

    public static void wrapChecked(final ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.run();
        } catch (RuntimeException e) {
            throw tryConvertUncheckedIO(e);
        }
    }

    @Nonnull
    private static RuntimeException tryConvertUncheckedIO(final RuntimeException e) {
        return Optional.ofNullable(e.getCause())
                .filter(IOException.class::isInstance)
                .<RuntimeException>map(th -> new UncheckedIOException((IOException) th))
                .orElse(e);
    }
}
