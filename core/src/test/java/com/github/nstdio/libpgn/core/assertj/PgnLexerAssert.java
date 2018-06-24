package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.description.Description;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class PgnLexerAssert extends AbstractAssert<PgnLexerAssert, InputStreamPgnLexer> {
    public PgnLexerAssert(final InputStreamPgnLexer lexer) {
        super(lexer, PgnLexerAssert.class);
    }

    public PgnLexerAssert lastTokenIsEqualTo(final byte lastToken) {
        assertThat(actual.last()).isEqualTo(lastToken);
        return this;
    }

    public PgnLexerAssert nextTokenIsEqualTo(final byte expectedToken) {
        final byte actualToken = this.actual.next();
        assertThat(actualToken)
                .as("Actual: %s, Expected: %s", TokenTypes.descOf(actualToken), TokenTypes.descOf(expectedToken))
                .isEqualTo(expectedToken);
        return this;
    }

    public PgnLexerAssert readIsEqualTo(final String expected) {
        final byte[] expectedBytes = expected.getBytes();

        assertThat(actual.read())
                .hasSameSizeAs(expectedBytes)
                .containsExactly(expectedBytes);

        return this;
    }

    public PgnLexerAssert commentReadIsEqualTo(final String expectedComment) {
        nextTokenIsEqualTo(TokenTypes.COMMENT_BEGIN).readIsEqualTo("{")
                .nextTokenIsEqualTo(TokenTypes.COMMENT).readIsEqualTo(expectedComment)
                .nextTokenIsEqualTo(TokenTypes.COMMENT_END).readIsEqualTo("}");

        return this;
    }

    public PgnLexerAssert tagPairReadIsEqualTo(final String name, final String value) {
        nextTokenIsEqualTo(TokenTypes.TP_BEGIN).readIsEqualTo("[")
                .nextTokenIsEqualTo(TokenTypes.TP_NAME).readIsEqualTo(name)
                .nextTokenIsEqualTo(TokenTypes.TP_NAME_VALUE_SEP).readIsEqualTo(" ")
                .nextTokenIsEqualTo(TokenTypes.TP_VALUE_BEGIN).readIsEqualTo("\"")
                .nextTokenIsEqualTo(TokenTypes.TP_VALUE).readIsEqualTo(value)
                .nextTokenIsEqualTo(TokenTypes.TP_VALUE_END).readIsEqualTo("\"")
                .nextTokenIsEqualTo(TokenTypes.TP_END).readIsEqualTo("]");

        return this;
    }

    public PgnLexerAssert outputContainsExactly(final byte[] tokens) {
        final byte[] out = new byte[tokens.length];

        IntStream.range(0, out.length).forEach(value -> {
            out[value] = actual.next();
            actual.read();
        });

        assertThat(out).containsExactly(tokens);

        return this;
    }

    public PgnLexerAssert readIsNull() {
        assertThat(actual.read()).isNull();
        return this;
    }

    public PgnLexerAssert linesCountIsEqualTo(final int expectedLineCount) {
        actual.next();
        do {
            actual.next();
            actual.read();
        }
        while (actual.last() != TokenTypes.UNDEFINED);

        return lineIsEqualTo(expectedLineCount);
    }

    public PgnLexerAssert lineIsEqualTo(final int expectedLineCount) {

        assertThat(actual.line())
                .isPositive()
                .isEqualTo(expectedLineCount);

        return this;
    }

    public PgnLexerAssert producesSameTokensAs(final InputStreamPgnLexer expected) {
        while (true) {
            assertThat(actual.next()).isEqualTo(expected.next());

            if (expected.last() == TokenTypes.UNDEFINED && actual.last() == TokenTypes.UNDEFINED) {
                break;
            }

            actual.skip();
            expected.skip();
        }

        return this;
    }
}
