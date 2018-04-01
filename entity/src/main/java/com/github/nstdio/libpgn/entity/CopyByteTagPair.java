package com.github.nstdio.libpgn.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@ToString
final class CopyByteTagPair extends AbstractByteTagPair {
    public CopyByteTagPair(final byte[] tag, final byte[] value) {
        super(tag, value);
    }

    @Nonnull
    @Override
    public byte[] getTag() {
        return Arrays.copyOf(tag, tag.length);
    }

    @Nonnull
    @Override
    public byte[] getValue() {
        return Arrays.copyOf(value, value.length);
    }
}
