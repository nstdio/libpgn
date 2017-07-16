package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TagPair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class TagPairParserTest extends MockedLexerAware {
    private Parser<List<TagPair>> parser;

    @Before
    public void setUp() throws Exception {
        doReturn(true)
                .when(configuration).cacheTagPair();

        doReturn(Integer.MAX_VALUE)
                .when(configuration).tagPairCacheSize();
    }

    @Test
    public void predefinedCache() throws Exception {
        final String white = "White";
        final String value = "Kasparov, Garry";

        TagPair tagPair = TagPair.of(white, value);

        doReturn(Collections.singleton(tagPair))
                .when(configuration).predefinedCache();

        doReturn(TP_BEGIN)
                .doReturn(MOVE_NUMBER)
                .when(lexer).lastToken();

        doReturn(TP_NAME)
                .doReturn(TP_NAME_VALUE_SEP)
                .doReturn(TP_VALUE_BEGIN)
                .doReturn(TP_VALUE)
                .doReturn(TP_VALUE_END)
                .doReturn(TP_END)
                .when(lexer).nextToken();


        doReturn(white)
                .doReturn(value)
                .when(lexer).extract();

        parser = new TagPairParser(lexer, configuration);

        final List<TagPair> tagPairs = parser.parse();

        assertTrue(tagPairs.size() == 1);

        final TagPair actual = tagPairs.get(0);

        assertSame(tagPair, actual);
        assertNotSame(TagPair.of(tagPair), actual);
    }
}