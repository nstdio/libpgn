package com.github.nstdio.libpgn.core.io;

import org.junit.Test;

import java.io.File;

public class PgnFileSlicerTest {
    @Test
    public void dummy() throws Exception {
        final File file = new File("/home/edgar/Desktop/pgn/lichess_db_standard_rated_2013-12.pgn");

        final long chunkSize = 1_000_000 * 64; // 2mb
        final int rwBufferSize = 8192 * 2;

        try (final PgnFileSlicer slicer = new PgnFileSlicer(file, new SimpleFileNamingStrategy(), rwBufferSize, chunkSize);) {
            slicer.write();
        }
    }

    @Test
    public void dummy2() throws Exception {
        final File file = new File("/home/edgar/Desktop/pgn/2.pgn");

        final long chunkSize = 400; // 2mb
        final int rwBufferSize = 8192 * 2;

        final PgnFileSlicer slicer = new PgnFileSlicer(file, new SimpleFileNamingStrategy(), rwBufferSize, chunkSize);

        slicer.write();
        slicer.close();
    }
}