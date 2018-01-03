package com.github.nstdio.libpgn.core;

import com.github.nstdio.libpgn.core.parser.PgnParser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class GameIterator implements Iterator<Game> {
    private final PgnParser parser;
    private Game next;

    public GameIterator(final PgnParser parser) {
        this.parser = Objects.requireNonNull(parser);
    }

    @Override
    public boolean hasNext() {
        if (next == null) {
            advance();
        }

        return next != null;
    }

    private void advance() {
        next = parser.next();
    }

    @Override
    public Game next() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        final Game game = next;

        advance();

        return game;
    }
}
