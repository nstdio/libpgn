package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.pgn.MoveText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class MoveTextSequenceParser extends AbstractParser implements InputParser<List<MoveText>, Byte> {

    private final Parser<MoveText> movetextParser;

    MoveTextSequenceParser(final PgnLexer lexer, final Configuration config, final Parser<MoveText> moveTextParser) {
        super(lexer, config);
        this.movetextParser = Objects.requireNonNull(moveTextParser);
    }

    @Override
    public List<MoveText> parse(Byte termToken) {
        if (config.skipMovetext()) {
            lexer.poll(TokenTypes.GAMETERM);

            return Collections.emptyList();
        }

        final List<MoveText> moves = new ArrayList<>();

        while (lexer.last() != termToken) {
            moves.add(movetextParser.parse());
        }

        return moves;
    }
}
