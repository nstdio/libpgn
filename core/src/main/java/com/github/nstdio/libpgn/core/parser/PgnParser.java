package com.github.nstdio.libpgn.core.parser;

import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.GameIterator;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;

/**
 * The main entry point to parsing process.
 */
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
              final Parser<Result> resultParser,
              final InputParser<List<MoveText>, Byte> movetextSequenceParser,
              final Parser<byte[]> commentParser) {
        super(lexer, config, tagPairParser, resultParser, movetextSequenceParser, commentParser);
    }

    /**
     * Parses remaining games and materialize it as a {@code Game}s.
     *
     * @deprecated This is method is very high level at should not be supported. Please use {@link #stream()} instead.
     */
    @Deprecated
    public List<Game> parse() {
        try (Stream<Game> stream = stream()) {
            return stream.collect(toList());
        }
    }

    /**
     * Creates the game stream from games. Sequentially parsing input and applying it as a stream element.
     * <p>
     * Note that returned stream should be {@code close}d after use.
     *
     * @return The sequential stream of games.
     */
    public Stream<Game> stream() {
        return StreamSupport.stream(spliterator(), false)
                .onClose(lexer::close);
    }

    @Nonnull
    @Override
    public Iterator<Game> iterator() {
        return new GameIterator(this);
    }

    @Override
    public Spliterator<Game> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE & Spliterator.NONNULL);
    }
}
