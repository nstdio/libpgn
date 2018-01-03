package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.Move;
import com.github.nstdio.libpgn.core.Movetext;
import com.github.nstdio.libpgn.core.NAG;
import com.github.nstdio.libpgn.core.internal.Pair;

import java.util.List;
import java.util.Objects;

class MoveParser extends AbstractParser implements InputParser<Move, Byte> {

    private static final String DELIM = ". ";
    private final InputParser<short[], short[]> nagParser;
    private final Parser<String> commentParser;
    private final Parser<List<Movetext>> variationParser;
    private final InlineNag inlineNag;

    MoveParser(final PgnLexer lexer, final Configuration config, final InputParser<short[], short[]> nagParser,
               final Parser<String> comment, final Parser<List<Movetext>> variation) {
        this(lexer, config, nagParser, comment, variation, new InlineNag());
    }

    // For testing propose only
    @SuppressWarnings("WeakerAccess")
    MoveParser(final PgnLexer lexer, Configuration config, final InputParser<short[], short[]> nagParser,
               final Parser<String> comment, final Parser<List<Movetext>> variation, final InlineNag inlineNag) {
        super(lexer, config);
        this.nagParser = Objects.requireNonNull(nagParser);
        this.commentParser = Objects.requireNonNull(comment);
        this.variationParser = Objects.requireNonNull(variation);
        this.inlineNag = Objects.requireNonNull(inlineNag);
    }

    @Override
    public Move parse(Byte input) {
        lastNotEqThrow(input);
        short[] nags = null;
        String move = read();

        if (config.extractLiteralNags()) {
            final Pair<String, short[]> pair = inlineNag.split(move);
            move = pair.first;
            nags = pair.second;
        }

        lexer.next();

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

        return new Move(move, comment, nags, variation);
    }
}
