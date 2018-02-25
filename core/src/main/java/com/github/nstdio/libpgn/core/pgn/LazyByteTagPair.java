package com.github.nstdio.libpgn.core.pgn;

import javax.annotation.Nonnull;

final class LazyByteTagPair implements TagPair {
    private final String tag;
    private final String value;

    private byte[] tagBytes;
    private byte[] valueBytes;

    LazyByteTagPair(final String tag, final String value) {
        this.tag = tag;
        this.value = value;
    }

    @Nonnull
    @Override
    public byte[] getTag() {
        if (tagBytes == null) {
            return tagBytes = tag.getBytes();
        }

        return tagBytes;
    }

    @Nonnull
    @Override
    public byte[] getValue() {
        if (valueBytes == null) {
            return valueBytes = value.getBytes();
        }

        return valueBytes;
    }

    @Override
    public String getTagAsString() {
        return tag;
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}
