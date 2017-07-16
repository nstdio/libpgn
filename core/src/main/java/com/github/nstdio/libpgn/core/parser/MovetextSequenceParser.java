package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.GameFilter;
import com.github.nstdio.libpgn.core.Movetext;
import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.FilterException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MovetextSequenceParser extends AbstractParser implements InputParser<List<Movetext>, Byte> {

    private final Parser<Movetext> movetextParser;

    MovetextSequenceParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config, Parser<Movetext> movetextParser) {
        super(lexer, config);
        this.movetextParser = movetextParser;
    }

    @Override
    public List<Movetext> parse(Byte termToken) {
        if (config.skipMovetext()) {
            lexer.poll(TokenTypes.GAMETERM);

            return Collections.emptyList();
        }

        final List<Movetext> moves = new ArrayList<>();

        while (lexer.lastToken() != termToken) {
            moves.add(movetextParser.parse());
        }

        GameFilter gameFilter = config.gameFilter();
        if (gameFilter != null && !gameFilter.testMovetext(moves)) {
            throw new FilterException("Skip this game.");
        }

        return moves;
    }
}
