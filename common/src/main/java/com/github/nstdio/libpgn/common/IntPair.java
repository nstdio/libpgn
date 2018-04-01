package com.github.nstdio.libpgn.common;

public final class IntPair {
    public final int first;
    public final int second;

    private IntPair(final int first, final int second) {
        this.first = first;
        this.second = second;
    }

    public static IntPair of(final int first, final int second) {
        return new IntPair(first, second);
    }

    public int sum() {
        return first + second;
    }
}
