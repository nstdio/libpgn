package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.Movetext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.asatryan.libpgn.core.TokenTypes.VARIATION_BEGIN;
import static com.asatryan.libpgn.core.TokenTypes.VARIATION_END;

class VariationParser extends AbstractParser implements Parser<List<Movetext>> {
    private InputParser<List<Movetext>, Byte> movetextSequenceParser;

    VariationParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config) {
        super(lexer, config);
    }

    void setMovetextSequenceParser(InputParser<List<Movetext>, Byte> movetextSequenceParser) {
        this.movetextSequenceParser = movetextSequenceParser;
    }

    @Override
    public List<Movetext> parse() {
        lastNotEqThrow(VARIATION_BEGIN);

        do {
            if (lexer.nextToken() != VARIATION_BEGIN) {
                return movetextSequenceParser.parse(VARIATION_END);
            }
        } while (lexer.lastToken() != VARIATION_END);

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public List<Movetext> tryParse() {
        List<Movetext> variation = null;
        if (lexer.lastToken() == VARIATION_BEGIN) {
            variation = parse();
            lexer.nextToken();
        }
        return config.skipVariations() ? null : variation;
    }
}
