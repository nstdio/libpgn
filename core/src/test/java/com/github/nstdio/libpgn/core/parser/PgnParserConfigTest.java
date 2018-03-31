package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.PgnSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class PgnParserConfigTest extends MockedEnvAware {
    private PgnParser parser;

    @BeforeEach
    public void setUp() {
        parser = new PgnParser(mockLexer, mockConfiguration, mockTagPairParser, mockResultParser, mockMoveSequenceParser);
    }

    @Test
    public void stopOnError() {
        when(mockLexer.last()).thenReturn(TokenTypes.TP_NAME);
        when(mockConfiguration.gameLimit()).thenReturn(Integer.MAX_VALUE);
        when(mockConfiguration.stopOnError()).thenReturn(false);

        assertThat(parser.parse()).isEmpty();
        assertThat(parser.hasExceptions()).isTrue();
        assertThat(parser.exceptions()).hasAtLeastOneElementOfType(PgnSyntaxException.class);
    }
}
