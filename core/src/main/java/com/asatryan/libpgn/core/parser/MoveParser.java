package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.*;
import com.asatryan.libpgn.core.internal.Pair;

import java.util.List;

class MoveParser extends AbstractParser implements InputParser<Move, Byte> {

    private static final String DELIM = ". ";
    private final InputParser<short[], short[]> nagParser;
    private final Parser<String> commentParser;
    private final Parser<List<Movetext>> variationParser;
    private final InlineNag inlineNag;

    MoveParser(PgnLexer lexer, Configuration config,
               InputParser<short[], short[]> nagParser, Parser<String> comment, Parser<List<Movetext>> variation) {
        this(lexer, config, nagParser, comment, variation, new InlineNag());
    }

    // For testing propose only
    @SuppressWarnings("WeakerAccess")
    MoveParser(PgnLexer lexer, Configuration config, InputParser<short[], short[]> nagParser, Parser<String> comment,
               Parser<List<Movetext>> variation, InlineNag inlineNag) {
        super(lexer, config);
        this.nagParser = nagParser;
        this.commentParser = comment;
        this.variationParser = variation;
        this.inlineNag = inlineNag;
    }

    @Override
    public Move parse(Byte input) {
        lastNotEqThrow(input);
        short[] nags = null;
        String move = lexer.extract();

        if (config.extractLiteralNags()) {
            final Pair<String, short[]> pair = inlineNag.split(move);
            move = pair.first;
            nags = pair.second;
        }

        lexer.nextToken();

        nags = nagParser.parse(nags);
        List<Movetext> variation = variationParser.tryParse();
        nags = nagParser.parse(nags);
        String comment = commentParser.tryParse();
        nags = nagParser.parse(nags);

        if (variation == null) {
            variation = variationParser.tryParse();
            nags = nagParser.parse(nags);
        }

        if (config.threatNagAsComment() != null) {

            if (comment == null) {
                comment = NAG.descriptionOf(nags, DELIM);
            } else {
                comment += DELIM + NAG.descriptionOf(nags, DELIM);
            }
        }

        return MoveFactory.of(move, comment, nags, variation);
    }
}
