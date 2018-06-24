package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.core.exception.PgnException;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

public abstract class AbstractPgnParser extends AbstractParser {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Result> resultParser;
    private final InputParser<List<MoveText>, Byte> moveTextSequenceParser;
    private final Parser<byte[]> commentParser;

    /**
     * Exception bag.
     */
    private final List<PgnException> exceptions = new ArrayList<>();

    AbstractPgnParser(final PgnLexer lexer, final Configuration config) {
        super(lexer, config);

        tagPairParser = new TagPairParser(lexer, config);
        resultParser = new ResultParser(lexer, config);
        commentParser = new CommentParser(lexer, config);

        NagParser nagParser = new NagParser(lexer, config);

        final VariationParser variation = new VariationParser(lexer, config);
        final InputParser<Move, Byte> moveParser = new MoveParser(lexer, config, nagParser, commentParser, variation);
        final Parser<MoveText> movetextParser = new MovetextParser(lexer, config, moveParser);
        moveTextSequenceParser = new MoveTextSequenceParser(lexer, config, movetextParser);
        variation.setMovetextSequenceParser(moveTextSequenceParser);
    }

    AbstractPgnParser(final PgnLexer lexer, final Configuration config,
                      final Parser<List<TagPair>> tagPairParser,
                      final Parser<Result> resultParser,
                      final InputParser<List<MoveText>, Byte> moveTextSequenceParser,
                      final Parser<byte[]> commentParser) {
        super(lexer, config);
        this.tagPairParser = tagPairParser;
        this.resultParser = resultParser;
        this.moveTextSequenceParser = moveTextSequenceParser;
        this.commentParser = commentParser;
    }

    public Game next() {
        if (lexer.next() == UNDEFINED) {
            return null;
        }

        switch (lexer.last()) {
            case TP_BEGIN:
                return new Game(
                        tagPairParser.parse(),
                        moveTextSequenceParser.parse(GAMETERM),
                        resultParser.parse()
                );

            case MOVE_NUMBER:
                return new Game(
                        null,
                        moveTextSequenceParser.parse(GAMETERM),
                        resultParser.parse()
                );
            case COMMENT_BEGIN:
                final byte[] comment = commentParser.parse();

                // preparation for move parser
                lexer.next();

                return new Game(
                        null,
                        comment,
                        moveTextSequenceParser.parse(GAMETERM),
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
