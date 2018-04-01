package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.MoveText;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.VARIATION_BEGIN;
import static com.github.nstdio.libpgn.core.TokenTypes.VARIATION_END;

class VariationParser extends AbstractParser implements Parser<List<MoveText>> {
    private InputParser<List<MoveText>, Byte> movetextSequenceParser;

    VariationParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    void setMovetextSequenceParser(InputParser<List<MoveText>, Byte> movetextSequenceParser) {
        this.movetextSequenceParser = movetextSequenceParser;
    }

    @Override
    public List<MoveText> parse() {
        lastNotEqThrow(VARIATION_BEGIN);

        do {
            if (lexer.next() != VARIATION_BEGIN) {
                return movetextSequenceParser.parse(VARIATION_END);
            }
        } while (lexer.last() != VARIATION_END);

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public List<MoveText> tryParse() {
        List<MoveText> variation = null;
        if (lexer.last() == VARIATION_BEGIN) {
            variation = parse();
            lexer.next();
        }
        return config.skipVariations() ? null : variation;
    }
}
