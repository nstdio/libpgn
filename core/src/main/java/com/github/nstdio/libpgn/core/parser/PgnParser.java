package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.*;
import com.github.nstdio.libpgn.core.exception.FilterException;
import com.github.nstdio.libpgn.core.exception.PgnException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;
import static java.util.stream.Collectors.toList;

public class PgnParser extends AbstractParser implements Iterable<Game> {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Game.Result> resultParser;
    private final InputParser<List<Movetext>, Byte> movetextSequenceParser;

    /**
     * Exception bag.
     */
    private final List<PgnException> exceptions = new ArrayList<>();

    public PgnParser(final PgnLexer lexer) {
        this(lexer, Configuration.defaultConfiguration());
    }

    public PgnParser(final PgnLexer lexer, final Configuration config) {
        super(lexer, config);
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

    @Deprecated
    public List<Game> parse() {
        return StreamSupport.stream(spliterator(), false).collect(toList());
    }

    public Game next() {
        if (lexer.next() == UNDEFINED) {
            return null;
        }

        final byte token = lexer.last();

        try {
            switch (token) {
                case TP_BEGIN:
                    return new Game(
                            tagPairParser.parse(),
                            movetextSequenceParser.parse(GAMETERM),
                            resultParser.parse()
                    );

                case MOVE_NUMBER:
                    return new Game(
                            null,
                            movetextSequenceParser.parse(GAMETERM),
                            resultParser.parse()
                    );
                default:
                    throw syntaxException(lexer, TP_BEGIN, MOVE_NUMBER);
            }
        } catch (FilterException e) {
            lexer.poll(GAMETERM);
        }

        return null;
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    @Nonnull
    public List<PgnException> exceptions() {
        return new ArrayList<>(exceptions);
    }

    @Override
    public Iterator<Game> iterator() {
        return new GameIterator(this);
    }

    @Override
    public Spliterator<Game> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE & Spliterator.NONNULL);
    }
}
