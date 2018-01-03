package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.GameFilter;
import com.github.nstdio.libpgn.core.Movetext;
import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.exception.FilterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class MovetextSequenceParser extends AbstractParser implements InputParser<List<Movetext>, Byte> {

    private final Parser<Movetext> movetextParser;

    MovetextSequenceParser(final PgnLexer lexer, final Configuration config, final Parser<Movetext> movetextParser) {
        super(lexer, config);
        this.movetextParser = Objects.requireNonNull(movetextParser);
    }

    @Override
    public List<Movetext> parse(Byte termToken) {
        if (config.skipMovetext()) {
            lexer.poll(TokenTypes.GAMETERM);

            return Collections.emptyList();
        }

        final List<Movetext> moves = new ArrayList<>();

        while (lexer.last() != termToken) {
            moves.add(movetextParser.parse());
        }

        return moves;
    }
}
