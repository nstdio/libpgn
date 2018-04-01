package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.core.GameIterator;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class PgnParser extends AbstractPgnParser implements Iterable<Game> {
    public PgnParser(final PgnLexer lexer) {
        this(lexer, Configuration.defaultConfiguration());
    }

    public PgnParser(final PgnLexer lexer, final Configuration config) {
        super(lexer, config);

    }

    /**
     * For testing propose only
     */
    PgnParser(final PgnLexer lexer, final Configuration config, final Parser<List<TagPair>> tagPairParser,
              final Parser<Result> resultParser, final InputParser<List<MoveText>, Byte> movetextSequenceParser) {
        super(lexer, config, tagPairParser, resultParser, movetextSequenceParser);
    }

    @Deprecated
    public List<Game> parse() {
        return StreamSupport.stream(spliterator(), false).collect(toList());
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
