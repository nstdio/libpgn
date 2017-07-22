package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Move;
import com.github.nstdio.libpgn.core.Movetext;
import com.github.nstdio.libpgn.core.MovetextFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

class MovetextParser extends AbstractParser implements Parser<Movetext> {
    @Nonnull
    private final InputParser<Move, Byte> moveInputParser;

    MovetextParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config, @Nonnull InputParser<Move, Byte> moveInputParser) {
        super(lexer, config);
        this.moveInputParser = moveInputParser;
    }

    @Override
    public Movetext parse() {
        lastNotEqThrow(MOVE_NUMBER);
        final String moveNumber = lexer.extract();
        Move black = null, white = null;

        lexer.nextToken();

        if (lexer.lastToken() == DOT) {
            lexer.nextToken();
            white = moveInputParser.parse(MOVE_WHITE);
        } else if (lexer.lastToken() == SKIP_PREV_MOVE) {
            lexer.nextToken();
            black = moveInputParser.parse(MOVE_BLACK);
        } else {
            throw syntaxException(lexer, DOT, SKIP_PREV_MOVE);
        }

        if (lexer.lastToken() == MOVE_NUMBER) { // variation parsed in past
            if (white != null) {
                final String moveNumber2 = lexer.extract();
                if (lexer.nextToken() != SKIP_PREV_MOVE && !moveNumber.equals(moveNumber2)) { // "1. d4 (1. d5) 45... f5"
                    throw syntaxException(lexer, SKIP_PREV_MOVE);
                }
                lexer.nextToken();
            }
        }

        if (lexer.lastToken() == MOVE_BLACK) {
            black = moveInputParser.parse(MOVE_BLACK);
        }

        return MovetextFactory.of(moveNumber, white, black);
    }

    @Nullable
    @Override
    public Movetext tryParse() {
        return null;
    }
}