package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.entity.TagPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TagPairParserTest extends MockedEnvAware {
    private Parser<List<TagPair>> parser;

    @BeforeEach
    public void setUp() {
        doReturn(true)
                .when(mockConfiguration).cacheTagPair();

        doReturn(Integer.MAX_VALUE)
                .when(mockConfiguration).tagPairCacheSize();
    }

    @Test
    @Disabled
    public void predefinedCache() {
        final String white = "White";
        final String value = "Kasparov, Garry";

        TagPair tagPair = TagPair.of(white, value);

        doReturn(Collections.singleton(tagPair))
                .when(mockConfiguration).predefinedCache();

        doReturn(TP_BEGIN)
                .doReturn(MOVE_NUMBER)
                .when(mockLexer).last();

        doReturn(TP_NAME)
                .doReturn(TP_NAME_VALUE_SEP)
                .doReturn(TP_VALUE_BEGIN)
                .doReturn(TP_VALUE)
                .doReturn(TP_VALUE_END)
                .doReturn(TP_END)
                .when(mockLexer).next();


        doReturn(white.getBytes())
                .doReturn(value.getBytes())
                .when(mockLexer).read();

        parser = new TagPairParser(mockLexer, mockConfiguration);

        final List<TagPair> tagPairs = parser.parse();

        assertThat(tagPairs)
                .hasSize(1);

        final TagPair actual = tagPairs.get(0);

        assertThat(tagPair).isSameAs(actual);
    }
}