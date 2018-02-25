package com.github.nstdio.libpgn.core.internal;

public final class ArrayUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private ArrayUtils() {
    }

    public static byte[] concat(final byte[] first, final byte[] second) {
        if (isEmptyOrNull(first)) {
            return second;
        }

        if (isEmptyOrNull(second)) {
            return first;
        }

        final byte[] result = new byte[first.length + second.length];

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }

    public static byte[] copy(final byte[] bytes) {
        return bytes == EMPTY_BYTE_ARRAY ? EMPTY_BYTE_ARRAY : bytes.clone();
    }

    public static short[] copy(final short[] shorts) {
        return shorts == EMPTY_SHORT_ARRAY ? EMPTY_SHORT_ARRAY : shorts.clone();
    }

    public static boolean isEmptyOrNull(final byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    public static boolean isEmptyOrNull(final short[] shorts) {
        return shorts == null || shorts.length == 0;
    }

    public static boolean isNotEmptyOrNull(final byte[] bytes) {
        return !isEmptyOrNull(bytes);
    }

    public static byte[] nullToEmpty(final byte[] bytes) {
        return bytes == null ? EMPTY_BYTE_ARRAY : bytes;
    }

    public static boolean sizeGe(final byte[] bytes, final int size) {
        return bytes != null && bytes.length >= size;
    }

    public static short[] nullToEmpty(final short[] shorts) {
        return shorts == null ? EMPTY_SHORT_ARRAY : shorts;
    }
}
