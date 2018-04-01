package com.github.nstdio.libpgn.common;

/**
 * The Guava style.
 */
public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgumentNotEmpty(final byte[] bytes, final String message) {
        checkArgumentSize(bytes, 0, message);
    }

    public static void checkArgumentSize(final byte[] bytes, final int size, final String message) {
        checkArgument(ArrayUtils.sizeGe(bytes, size), message);
    }

    public static void checkArgument(final boolean expected, final String message) {
        if (!expected)
            throw new IllegalArgumentException(message);
    }
}
