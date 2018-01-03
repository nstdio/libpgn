package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.exception.PgnException;

import javax.annotation.Nullable;

import static com.github.nstdio.libpgn.core.TokenTypes.GAMETERM;

class ResultParser extends AbstractParser implements Parser<Game.Result> {

    ResultParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    @Override
    public Game.Result parse() {
        lastNotEqThrow(GAMETERM);
        String result = read();

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
