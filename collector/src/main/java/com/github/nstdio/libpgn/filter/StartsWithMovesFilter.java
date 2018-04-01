package com.github.nstdio.libpgn.filter;

import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tests that input list is exactly matching with
 */
class StartsWithMovesFilter extends MoveSequenceFilter {
    StartsWithMovesFilter(final List<MoveText> sequence) {
        super(sequence);
    }

    @Override
    public boolean test(final List<MoveText> input) {
        final int inputSize = input.size();
        final int expectedSize = sequence.size();

        if (inputSize < expectedSize) {
            return false;
        }

        final int size = Math.min(expectedSize, inputSize);

        for (int i = 0; i < size; i++) {
            final MoveText movetext = sequence.get(i);
            final MoveText obj = input.get(i);

            if (movetext.moveNo() != obj.moveNo()) {
                return false;
            }

            if (!movesEqual(movetext.white(), obj.white())) {
                return false;
            }

            if (!movesEqual(movetext.black(), obj.black())) {
                return false;
            }
        }

        return true;
    }

    private boolean movesEqual(final Optional<Move> first, final Optional<Move> second) {
        return first.map(Move::move)
                .filter(bytes -> Arrays.equals(bytes, second.map(Move::move).orElse(null)))
                .isPresent();
    }
}
