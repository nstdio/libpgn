package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;

class ExceptionBuilder {

    static PgnSyntaxException syntaxException(PgnLexer lexer, byte... expectedToken) {
        return new PgnSyntaxException("", 0, 0);
    }
}
