package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.Movetext;

import java.util.List;
import java.util.function.Predicate;

class OpeningFilter implements Predicate<List<Movetext>> {
    private final List<Movetext> expected;

    OpeningFilter(final List<Movetext> expected) {
        this.expected = expected;
    }

    @Override
    public boolean test(final List<Movetext> input) {
        final int inputSize = input.size();
        final int expectedSize = expected.size();

        if (inputSize < expectedSize) {
            return false;
        }

        final int size = Math.min(expectedSize, inputSize);

        for (int i = 0; i < size; i++) {
            final Movetext movetext = expected.get(i);
            final Movetext obj = input.get(i);

            if (movetext.moveNo() != obj.moveNo()) {
                return false;
            }

            if (!movetext.whiteMove().equals(obj.whiteMove())) {
                return false;
            }

            if (movetext.blackMove() != null && obj.blackMove() != null && !movetext.blackMove().equals(obj.blackMove())) {
                return false;
            }
        }

        return true;
    }
}
