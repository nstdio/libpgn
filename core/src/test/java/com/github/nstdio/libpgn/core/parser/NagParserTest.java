package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.TokenTypes;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class NagParserTest {
    private NagParser parser;
    private int limit;
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = Configuration.defaultConfiguration();
        limit = 5;
    }

    private short[] parseWithLimit(final PgnLexer lexer) {
        buildNewConfig();
        parser = new NagParser(lexer, config);

        return parser.parse(null);
    }

    private void buildNewConfig() {
        config = Configuration.defaultBuilder().nagLimit(limit).build();
    }

    private PgnLexer initAndIterateUntilNag(String input) {
        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(input.getBytes());
        lexer.next();
        lexer.poll(TokenTypes.NAG);

        return lexer;
    }

    @Test
    public void limit() throws Exception {
        final PgnLexer pgnLexer = initAndIterateUntilNag("1. e4 $6$2$3$4$5$1$7 *");

        final short[] nags = parseWithLimit(pgnLexer);

        assertThat(nags).isNotNull();
        assertEquals(limit, nags.length);
    }

    @Test
    public void exactLimit() throws Exception {
        final PgnLexer pgnLexer = initAndIterateUntilNag("1. e4 $6$2$3$4$5$1$7 *");

        limit = 2;
        buildNewConfig();

        final short[] nags = parseWithLimit(pgnLexer);
        assertEquals(limit, nags.length);
    }

    @Test
    public void mergeWithInvalidTopElement() throws Exception {
        final InputStreamPgnLexer inputStreamPgnLexer = InputStreamPgnLexer.of("1. e4 $2 *".getBytes());

        final short[] nags = parseWithLimit(inputStreamPgnLexer);
        final short[] merged = parser.parse(nags);

        assertArrayEquals(nags, merged);
        assertSame(nags, merged);
    }

    @Test
    public void mergeWithInvalidNag() throws Exception {
        final PgnLexer pgnLexer = initAndIterateUntilNag("1. d4 $a $e *");
        final short[] nags = parseWithLimit(pgnLexer);

        assertArrayEquals(new short[]{0}, nags);
    }
}