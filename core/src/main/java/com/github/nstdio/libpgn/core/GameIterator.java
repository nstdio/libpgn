package com.github.nstdio.libpgn.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.github.nstdio.libpgn.core.parser.AbstractPgnParser;
import com.github.nstdio.libpgn.entity.Game;

public class GameIterator implements Iterator<Game> {
    private final AbstractPgnParser parser;
    private Game next;

    public GameIterator(final AbstractPgnParser parser) {
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
