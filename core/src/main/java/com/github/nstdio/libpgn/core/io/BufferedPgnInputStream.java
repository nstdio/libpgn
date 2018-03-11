package com.github.nstdio.libpgn.core.io;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 *
 */
class BufferedPgnInputStream extends PgnInputStream {
    static final int DEFAULT_SIZE = 8192 * 2;

    BufferedPgnInputStream(final BufferedInputStream bis) {
        super(bis);
    }

    BufferedPgnInputStream(final InputStream in) {
        this(in, DEFAULT_SIZE);
    }

    BufferedPgnInputStream(final InputStream in, final int size) {
        this(new BufferedInputStream(in, size));
    }
}
