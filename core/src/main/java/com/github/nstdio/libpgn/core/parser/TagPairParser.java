package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.common.ArrayUtils;
import com.github.nstdio.libpgn.entity.TagPair;

import java.util.ArrayList;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

class TagPairParser extends AbstractParser implements Parser<List<TagPair>> {

    TagPairParser(final PgnLexer lexer, final Configuration config) {
        super(lexer, config);
    }

    @Override
    public List<TagPair> parse() {
        if (config.skipTagPairSection()) {
            lexer.poll(MOVE_NUMBER);

            return null;
        }

        final List<TagPair> section = new ArrayList<>(10);

        while (lexer.last() == TP_BEGIN) {
            section.add(parseTagPair());
        }

        return section;
    }

    private TagPair parseTagPair() {
        nextNotEqThrow(TP_NAME);

        final byte[] tag = readBytes();
        final byte[] value;

        nextNotEqThrow(TP_NAME_VALUE_SEP);
        nextNotEqThrow(TP_VALUE_BEGIN);

        if (lexer.next() == TP_VALUE) {
            value = readBytes();

            nextNotEqThrow(TP_VALUE_END);
            nextNotEqThrow(TP_END);
        } else if (lexer.last() == TP_VALUE_END) {
            value = ArrayUtils.EMPTY_BYTE_ARRAY;
            nextNotEqThrow(TP_END);
        } else {
            throw syntaxException(lexer, lexer.last(), TP_VALUE, TP_VALUE_END);
        }

        lexer.next();

        return TagPair.of(tag, value);
    }

    @Override
    public List<TagPair> tryParse() {
        throw new RuntimeException("Not implemented yet.");
    }
}
