package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.NAG;
import com.github.nstdio.libpgn.common.ArrayUtils;
import com.github.nstdio.libpgn.common.Pair;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;

import java.util.List;
import java.util.Objects;

class MoveParser extends AbstractParser implements InputParser<Move, Byte> {

    private static final String DELIM = ". ";
    private final InputParser<short[], short[]> nagParser;
    private final Parser<byte[]> commentParser;
    private final Parser<List<MoveText>> variationParser;
    private final InlineNag inlineNag;

    MoveParser(final PgnLexer lexer, final Configuration config, final InputParser<short[], short[]> nagParser,
               final Parser<byte[]> comment, final Parser<List<MoveText>> variation) {
        this(lexer, config, nagParser, comment, variation, new InlineNag());
    }

    // For testing propose only
    @SuppressWarnings("WeakerAccess")
    MoveParser(final PgnLexer lexer, Configuration config, final InputParser<short[], short[]> nagParser,
               final Parser<byte[]> comment, final Parser<List<MoveText>> variation, final InlineNag inlineNag) {
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
        byte[] move = readBytes();

        if (config.extractLiteralNags()) {
            final Pair<byte[], short[]> pair = inlineNag.split(move);
            move = pair.first;
            nags = pair.second;
        }

        lexer.next();

        nags = nagParser.parse(nags);
        List<MoveText> variation = variationParser.tryParse();
        nags = nagParser.parse(nags);
        byte[] comment = commentParser.tryParse();
        nags = nagParser.parse(nags);

        if (variation == null) {
            variation = variationParser.tryParse();
            nags = nagParser.parse(nags);
        }

        if (config.threatNagAsComment() != null) {
            if (ArrayUtils.isEmptyOrNull(comment)) {
                comment = NAG.descriptionOf(nags, DELIM).getBytes();
            } else {
                comment = ArrayUtils.concat(comment, (DELIM + NAG.descriptionOf(nags, DELIM)).getBytes());
            }
        }

        return Move.of(move, comment, nags, variation);
    }
}
