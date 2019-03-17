package com.github.nstdio.libpgn.core.parser;

import java.util.StringJoiner;

import javax.annotation.Nonnull;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;

class ExceptionBuilder {
    static PgnSyntaxException syntaxException(PgnLexer lexer, byte actualToken, byte... expectedTokens) {
        String message = String.format("Unexpected [%s] token occurred, expected was %s.",
                TokenTypes.descOf(actualToken), description(expectedTokens).toString());

        return new PgnSyntaxException(message, 0, 0);
    }

    @Nonnull
    private static StringJoiner description(byte[] expectedTokens) {
        StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");

        for (byte expectedToken : expectedTokens) {
            stringJoiner.add(TokenTypes.descOf(expectedToken));
        }

        return stringJoiner;
    }

    static PgnSyntaxException unexpectedEOF(PgnLexer lexer, Throwable cause, byte... expectedTokens) {
        String message = String.format("Unexpected end of input! Expecting %s, but got EOF!",
                description(expectedTokens).toString());

        return new PgnSyntaxException(message, cause, 0, 0);
    }
}
