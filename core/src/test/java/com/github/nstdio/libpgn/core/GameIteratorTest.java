package com.github.nstdio.libpgn.core;

import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.core.parser.PgnParser;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThat;
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

        assertThat(gameIterator.next())
                .isWhiteMoveEqualTo(1, "d4")
                .isBlackMoveEqualTo(1, "d5")
                .isResultUnknown();

        IntStream.range(0, 100).forEach(value -> assertThat(gameIterator.hasNext()).isTrue());

        assertThat(gameIterator.next())
                .isWhiteMoveEqualTo(1, "d5")
                .result()
                .isWhiteWin();
    }

    private GameIterator create(final String pgn) {
        return new GameIterator(new PgnParser(InputStreamPgnLexer.of(pgn.getBytes())));
    }
}