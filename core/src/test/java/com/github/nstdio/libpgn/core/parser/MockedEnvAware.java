package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;
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
    Parser<Result> mockResultParser;

    @Mock
    InputParser<List<MoveText>, Byte> mockMoveSequenceParser;

    @Mock
    Parser<byte[]> mockCommentParser;
}
