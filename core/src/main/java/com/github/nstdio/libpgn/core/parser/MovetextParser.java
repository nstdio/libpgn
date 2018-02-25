package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;

import javax.annotation.Nullable;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

class MovetextParser extends AbstractParser implements Parser<MoveText> {
    private final InputParser<Move, Byte> moveInputParser;

    MovetextParser(PgnLexer lexer, Configuration config, InputParser<Move, Byte> moveInputParser) {
        super(lexer, config);
        this.moveInputParser = moveInputParser;
    }

    @Override
    public MoveText parse() {
        lastNotEqThrow(MOVE_NUMBER);
        final String moveNumber = read();
        Move black = null, white = null;

        lexer.next();

        if (lexer.last() == DOT) {
            lexer.next();
            white = moveInputParser.parse(MOVE_WHITE);
        } else if (lexer.last() == SKIP_PREV_MOVE) {
            lexer.skip();
            lexer.next();
            black = moveInputParser.parse(MOVE_BLACK);
        } else {
            throw syntaxException(lexer, DOT, SKIP_PREV_MOVE);
        }

        if (lexer.last() == MOVE_NUMBER) { // variation parsed in past
            if (white != null) {
                final String moveNumber2 = read();
                if (lexer.next() != SKIP_PREV_MOVE && !moveNumber.equals(moveNumber2)) { // "1. d4 (1. d5) 45... f5"
                    throw syntaxException(lexer, SKIP_PREV_MOVE);
                }
                // discarding the "..." token.
                lexer.skip();
                lexer.next();
            }
        }

        if (lexer.last() == MOVE_BLACK) {
            black = moveInputParser.parse(MOVE_BLACK);
        }

        return MoveText.of(Integer.parseInt(moveNumber), white, black);
    }

    @Nullable
    @Override
    public MoveText tryParse() {
        throw new UnsupportedOperationException();
    }
}
