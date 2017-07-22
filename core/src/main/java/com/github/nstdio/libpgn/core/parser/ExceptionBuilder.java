package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;
import com.github.nstdio.libpgn.core.internal.ByteUtils;

class ExceptionBuilder {

    static PgnSyntaxException syntaxException(PgnLexer lexer, byte... expectedToken) {
        final int relPos = ByteUtils.lookBackForNewLine(lexer.data(), lexer.position());
        String charOrName = ByteUtils.charOrName(lexer.data(), lexer.position());

        StringBuilder message = new StringBuilder(String.format("Found unexpected token %s <%s>.", TokenTypes.descOf(lexer.lastToken()), charOrName));
        message.append(" Expecting: ");

        for (byte token : expectedToken) {
            message.append(TokenTypes.descOf(token))
                    .append(", ");
        }

        return new PgnSyntaxException(message.substring(0, message.length() - 2),
                lexer.line(),
                lexer.position() - relPos
        );
    }
}
