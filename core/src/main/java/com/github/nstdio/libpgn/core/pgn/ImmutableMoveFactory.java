package com.github.nstdio.libpgn.core.pgn;

import com.github.nstdio.libpgn.core.internal.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Produces the immutable and specialized {@code Move} instances.
 */
final class ImmutableMoveFactory extends MoveFactory {
    @Override
    Move createSingle(final byte[] move) {
        return new ImmutableSingleRawByteMove(move);
    }

    @Override
    Move createPlain(final byte[] move, final byte[] comment, final short[] nag, final List<MoveText> variations) {
        return new ImmutableRawByteMove(move, comment, nag, variations);
    }

    /**
     * The {@code Move} implementation with most often occurred data structure. This class make defensive copy of underlying array.
     */
    static class ImmutableSingleRawByteMove extends MutableMoveFactory.SingleRawByteMove {

        public ImmutableSingleRawByteMove(final byte[] move) {
            super(move);
        }

        @Nonnull
        @Override
        public byte[] move() {
            return ArrayUtils.copy(move);
        }
    }

    /**
     * The {@code Move} implementation that makes defencive copies.
     */
    static class ImmutableRawByteMove extends MutableMoveFactory.RawByteMove {

        public ImmutableRawByteMove(final byte[] move, final byte[] comment, final short[] nag, final List<MoveText> variations) {
            super(move, comment, nag, Collections.unmodifiableList(variations));
        }

        @Nonnull
        @Override
        public byte[] move() {
            return ArrayUtils.copy(move);
        }

        @Nonnull
        @Override
        public byte[] comment() {
            return ArrayUtils.copy(comment);
        }

        @Nonnull
        @Override
        public short[] nag() {
            return ArrayUtils.copy(nag);
        }
    }
}
