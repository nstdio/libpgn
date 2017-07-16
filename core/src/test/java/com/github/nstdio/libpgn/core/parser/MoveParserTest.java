package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Move;
import com.github.nstdio.libpgn.core.Movetext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.MOVE_WHITE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MoveParserTest {
    @Mock
    private PgnLexer lexer;
    @Mock
    private InputParser<short[], short[]> nag;
    @Mock
    private Configuration config;
    @Mock
    private Parser<String> comment;
    @Mock
    private Parser<List<Movetext>> variation;
    @Mock
    private InlineNag inlineNag;

    private InputParser<Move, Byte> moveParser;

    @Before
    public void setUp() throws Exception {
        moveParser = new MoveParser(lexer, config, nag, comment, variation, inlineNag);
    }

    @Test
    public void nagAsComments() throws Exception {
        final short[] expectedNag = {3, 7};
        final String commentText = "Comment";
        final String nagDelim = ". ";

        when(lexer.lastToken()).thenReturn(MOVE_WHITE);
        when(lexer.extract()).thenReturn("Nf3");
        when(config.nagLimit()).thenReturn(8);
        when(config.threatNagAsComment()).thenReturn(nagDelim);
        when(nag.parse(anyObject())).then(invocationOnMock -> expectedNag);
        when(comment.tryParse()).then(invocationOnMock -> commentText);

        final Move move = moveParser.parse(MOVE_WHITE);


        assertEquals("Nf3", move.move());
        assertArrayEquals(expectedNag, move.nag());

        assertEquals(commentText + nagDelim + "Very good move" + nagDelim
                + "Forced move (all others lose quickly)", move.comment());
    }
}