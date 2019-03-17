package com.github.nstdio.libpgn.core.assertj;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;
import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.io.PgnInputStreamFactory;

import java.io.ByteArrayInputStream;

import org.assertj.core.api.ThrowableTypeAssert;

public class Assertions {
    public static PgnLexerAssert assertThatLexer(final String input) {
        return assertThatLexer(new InputStreamPgnLexer(PgnInputStreamFactory.of(new ByteArrayInputStream(input.getBytes()))));
    }

    public static PgnLexerAssert assertThatLexer(final InputStreamPgnLexer lexer) {
        return new PgnLexerAssert(lexer);
    }

    public static ThrowableTypeAssert<PgnSyntaxException> assertThatPgnSyntaxException() {
        return assertThatExceptionOfType(PgnSyntaxException.class);
    }
}