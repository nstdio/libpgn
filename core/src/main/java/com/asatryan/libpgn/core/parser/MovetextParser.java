package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.Move;
import com.asatryan.libpgn.core.Movetext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static com.asatryan.libpgn.core.parser.ExceptionBuilder.syntaxException;

class MovetextParser extends AbstractParser implements Parser<Movetext> {
    @Nonnull
    private final InputParser<Move, Byte> moveInputParser;

    MovetextParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config, @Nonnull InputParser<Move, Byte> moveInputParser) {
        super(lexer, config);
        this.moveInputParser = moveInputParser;
    }

    @Override
    public Movetext parse() {
        nextNotEqThrow(MOVE_NUMBER);
        final String moveNumber = lexer.extract();
        Move black = null, white = null;

        lexer.nextToken();

        if (lexer.lastToken() == DOT) {
            lexer.nextToken();
            white = moveInputParser.parse(MOVE_WHITE);
        } else if (lexer.lastToken() == SKIP_PREV_MOVE) {
            lexer.positionAlign();
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
                lexer.positionAlign(); // ...
                lexer.nextToken();
            }
        }

        if (lexer.lastToken() == MOVE_BLACK) {
            black = moveInputParser.parse(MOVE_BLACK);
        }

        return Movetext.of(moveNumber, white, black);
    }

    @Nullable
    @Override
    public Movetext tryParse() {
        return null;
    }
}
