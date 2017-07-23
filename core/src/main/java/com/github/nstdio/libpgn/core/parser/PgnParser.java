package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.*;
import com.github.nstdio.libpgn.core.exception.FilterException;
import com.github.nstdio.libpgn.core.exception.PgnException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

public class PgnParser extends AbstractParser implements InputParser<List<Game>, byte[]> {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Game.Result> resultParser;
    private final InputParser<List<Movetext>, Byte> movetextSequenceParser;

    /**
     * Exception bag.
     */
    private final List<PgnException> exceptions = new ArrayList<>();

    public PgnParser(final Configuration config) {
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

    public PgnParser() {
        this(Configuration.defaultConfiguration());
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

        try {
            parse0(db, config.gameLimit());
        } catch (PgnException e) {
            if (config.stopOnError()) {
                throw e;
            }
            exceptions.add(e);
        }

        return db;
    }

    private void parse0(final List<Game> container, final int gameLimit) {
        while (lexer.lastToken() != UNDEFINED) {
            final byte token = lexer.lastToken();

            if (container.size() >= gameLimit) {
                lexer.terminate();
                break;
            }

            try {
                switch (token) {
                    case TP_BEGIN:
                        container.add(new Game(
                                tagPairParser.parse(),
                                movetextSequenceParser.parse(GAMETERM),
                                resultParser.parse()
                        ));

                        break;
                    case MOVE_NUMBER:
                        container.add(new Game(
                                null,
                                movetextSequenceParser.parse(GAMETERM),
                                resultParser.parse()
                        ));
                        break;
                    default:
                        throw syntaxException(lexer, TP_BEGIN, MOVE_NUMBER);
                }
            } catch (FilterException e) {
                lexer.poll(GAMETERM);
            }

            lexer.nextToken();
        }
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    @Nonnull
    public List<PgnException> exceptions() {
        return new ArrayList<>(exceptions);
    }
}
