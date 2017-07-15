package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.TokenTypes;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class NagParserTest {
    private NagParser parser;
    private int limit;
    private PgnLexer lexer;
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        lexer = new PgnLexer();
        config = Configuration.defaultConfiguration();
        parser = new NagParser(lexer, config);
        limit = 5;
    }

    private short[] parseWithLimit() {
        buildNewConfig();
        parser = new NagParser(lexer, config);

        return parser.parse(null);
    }

    private void buildNewConfig() {
        config = Configuration.defaultBuilder().nagLimit(limit).build();
    }

    private void initAndIterateUntilNag(String input) {
        lexer.init(input.getBytes());
        lexer.queue(TokenTypes.NAG);
    }

    @Test
    public void limit() throws Exception {
        initAndIterateUntilNag("1. e4 $6$2$3$4$5$1$7 *");

        final short[] nags = parseWithLimit();

        assertNotNull(nags);
        assertEquals(limit, nags.length);
    }

    @Test
    public void exactLimit() throws Exception {
        initAndIterateUntilNag("1. e4 $6$2$3$4$5$1$7 *");

        limit = 2;
        buildNewConfig();

        final short[] nags = parseWithLimit();
        assertEquals(limit, nags.length);
    }

    @Test
    @Ignore("Not implemented.")
    public void merge() throws Exception {
        initAndIterateUntilNag("1. e4 $18$27 {Comment} $34$12 *");

        final short[] mergeWith = parseWithLimit();

        assertArrayEquals(new short[]{18, 27}, mergeWith);

        do {
            lexer.nextToken();
        } while (lexer.lastToken() != TokenTypes.NAG);

        final short[] nags = parser.parse(mergeWith);

        assertArrayEquals(new short[]{12, 18, 27, 34}, nags);
    }

    @Test
    public void mergeWithInvalidTopElement() throws Exception {
        lexer.init("1. e4 $2 *".getBytes());
        lexer.nextToken();

        final short[] nags = parseWithLimit();
        final short[] merged = parser.parse(nags);

        assertArrayEquals(nags, merged);
        assertSame(nags, merged);
    }

    @Test
    public void mergeWithInvalidNag() throws Exception {
        initAndIterateUntilNag("1. d4 $a $e *");
        final short[] nags = parseWithLimit();

        assertArrayEquals(new short[]{0}, nags);
    }

    @Test
    public void nullInput() throws Exception {
        final short[] nags = parser.parse(null);

        assertNull(nags);
    }
}