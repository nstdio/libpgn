package com.github.nstdio.libpgn.io;

import java.io.Closeable;
import java.io.UncheckedIOException;

import com.github.nstdio.libpgn.entity.Game;

/**
 * Stream oriented {@code Game} writer.
 *
 * @see PgnOutputStreamImpl
 */
public interface PgnOutputStream extends Closeable {
    /**
     * Writes the game to the underlying stream.
     *
     * @param game The game the should be written.
     *
     * @throws UncheckedIOException When some I/O error encounters.
     */
    void write(Game game);

    /**
     * Writes multiple games to the underlying stream.
     *
     * @param games The games to write.
     *
     * @throws UncheckedIOException When some I/O error encounters.
     */
    default void write(Iterable<Game> games) {
        games.forEach(this::write);
    }
}