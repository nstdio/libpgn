package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.exception.PgnException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.nstdio.libpgn.core.TokenTypes.GAMETERM;

class ResultParser extends AbstractParser implements Parser<Game.Result> {

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
