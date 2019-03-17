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


    void lastNotEqThrow(byte expectedToken) {
        if (lexer.last() != expectedToken) {
            throw syntaxException(lexer, lexer.last(), expectedToken);
        }
    }

    String read() {
        return new String(readBytes());
    }

    byte[] readBytes() {
        return lexer.read();
    }

    void nextNotEqThrow(byte expectedToken) {
        if (lexer.next() != expectedToken) {
            throw syntaxException(lexer, lexer.last(), expectedToken);
        }
    }
}
