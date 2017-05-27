package com.asatryan.libpgn.core.parser;

import java.util.Collection;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

class AssertUtils {

    static void assertSameResult(PgnLexer lexer, final String[] pgnStrings, final byte[] tokens) {
        for (String pgnString : pgnStrings) {
            assertTokensEqual(lexer, pgnString, tokens);
        }
    }

    static void assertTokensEqual(PgnLexer lexer, String pgnString, byte[] tokens) {
        lexer.init(pgnString.toCharArray());
        Queue<Byte> queue = lexer.stream();

        for (byte token : tokens) {
            final byte actual = queue.poll();

            assertEquals(
                    String.format("<%s> at position %d", pgnString, lexer.position()),
                    token,
                    actual);
        }

        assertEmpty(queue);
    }

    static <T> void assertSize(final int expected, Collection<T> collection) {
        assertEquals(expected, collection.size());
    }

    static <T> void assertEmpty(Collection<T> collection) {
        assertSize(0, collection);
    }
}
