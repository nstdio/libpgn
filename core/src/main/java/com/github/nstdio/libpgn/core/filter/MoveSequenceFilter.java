package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.pgn.MoveText;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Test whether input list contains {@code sequence} or not.
 */
abstract class MoveSequenceFilter implements Predicate<List<MoveText>> {
    final List<MoveText> sequence;

    MoveSequenceFilter(final List<MoveText> sequence) {
        this.sequence = Objects.requireNonNull(sequence, "sequence == null");
    }
}
