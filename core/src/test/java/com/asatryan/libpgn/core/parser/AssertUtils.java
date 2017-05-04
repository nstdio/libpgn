package com.asatryan.libpgn.core.parser;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static org.junit.Assert.assertEquals;

class AssertUtils {

    static void assertSameResult(PgnLexer lexer, final String[] pgnStrings, final byte[] tokens) {
        for (String pgnString : pgnStrings) {
            assertTokensEqual(lexer, pgnString, tokens);

        }
    }

    static void assertTokensEqual(PgnLexer lexer, String pgnString, byte[] tokens) {
        lexer.init(pgnString.toCharArray());

        for (byte token : tokens) {
            final byte actual = lexer.nextToken();
            if (lexer.tokenLength() > 1 || shouldOffsetIncreased(lexer)) {
                lexer.positionOffset(lexer.tokenLength());
            }

            assertEquals(
                    String.format("<%s> at position %d", pgnString, lexer.position()),
                    token,
                    actual);
        }
    }

    private static boolean shouldOffsetIncreased(PgnLexer lexer) {
        final byte token = lexer.lastToken();

        return token == TP_VALUE
                || token == MOVE_NUMBER
                || token == MOVE_WHITE
                || token == MOVE_BLACK
                || token == COMMENT
                || token == GAMETERM
                || token == NAG
                || token == ROL_COMMENT;
    }
}
