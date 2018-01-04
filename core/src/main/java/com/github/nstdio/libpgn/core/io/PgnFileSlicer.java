package com.github.nstdio.libpgn.core.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Represents cutting one large file into smaller pieces. Since large files are not practical to read completely, this
 * implementation will help to cut a huge file into smaller pieces.
 */
public class PgnFileSlicer implements Closeable {
    private static final OpenOption[] DEFAULT_OPEN_OPTIONS = {WRITE, CREATE_NEW};
    private final RandomAccessFile file;
    private final Path path;
    private final FileNamingStrategy fileNamingStrategy;
    private final int rwBufferSize;
    private final long chunkSize;

    /**
     * Creates a new instance of this class.
     *
     * @param file               The source file.
     * @param fileNamingStrategy The file naming strategy object to somehow name the output files.
     * @param bufferSize         The size of intermediate buffer to read into and then write from it on destination
     *                           file.
     * @param chunkSize          The estimated size for destination file. The output file typically will be a few bytes
     *                           bigger then this size.
     *
     * @throws FileNotFoundException if provided file does not exists.
     */
    public PgnFileSlicer(final File file, final FileNamingStrategy fileNamingStrategy, final int bufferSize,
                         final long chunkSize) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
        this.path = file.toPath();
        this.fileNamingStrategy = fileNamingStrategy;
        this.rwBufferSize = bufferSize;
        this.chunkSize = chunkSize;
    }

    private long[] marks() throws IOException {
        final List<Long> em = new ArrayList<>();
        final long fileSize = file.length();

        if (fileSize / chunkSize <= 1.0) {
            throw new IOException("Invalid chunkSize for file.");
        }

        long termination = chunkSize;

        while (termination < fileSize) {
            final long term = nearestGameTermination(termination);
            termination = term + chunkSize;
            em.add(term);
        }

        if (em.get(em.size() - 1) != fileSize) {
            em.add(fileSize);
        }

        final long[] marks = em.stream().mapToLong(value -> value).toArray();
        em.clear();

        return marks;
    }

    private long nearestGameTermination(final long seek) throws IOException {
        file.seek(seek);

        String line;

        do {
            line = file.readLine();
        }
        while (!(line.endsWith("1-0") || line.endsWith("1/2-1/2") || line.endsWith("0-1") || line.endsWith("*")));

        return file.getFilePointer();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    /**
     * Write chunks on the disk.
     *
     * @param options The options specifying how the new file is opened. If none specified the default options will be
     *                applied: {@link java.nio.file.StandardOpenOption#WRITE}, {@link java.nio.file.StandardOpenOption#CREATE_NEW}.
     *
     * @throws IOException if some I/O error occurred or if the ratio of the file size to the size of the chunk is less
     *                     than one.
     */
    public void write(OpenOption... options) throws IOException {
        options = options.length == 0 ? DEFAULT_OPEN_OPTIONS : options;

        // rewind
        file.seek(0);

        final byte[] rwBuffer = new byte[rwBufferSize];
        final long[] marks = marks();

        for (int i = 0; i < marks.length; i++) {
            write(marks[i], rwBuffer, fileNamingStrategy.name(path, i + 1), options);
        }
    }

    private void write(final long readUntil, final byte[] rwBuffer, final Path out, final OpenOption... options) throws IOException {
        try (final OutputStream outputStream = Files.newOutputStream(out, options)) {
            long pointer = file.getFilePointer();

            while (pointer < readUntil) {
                final int rem = (int) Math.min(readUntil - pointer, rwBufferSize);
                file.read(rwBuffer, 0, rem);
                outputStream.write(rwBuffer, 0, rem);

                pointer = file.getFilePointer();
            }
        }
    }
}
