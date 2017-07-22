package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;

import java.util.Objects;

import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

abstract class AbstractParser {
    final PgnLexer lexer;
    final Configuration config;

    AbstractParser(final PgnLexer lexer, final Configuration config) {
        this.lexer = Objects.requireNonNull(lexer);
        this.config = Objects.requireNonNull(config);
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
