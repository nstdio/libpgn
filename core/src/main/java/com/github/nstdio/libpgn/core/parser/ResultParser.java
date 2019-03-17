package com.github.nstdio.libpgn.core.parser;

import static com.github.nstdio.libpgn.core.TokenTypes.GAMETERM;

import javax.annotation.Nullable;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.exception.PgnException;
import com.github.nstdio.libpgn.entity.Result;

class ResultParser extends AbstractParser implements Parser<Result> {

    ResultParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    @Override
    public Result parse() {
        lastNotEqThrow(GAMETERM);

        // TODO: Skip reading and use token length.
        byte[] resultBytes = readBytes();

        switch (resultBytes.length) {
            case 1:
                return Result.UNKNOWN;
            case 7:
                return Result.DRAW;
            case 3:
                return resultBytes[0] == 49 ? Result.WHITE : Result.BLACK;
            default:
                throw new PgnException("Bad game termination: " + new String(resultBytes));
        }
    }

    @Nullable
    @Override
    public Result tryParse() {
        return null;
    }
}
