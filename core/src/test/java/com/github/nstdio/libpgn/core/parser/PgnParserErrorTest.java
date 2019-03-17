package com.github.nstdio.libpgn.core.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PgnParserErrorTest {

    private InputStreamPgnLexer createLexer(final String input) {
        return InputStreamPgnLexer.of(input.getBytes());
    }

    @Nested
    class TagPair {
        @Test
        void openingBracketIsMissing() {
            new PgnParser(createLexer("Event \"Leipzig8990 m\"]\n 1. d4 *"));
        }
    }
}