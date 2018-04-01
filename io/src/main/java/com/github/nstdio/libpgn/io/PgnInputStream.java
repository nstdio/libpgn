package com.github.nstdio.libpgn.io;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.IntPredicate;

public abstract class PgnInputStream extends FilterInputStream {
    private static final int EOF = -1;

    PgnInputStream(final InputStream in) {
        super(Objects.requireNonNull(in));
    }

    @Override
    public int read() throws IOException {
        final int read = super.read();

        if (read == EOF) {
            throw new EOFException();
        }

        return read;
    }

    /**
     * Reads from the stream bytes until first occurrence of {@code b}. Subsequent call of {@link #read()} should return
     * the {@code b} byte.
     *
     * @param predicate The terminator byte.
     *
     * @return The necessary reads count to reach the {@code b} or {@code -1} when stream is ended or {@code b} never
     * occurred.
     *
     * @throws IOException if an I/O error occurs.
     */
    int until(final IntPredicate predicate) throws IOException {
        int current;
        int rc = 0;

        mark(0);
        do {
            current = read();
            rc++;

            if (predicate.test(current)) {
                reset();
                return rc;
            }

        } while (current != EOF);

        reset();

        return EOF;
    }

    public int until(final int i1) throws IOException {
        int current;
        int rc = 0;

        mark(0);
        do {
            current = read();
            rc++;

            if (current == i1) {
                reset();
                return rc;
            }

        } while (current != EOF);

        reset();

        return EOF;
    }

    public int until(final int[] stops) throws IOException {
        int cur;
        int rc = 0;

        mark(0);
        do {
            cur = read();
            rc++;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, sz = stops.length; i < sz; i++) {
                if (cur == stops[i]) {
                    reset();
                    return rc;
                }
            }

        } while (cur != EOF);

        reset();

        return EOF;
    }

    /**
     * Sequentially reads the {@code offset} bytes and returns the last {@link #read()} result. After reads the position
     * should not be affected.
     *
     * @param offset The number of reads that need to be performed.
     *
     * @return The last result of sequentially read calling or {@code -1} when stream is ended before perform necessary
     * count of reads.
     *
     * @throws IOException              if an I/O error occurs.
     * @throws IllegalArgumentException if {@code offset} is negative or zero.
     */
    public int readAhead(final int offset) throws IOException {

        if (offset <= 0) {
            throw new IllegalArgumentException("offset must be positive.");
        }

        if (offset == 1) {
            return readAhead1();
        }

        mark(offset);

        int rc = 0;
        int read = EOF;

        while (rc < offset) {
            try {
                read = read();
                rc++;

            } catch (EOFException e) {
                reset();
                throw e;
            }
        }

        reset();

        return read;
    }

    private int readAhead1() throws IOException {
        mark(1);

        final int read = read();

        reset();

        return read;
    }

    public void skipWhiteSpace() throws IOException {
        boolean isWhiteSpace;

        do {
            mark(1);

            // don't want to throw {@code EOFException}
            switch (in.read()) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                case '\0':
                    isWhiteSpace = true;
                    break;
                default:
                    isWhiteSpace = false;
                    reset();
                    break;
            }
        } while (isWhiteSpace);
    }
}
