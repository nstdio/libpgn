package com.github.nstdio.libpgn.core;

import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.core.parser.PgnParser;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.Result;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class GameIteratorTest {

    @Test
    public void hasNextAndNextCombination() {
        final GameIterator gameIterator = create("1. d4 *\n\n1. d5 *");

        assertThat(gameIterator.hasNext()).isTrue();
        assertThat(gameIterator.next()).isNotNull();

        assertThat(gameIterator.hasNext()).isTrue();
        assertThat(gameIterator.next()).isNotNull();

        assertThat(gameIterator.hasNext()).isFalse();

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(gameIterator::next);
    }

    @Test
    public void hasNextNotAffectingIteratorPosition() {
        final GameIterator gameIterator = create("1. d4 d5*\n\n1. d5 1-0");

        assertThat(gameIterator.hasNext()).isTrue();
        assertThat(gameIterator.hasNext()).isTrue();

        final Game next = gameIterator.next();
        assertThat(next.moves().get(0).white())
                .isPresent()
                .hasValue(Move.of("d4"));
        assertThat(next.moves().get(0).black())
                .isPresent()
                .hasValue(Move.of("d5"));
        assertThat(next.gameResult()).isEqualTo(Result.UNKNOWN);

        IntStream.range(0, 100).forEach(value -> assertThat(gameIterator.hasNext()).isTrue());

        final Game next1 = gameIterator.next();

        assertThat(next1.moves().get(0).white())
                .isPresent()
                .hasValue(Move.of("d5"));
        assertThat(next1.gameResult()).isEqualTo(Result.WHITE);
    }

    private GameIterator create(final String pgn) {
        return new GameIterator(new PgnParser(InputStreamPgnLexer.of(pgn.getBytes())));
    }
}