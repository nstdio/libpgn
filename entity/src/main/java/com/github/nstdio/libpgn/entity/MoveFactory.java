package com.github.nstdio.libpgn.entity;

import com.github.nstdio.libpgn.common.CollectionUtils;

import java.util.List;

import static com.github.nstdio.libpgn.common.ArrayUtils.isEmptyOrNull;
import static com.github.nstdio.libpgn.common.ArrayUtils.nullToEmpty;
import static com.github.nstdio.libpgn.common.CollectionUtils.isEmpty;
import static com.github.nstdio.libpgn.common.Preconditions.checkArgumentSize;

/**
 * Produces {@code Move} instances with various behavior.
 */
abstract class MoveFactory {
    final Move create(byte[] move, byte[] comment, short[] nag, List<MoveText> variations) {
        checkArgumentSize(move, 2, "move size must be greater then 2");
        return canUseSingle(comment, nag, variations) ?
                createSingle(move) :
                createPlain(move, nullToEmpty(comment), nullToEmpty(nag), CollectionUtils.nullToEmpty(variations));
    }

    abstract Move createSingle(byte[] move);

    abstract Move createPlain(byte[] move, byte[] comment, short[] nag, List<MoveText> variations);

    boolean canUseSingle(byte[] comment, short[] nag, List<MoveText> variations) {
        return isEmptyOrNull(comment) && isEmptyOrNull(nag) && isEmpty(variations);
    }

    /**
     * Holds shared stateless factory objects.
     */
    static class Holder {
        static final MoveFactory MUTABLE = new MutableMoveFactory();
        static final MoveFactory IMMUTABLE = new ImmutableMoveFactory();
    }
}
