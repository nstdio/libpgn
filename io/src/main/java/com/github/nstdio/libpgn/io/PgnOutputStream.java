package com.github.nstdio.libpgn.io;

import com.github.nstdio.libpgn.entity.Game;

import java.io.Closeable;
import java.io.UncheckedIOException;

public interface PgnOutputStream extends Closeable {
    /**
     * Writes the game to the underlying stream.
     *
     * @param game The game the should be written.
     *
     * @throws UncheckedIOException When some I/O error encounters.
     */
    void write(Game game);

    default void write(Iterable<Game> games) {
        games.forEach(this::write);
    }
}