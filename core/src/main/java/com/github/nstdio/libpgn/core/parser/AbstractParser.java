package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;

import javax.annotation.Nonnull;

import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

abstract class AbstractParser {
    final PgnLexer lexer;
    final Configuration config;

    AbstractParser(final @Nonnull PgnLexer lexer, final @Nonnull Configuration config) {
        this.lexer = lexer;
        this.config = config;
    }


    void lastNotEqThrow(byte token) {
        if (lexer.lastToken() != token) {
            throw syntaxException(lexer, token);
        }
    }

    String extractNextIfNotEqThrow(byte expectedToken) {
        nextNotEqThrow(expectedToken);

        return lexer.extract();
    }

    void nextNotEqThrow(byte expectedToken) {
        final byte nextToken = lexer.nextToken();
        if (nextToken != expectedToken) {
            throw syntaxException(lexer, expectedToken);
        }
    }
}
