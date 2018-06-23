package com.github.nstdio.libpgn.io;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PgnInputStream extends FilterInputStream {
    private static final int EOF = -1;

    PgnInputStream(final InputStream in) {
        super(Objects.requireNonNull(in));

        if (!in.markSupported()) {
            throw new IllegalArgumentException("The InputStream must support the mark/reset." +
                    " Probably should use use the java.io.BufferedInputStream");
        }
    }

    @Override
    public int read() throws IOException {
        final int read = super.read();

        if (read == EOF) {
            throw new EOFException();
        }

        return read;
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

    public int until(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) throws IOException {
        int c;
        int rc = 0;

        mark(0);
        do {
            c = read();
            rc++;

            if (c == i1 || c == i2 || c == i3 || c == i4 || c == i5 || c == i6 || c == i7 || c == i8 || c == i9 || c == i10) {
                reset();
                return rc;
            }

        } while (c != EOF);

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
