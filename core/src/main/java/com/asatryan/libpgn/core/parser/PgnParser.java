package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.Game;
import com.asatryan.libpgn.core.Movetext;
import com.asatryan.libpgn.core.TagPair;
import com.asatryan.libpgn.core.exception.FilterException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static com.asatryan.libpgn.core.parser.ExceptionBuilder.syntaxException;

public class PgnParser extends AbstractParser {
    private final Parser<List<TagPair>> tagPairParser;
    private final Parser<Game.Result> resultParser;
    private final InputParser<List<Movetext>, Byte> movetextSequenceParser;

    public PgnParser(@Nonnull Configuration config) {
        super(new PgnLexer(), config);
        tagPairParser = new TagPairParser(lexer, config);
        resultParser = new ResultParser(lexer, config);


        NagParser nagParser = new NagParser(lexer, config);
        CommentParser commentParser = new CommentParser(lexer, config);

        final VariationParser variation = new VariationParser(lexer, config);
        final MoveParser moveParser = new MoveParser(lexer, config, nagParser, commentParser, variation);
        final Parser<Movetext> movetextParser = new MovetextParser(lexer, config, moveParser);
        movetextSequenceParser = new MovetextSequenceParser(lexer, config, movetextParser);
        variation.setMovetextSequenceParser(movetextSequenceParser);
    }

    public PgnParser() {
        this(Configuration.defaultConfiguration());
    }

    public List<Game> parse(@Nonnull final String input) {
        return parse0(input.toCharArray());
    }

    public List<Game> parse(@Nonnull final char[] input) {
        return parse0(input);
    }

    private List<Game> parse0(final char[] input) {
        lexer.init(input);
        lexer.nextToken();

        List<Game> db = new ArrayList<>();

        while (lexer.lastToken() != UNDEFINED) {
            final byte token = lexer.lastToken();

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
