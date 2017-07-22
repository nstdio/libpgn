package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.Movetext;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Test whether input list contains {@code sequence} or not.
 */
abstract class MoveSequenceFilter implements Predicate<List<Movetext>> {
    final List<Movetext> sequence;

    MoveSequenceFilter(final List<Movetext> sequence) {
        this.sequence = Objects.requireNonNull(sequence, "sequence == null");
    }
}
