package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.exception.PgnException;
import com.github.nstdio.libpgn.entity.Result;

import javax.annotation.Nullable;

import static com.github.nstdio.libpgn.core.TokenTypes.GAMETERM;

class ResultParser extends AbstractParser implements Parser<Result> {

    ResultParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    @Override
    public Result parse() {
        lastNotEqThrow(GAMETERM);
        String result = read();

        for (Result res : Result.values()) {
            if (result.equals(res.getTerm())) {
                return res;
            }
        }

        throw new PgnException("Bad game termination.");
    }

    @Nullable
    @Override
    public Result tryParse() {
        return null;
    }
}
