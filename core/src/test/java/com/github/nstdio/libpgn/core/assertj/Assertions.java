package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.io.PgnInputStreamFactory;

import java.io.ByteArrayInputStream;

public class Assertions {
    public static PgnLexerAssert assertThatLexer(final String input) {
        return assertThatLexer(new InputStreamPgnLexer(PgnInputStreamFactory.of(new ByteArrayInputStream(input.getBytes()))));
    }

    public static PgnLexerAssert assertThatLexer(final InputStreamPgnLexer lexer) {
        return new PgnLexerAssert(lexer);
    }
}