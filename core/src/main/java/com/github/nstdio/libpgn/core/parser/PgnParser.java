package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.*;
import com.github.nstdio.libpgn.core.exception.FilterException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

public class PgnParser extends AbstractParser implements InputParser<List<Game>, byte[]> {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Game.Result> resultParser;
    private final InputParser<List<Movetext>, Byte> movetextSequenceParser;

    public PgnParser(@Nonnull final Configuration config) {
        super(new PgnLexer(), config);
        tagPairParser = new TagPairParser(lexer, config);
        resultParser = new ResultParser(lexer, config);


        NagParser nagParser = new NagParser(lexer, config);
        CommentParser commentParser = new CommentParser(lexer, config);

        final VariationParser variation = new VariationParser(lexer, config);
        final InputParser<Move, Byte> moveParser = new MoveParser(lexer, config, nagParser, commentParser, variation);
        final Parser<Movetext> movetextParser = new MovetextParser(lexer, config, moveParser);
        movetextSequenceParser = new MovetextSequenceParser(lexer, config, movetextParser);
        variation.setMovetextSequenceParser(movetextSequenceParser);
    }

    /**
     * For testing propose only
     */
    PgnParser(final PgnLexer lexer, final Configuration config, final Parser<List<TagPair>> tagPairParser,
              final Parser<Game.Result> resultParser, final InputParser<List<Movetext>, Byte> movetextSequenceParser) {
        super(lexer, config);
        this.tagPairParser = tagPairParser;
        this.resultParser = resultParser;
        this.movetextSequenceParser = movetextSequenceParser;
    }

    public PgnParser() {
        this(Configuration.defaultConfiguration());
    }

    public List<Game> parse(final String input) {
        return parse0(input.getBytes());
    }

    @Override
    public List<Game> parse(final byte[] input) {
        return parse0(input);
    }

    private List<Game> parse0(final byte[] input) {
        lexer.init(input);
        lexer.nextToken();

        final List<Game> db = new ArrayList<>();
        final int gameLimit = config.gameLimit();

        while (lexer.lastToken() != UNDEFINED) {
            final byte token = lexer.lastToken();

            if (db.size() >= gameLimit) {
                lexer.terminate();
                break;
            }

            switch (token) {
                case TP_BEGIN:
                    try {
                        final List<TagPair> section = tagPairParser.parse();
                        final List<Movetext> moves = movetextSequenceParser.parse(GAMETERM);
                        final Game.Result result = resultParser.parse();

                        db.add(new Game(section, moves, result));
                    } catch (FilterException e) {
                        lexer.poll(GAMETERM);
                    }

                    break;
                case MOVE_NUMBER:
                    final List<Movetext> parse = movetextSequenceParser.parse(GAMETERM);

                    db.add(new Game(null, parse, resultParser.parse()));
                    break;
                default:
                    throw syntaxException(lexer, TP_BEGIN, MOVE_NUMBER);
            }
            lexer.nextToken();
        }

        return db;
    }
}
