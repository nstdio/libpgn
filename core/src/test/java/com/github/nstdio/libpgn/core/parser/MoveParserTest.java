package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.MOVE_WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoveParserTest {
    @Mock
    private PgnLexer lexer;
    @Mock
    private InputParser<short[], short[]> nag;
    @Mock
    private Configuration config;
    @Mock
    private Parser<byte[]> comment;
    @Mock
    private Parser<List<MoveText>> variation;
    @Mock
    private InlineNag inlineNag;

    private InputParser<Move, Byte> moveParser;

    @BeforeEach
    public void setUp() {
        moveParser = new MoveParser(lexer, config, nag, comment, variation, inlineNag);
    }

    @Test
    public void nagAsComments() {
        final short[] expectedNag = {3, 7};
        final String commentText = "Comment";
        final String nagDelim = ". ";

        when(lexer.last()).thenReturn(MOVE_WHITE);
        when(lexer.read()).thenReturn("Nf3".getBytes());
        when(config.threatNagAsComment()).thenReturn(nagDelim);
        when(nag.parse(any())).then(invocationOnMock -> expectedNag);
        when(comment.tryParse()).then(invocationOnMock -> commentText.getBytes());

        final Move move = moveParser.parse(MOVE_WHITE);

        assertThat(move.move()).containsExactly("Nf3".getBytes());
        assertThat(move.nag()).containsExactly(expectedNag);
        assertThat(move.comment())
                .containsExactly((commentText + nagDelim + "Very good move" + nagDelim
                        + "Forced move (all others lose quickly)").getBytes());
    }
}