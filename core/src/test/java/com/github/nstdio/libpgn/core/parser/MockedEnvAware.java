package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import com.github.nstdio.libpgn.core.pgn.TagPair;
import org.mockito.Mock;

import java.util.List;


public class MockedEnvAware {
    @Mock
    PgnLexer mockLexer;

    @Mock
    Configuration mockConfiguration;

    @Mock
    Parser<List<TagPair>> mockTagPairParser;

    @Mock
    Parser<Game.Result> mockResultParser;

    @Mock
    InputParser<List<MoveText>, Byte> mockMoveSequenceParser;
}
