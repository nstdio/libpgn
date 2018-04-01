package com.github.nstdio.libpgn.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@AllArgsConstructor
@EqualsAndHashCode
final class StringTagPair implements TagPair {
    private final String tag;
    private final String value;

    @Override
    public String getTagAsString() {
        return tag;
    }

    @Override
    public String getValueAsString() {
        return value;
    }

    @Nonnull
    @Override
    public byte[] getTag() {
        return tag.getBytes();
    }

    @Nonnull
    @Override
    public byte[] getValue() {
        return value.getBytes();
    }
}
