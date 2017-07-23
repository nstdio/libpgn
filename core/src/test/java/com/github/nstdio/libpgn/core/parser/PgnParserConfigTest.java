package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PgnParserConfigTest extends MockedEnvAware {
    private PgnParser parser;

    @Before
    public void setUp() {
        parser = new PgnParser(mockLexer, mockConfiguration, mockTagPairParser, mockResultParser, mockMoveSequenceParser);
    }

    @Test
    public void gameLimit() {
        when(mockLexer.lastToken()).thenReturn(TokenTypes.TP_BEGIN);
        when(mockConfiguration.gameLimit()).thenReturn(1);
        when(mockTagPairParser.parse()).thenReturn(new ArrayList<>());
        when(mockMoveSequenceParser.parse(anyByte())).thenReturn(new ArrayList<>());
        when(mockResultParser.parse()).thenReturn(any(Game.Result.class));

        assertThat(parser.parse("")).hasSize(1);

        verify(mockLexer).terminate();
        verify(mockLexer).init(any(byte[].class));
        verify(mockLexer, times(2)).nextToken();
        verify(mockLexer, times(4)).lastToken();

        verify(mockTagPairParser).parse();
        verify(mockConfiguration).gameLimit();
        verify(mockMoveSequenceParser).parse(anyByte());
        verify(mockResultParser).parse();

        verifyNoMoreInteractions(mockLexer, mockConfiguration, mockTagPairParser, mockMoveSequenceParser, mockResultParser);
    }

    @Test
    public void stopOnError() {
        final String input = "a";

        when(mockLexer.lastToken()).thenReturn(TokenTypes.TP_NAME);
        when(mockLexer.data()).thenReturn(input.getBytes());
        when(mockLexer.position()).thenReturn(0);
        when(mockConfiguration.gameLimit()).thenReturn(Integer.MAX_VALUE);
        when(mockConfiguration.stopOnError()).thenReturn(false);

        assertThat(parser.parse(input)).isEmpty();
        assertThat(parser.hasExceptions()).isTrue();
        assertThat(parser.exceptions()).hasAtLeastOneElementOfType(PgnSyntaxException.class);
    }
}
