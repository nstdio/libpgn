package com.github.nstdio.libpgn.core.pgn;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;

@EqualsAndHashCode(callSuper = true)
@ToString
final class RawByteTagPair extends AbstractByteTagPair {
    public RawByteTagPair(final byte[] tag, final byte[] value) {
        super(tag, value);
    }

    @Nonnull
    @Override
    public byte[] getTag() {
        return tag;
    }

    @Nonnull
    @Override
    public byte[] getValue() {
        return value;
    }
}
