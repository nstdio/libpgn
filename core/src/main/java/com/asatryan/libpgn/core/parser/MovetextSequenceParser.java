package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.Movetext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

class MovetextSequenceParser extends AbstractParser implements InputParser<List<Movetext>, Byte> {

    private final Parser<Movetext> movetextParser;

    MovetextSequenceParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config, Parser<Movetext> movetextParser) {
        super(lexer, config);
        this.movetextParser = movetextParser;
    }

    @Override
    public List<Movetext> parse(Byte termToken) {
        final List<Movetext> moves = new ArrayList<>();

        while (lexer.lastToken() != termToken) {
            Movetext move = movetextParser.parse();

            moves.add(move);
        }

        return tryUseSingletonList(moves);
    }

    @Nonnull
    private List<Movetext> tryUseSingletonList(final List<Movetext> moves) {
        return moves.size() == 1 ? singletonList(moves.get(0)) : moves;
    }
}
