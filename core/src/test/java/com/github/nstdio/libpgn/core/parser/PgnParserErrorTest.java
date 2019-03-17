package com.github.nstdio.libpgn.core.parser;

import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThatPgnSyntaxException;

import java.io.EOFException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.nstdio.libpgn.entity.Game;

class PgnParserErrorTest {

    private static InputStreamPgnLexer createLexer(final String input) {
        return InputStreamPgnLexer.of(input.getBytes());
    }

    private static PgnParser createParser(final String input) {
        return new PgnParser(createLexer(input));
    }

    private static Game parseSingle(final String input) {
        return createParser(input).next();
    }

    @Nested
    class TagPair {
        @Test
        void openingBracketIsMissing() {
            assertThatPgnSyntaxException()
                    .isThrownBy(() -> parseSingle("Event \"Leipzig8990 m\"]\n 1. d4 *"))
                    .withMessageEndingWith("Unexpected [MOVE_WHITE] token occurred, expected was [TP_BEGIN, MOVE_NUMBER, COMMENT_BEGIN].");
        }

        @Test
        void nameIsMissing() {
            assertThatPgnSyntaxException()
                    .isThrownBy(() -> parseSingle("[\"Leipzig8990 m\"]\n 1. d4 *"))
                    .withMessageEndingWith("Unexpected [TP_VALUE_BEGIN] token occurred, expected was [TP_NAME].");
        }

        @Test
        void nameBeginningQuoteIsMissing() {
            assertThatPgnSyntaxException()
                    .isThrownBy(() -> parseSingle("[Event Leipzig8990 m\"]\n 1. d4 *"))
                    .withMessageEndingWith("Unexpected [TP_VALUE] token occurred, expected was [TP_VALUE_BEGIN].");
        }

        @Test
        void nameEndingQuoteIsMissing() {
            assertThatPgnSyntaxException()
                    .isThrownBy(() -> parseSingle("[Event \"Leipzig8990 m]\n 1. d4 *"))
                    .withCauseInstanceOf(EOFException.class)
                    .withMessageEndingWith("Unexpected end of input! Expecting [TP_VALUE_BEGIN, TP_VALUE_END], but got EOF!");
        }
    }
}