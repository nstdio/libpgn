package com.github.nstdio.libpgn.core.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.GameIterator;
import com.github.nstdio.libpgn.entity.Game;

/**
 * The parser that parses and produces objects PGN file in certain size chunks. This implementation is optimal to parse
 * reasonably large PGN databases.
 */
public class PageablePgnParser extends AbstractPgnParser implements Iterable<List<Game>> {
    private final PageableGameIterator iterator;

    /**
     * Constructs the new instance of {@code PageablePgnParser}.
     *
     * @param lexer        The lexer to produce tokens.
     * @param config       The parser configuration.
     * @param listSupplier The supplier to produce collection container for games.
     * @param size         The maximum size of each page.
     */
    public PageablePgnParser(final PgnLexer lexer, final Configuration config, final Supplier<List<Game>> listSupplier,
                             final int size) {
        super(lexer, config);
        iterator = new PageableGameIterator(new GameIterator(this), listSupplier, size);
    }

    /**
     * Constructs the new instance of {@code PageablePgnParser} using {@link ArrayList} as a games container.
     *
     * @param lexer  The lexer to produce tokens.
     * @param config The parser configuration.
     * @param size   The maximum size of each page.
     */
    public PageablePgnParser(final PgnLexer lexer, final Configuration config, final int size) {
        this(lexer, config, ArrayList::new, size);
    }

    @Nonnull
    @Override
    public Iterator<List<Game>> iterator() {
        return iterator;
    }

    /**
     * Iterator accumulating certain number of parsed games and produces it via {@link #next()} method.
     */
    private static class PageableGameIterator implements Iterator<List<Game>> {
        private final Iterator<Game> iterator;
        private final Supplier<List<Game>> listSupplier;
        private final int size;

        private PageableGameIterator(final Iterator<Game> iterator, final Supplier<List<Game>> listSupplier,
                                     final int size) {
            this.iterator = Objects.requireNonNull(iterator);
            this.listSupplier = Objects.requireNonNull(listSupplier);

            if (size <= 0) {
                throw new IllegalArgumentException("size must be positive.");
            }

            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public List<Game> next() {
            final List<Game> slice = listSupplier.get();

            while (hasNext() && slice.size() < size) {
                slice.add(iterator.next());
            }

            return slice;
        }
    }
}
