package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.*;
import com.github.nstdio.libpgn.core.exception.PgnException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

public abstract class AbstractPgnParser extends AbstractParser {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Game.Result> resultParser;
    private final InputParser<List<Movetext>, Byte> movetextSequenceParser;

    /**
     * Exception bag.
     */
    private final List<PgnException> exceptions = new ArrayList<>();

    AbstractPgnParser(final PgnLexer lexer, final Configuration config) {
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

    AbstractPgnParser(final PgnLexer lexer, final Configuration config,
                      final Parser<List<TagPair>> tagPairParser,
                      final Parser<Game.Result> resultParser,
                      final InputParser<List<Movetext>, Byte> movetextSequenceParser) {
        super(lexer, config);
        this.tagPairParser = tagPairParser;
        this.resultParser = resultParser;
        this.movetextSequenceParser = movetextSequenceParser;
    }

    public Game next() {
        if (lexer.next() == UNDEFINED) {
            return null;
        }

        switch (lexer.last()) {
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
    }


    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    @Nonnull
    public List<PgnException> exceptions() {
        return new ArrayList<>(exceptions);
    }
}
