package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class PgnParserConfigTest extends MockedEnvAware {
    private PgnParser parser;

    @Before
    public void setUp() {
        parser = new PgnParser(mockLexer, mockConfiguration, mockTagPairParser, mockResultParser, mockMoveSequenceParser);
    }

    @Test
    public void stopOnError() {
        final String input = "a";

        when(mockLexer.last()).thenReturn(TokenTypes.TP_NAME);
        when(mockConfiguration.gameLimit()).thenReturn(Integer.MAX_VALUE);
        when(mockConfiguration.stopOnError()).thenReturn(false);

        assertThat(parser.parse()).isEmpty();
        assertThat(parser.hasExceptions()).isTrue();
        assertThat(parser.exceptions()).hasAtLeastOneElementOfType(PgnSyntaxException.class);
    }
}
