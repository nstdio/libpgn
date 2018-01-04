package com.github.nstdio.libpgn.core.io;

import java.io.*;
import java.util.function.IntPredicate;

public class PgnInputStream extends FilterInputStream {
    private static final int EOF = -1;

    public PgnInputStream(final InputStream in) {
        super(in.markSupported() ? in : new BufferedInputStream(in));
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
    public int until(final IntPredicate predicate) throws IOException {
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

    public int until(final int i1, final int i2, final int i3, final int i4, final int i5, final int i6,
                     final int i7, final int i8, final int i9, final int i10) throws IOException {
        int cur;
        int rc = 0;

        mark(0);
        do {
            cur = read();
            rc++;

            if (cur == i1 || cur == i2 || cur == i3 || cur == i4 || cur == i5 || cur == i6 || cur == i7
                    || cur == i8 || cur == i9 || cur == i10) {
                reset();
                return rc;
            }

        } while (cur != EOF);

        reset();

        return EOF;
    }

    public int until(final int i1, final int i2, final int i3, final int i4, final int i5) throws IOException {
        int cur;
        int rc = 0;

        mark(0);
        do {
            cur = read();
            rc++;

            if (cur == i1 || cur == i2 || cur == i3 || cur == i4 || cur == i5) {
                reset();
                return rc;
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
            }
        } while (isWhiteSpace);
    }
}