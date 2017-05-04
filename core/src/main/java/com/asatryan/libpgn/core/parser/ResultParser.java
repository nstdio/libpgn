package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.Game;
import com.asatryan.libpgn.core.exception.PgnException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.asatryan.libpgn.core.TokenTypes.GAMETERM;

public class ResultParser extends AbstractParser implements Parser<Game.Result> {

    ResultParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config) {
        super(lexer, config);
    }

    @Override
    public Game.Result parse() {
        lastNotEqThrow(GAMETERM);
        String result = lexer.extract();

        for (Game.Result res : Game.Result.values()) {
            if (result.equals(res.getTerm())) {
                return res;
            }
        }

        throw new PgnException("Bad game termination.");
    }

    @Nullable
    @Override
    public Game.Result tryParse() {
        return null;
    }
}
