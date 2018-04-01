package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.core.GameIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PageablePgnParser extends AbstractPgnParser implements Iterable<List<Game>> {
    private final PageableGameIterator iterator;

    public PageablePgnParser(final PgnLexer lexer, final Configuration config, final Supplier<List<Game>> listSupplier, final int size) {
        super(lexer, config);
        iterator = new PageableGameIterator(new GameIterator(this), listSupplier, size);
    }

    @Override
    public Iterator<List<Game>> iterator() {
        return iterator;
    }

    private static class PageableGameIterator implements Iterator<List<Game>> {
        private final Iterator<Game> iterator;
        private final Supplier<List<Game>> listSupplier;
        private final int size;

        private PageableGameIterator(final Iterator<Game> iterator, final Supplier<List<Game>> listSupplier, final int size) {
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
