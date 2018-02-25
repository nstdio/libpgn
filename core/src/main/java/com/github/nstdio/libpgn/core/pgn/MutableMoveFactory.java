package com.github.nstdio.libpgn.core.pgn;

import com.github.nstdio.libpgn.core.internal.ArrayUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Produces mutable instances of {@code Move}.
 */
final class MutableMoveFactory extends MoveFactory {
    @Override
    Move createSingle(final byte[] move) {
        return new SingleRawByteMove(move);
    }

    @Override
    Move createPlain(final byte[] move, final byte[] comment, final short[] nag, final List<MoveText> variations) {
        return new RawByteMove(move, comment, nag, variations);
    }

    /**
     * The {@code Move} implementation with most often occurred data structure. This class exposes array without any
     * defensive copies.
     */
    @Accessors(fluent = true)
    @Data
    static class SingleRawByteMove implements Move {
        final byte[] move;

        @Nonnull
        @Override
        public byte[] comment() {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        @Nonnull
        @Override
        public short[] nag() {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }

        @Nonnull
        @Override
        public List<MoveText> variations() {
            return Collections.emptyList();
        }
    }

    /**
     * The full representation of move.
     */
    @Accessors(fluent = true)
    @Data
    static class RawByteMove implements Move {
        final byte[] move;
        final byte[] comment;
        final short[] nag;
        final List<MoveText> variations;
    }
}
