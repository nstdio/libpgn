package com.github.nstdio.libpgn.io;

import static com.github.nstdio.libpgn.common.ExceptionUtils.wrapChecked;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.annotation.Nonnull;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * The central point for creating an instance of {@code PgnInputStream}. This factory can deal with not just plain
 * {@code .pgn} files but with archived files too.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PgnInputStreamFactory {
    private static final Map<Pattern, Function<File, InputStream>> producers;
    private static final CompressorStreamFactory STREAM_FACTORY;

    static {
        final StreamProducer plain = new StreamProducer();

        producers = new LinkedHashMap<>();

        producers.put(Pattern.compile("\\.pgn$"), LoggingInputStreamProducer.of(plain.andThen(buffered())));

        if (commonsCompressAtClasspath()) {
            STREAM_FACTORY = new CompressorStreamFactory(true);

            producers.put(Pattern.compile("\\.(tar\\.[xg]z)$"), LoggingInputStreamProducer.of(new ArchiveStreamProducer(plain)));
            producers.put(Pattern.compile("\\.bz2$"), LoggingInputStreamProducer.of(new BZip2SteamProducer(plain)));
            producers.put(Pattern.compile("\\.7z$"), LoggingInputStreamProducer.of(SevenZipStreamProducer.of()));
            producers.put(Pattern.compile("\\.zip$"), LoggingInputStreamProducer.of(new ZipFileStreamProducer()));
            producers.put(Pattern.compile("\\.rar$"), NoOpStreamProducer.of());
        } else {
            STREAM_FACTORY = null;
        }
    }

    /**
     * Creates the {@code PgnInputStream} from provided file.
     *
     * @param file The input file.
     *
     * @return The input stream.
     */
    public static PgnInputStream of(final File file) {
        return ofFile(file);
    }

    /**
     * Creates the {@code PgnInputStream} from provided input stream.
     *
     * @param in The source input stream.
     *
     * @return The wrapped input stream.
     *
     * @throws IllegalArgumentException When {@code in} does not support marking.
     */
    public static PgnInputStream of(final InputStream in) {
        if (in instanceof PgnInputStream) {
            log.info("The input is already PgnInputStream, do no constructing new object.");
            return (PgnInputStream) in;
        }

        return new PgnInputStream(in);
    }

    private static PgnInputStream ofFile(final File file) {
        final String fileName = Objects.requireNonNull(file).getName();

        return producers.entrySet().stream()
                .filter(mapEntry -> mapEntry.getKey().matcher(fileName).find())
                .findFirst().map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot find reader for file: %s", file.getPath())))
                .andThen(PgnInputStreamFactory::of)
                .apply(file);
    }

    private static boolean commonsCompressAtClasspath() {
        try {
            Class.forName("org.apache.commons.compress.compressors.CompressorStreamFactory");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Nonnull
    private static Function<InputStream, BufferedInputStream> buffered() {
        return is -> is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    @Nonnull
    private static Function<InputStream, CompressorInputStream> compressorStream() {
        return is -> wrapChecked(() -> STREAM_FACTORY.createCompressorInputStream(is));
    }

    private static Function<InputStream, InputStream> bufferedCompressorStream() {
        return is -> compressorStream()
                .compose(buffered())
                .andThen(buffered())
                .apply(is);
    }

    @Nonnull
    private static Function<InputStream, ArchiveInputStream> archiveStream() {
        return is -> wrapChecked(() -> new ArchiveStreamFactory().createArchiveInputStream(is));
    }

    /**
     * Plain old ZIP archives.
     */
    private static class ZipFileStreamProducer implements Function<File, InputStream> {
        @Override
        public InputStream apply(final File file) {
            final ZipFile zipFile = wrapChecked(() -> new ZipFile(file));

            return zipFile.stream()
                    .map(entry -> wrapChecked(() -> zipFile.getInputStream(entry)))
                    .reduce(SequenceInputStream::new)
                    .map(buffered())
                    .orElseThrow(() -> new IllegalArgumentException("Cannot create InputStream from File: "
                            + file.getAbsoluteFile())
                    );
        }
    }

    /**
     * Just creates input stream from file. Can be used for plain PGN files.
     */
    private static class StreamProducer implements Function<File, InputStream> {
        @Override
        public InputStream apply(final File file) {
            return wrapChecked(() -> Files.newInputStream(file.toPath(), StandardOpenOption.READ));
        }
    }

    /**
     * The empty stub for any unknown/unsupported file formats.
     */
    @NoArgsConstructor(staticName = "of")
    private static class NoOpStreamProducer implements Function<File, InputStream> {
        @Override
        public InputStream apply(final File file) {
            throw new UnsupportedOperationException(String.format("Cannot create InputStream from File: %s", file.getAbsoluteFile()));
        }
    }

    /**
     * The specialized class to handle 7z archives.
     */
    @NoArgsConstructor(staticName = "of")
    private static class SevenZipStreamProducer implements Function<File, InputStream> {
        @Override
        public InputStream apply(final File file) {
            return new BufferedInputStream(SevenZFileInputStream.of(
                    wrapChecked(() -> {
                        final SevenZFile sevenZFile = new SevenZFile(file);
                        sevenZFile.getNextEntry();

                        return sevenZFile;
                    })
            ));
        }
    }

    /**
     * The bz2 archives.
     */
    @ToString
    private static class BZip2SteamProducer extends DelegatingStreamProducer {

        private BZip2SteamProducer(final StreamProducer delegate) {
            super(delegate);
        }

        @Override
        public InputStream apply(final File file) {
            return delegate
                    .andThen(bufferedCompressorStream())
                    .apply(file);
        }
    }

    /**
     * Used for archives.
     */
    @ToString
    private static class ArchiveStreamProducer extends DelegatingStreamProducer {

        private ArchiveStreamProducer(final StreamProducer delegate) {
            super(delegate);
        }

        @Override
        public InputStream apply(final File file) {
            return delegate
                    .andThen(bufferedCompressorStream())
                    .andThen(archiveStream())
                    .andThen(ais -> {
                        wrapChecked(ais::getNextEntry);
                        return new IteratingArchiveInputStream(ais);
                    })
                    .andThen(buffered())
                    .apply(file);
        }
    }

    /**
     * Forwards the {@code InputStream} creation to it's delegate.
     */
    @RequiredArgsConstructor(staticName = "of")
    private static class DelegatingStreamProducer implements Function<File, InputStream> {
        @NonNull
        final Function<File, InputStream> delegate;

        @Override
        public InputStream apply(final File file) {
            return delegate.apply(file);
        }
    }

    /**
     * The logging wrapper for producer functions.
     */
    @ToString
    private static class LoggingInputStreamProducer extends DelegatingStreamProducer {

        private LoggingInputStreamProducer(final Function<File, InputStream> delegate) {
            super(delegate);
        }

        @Override
        public InputStream apply(final File file) {
            log.info("Trying to convert file to InputStream using: {}", delegate.getClass().getName());

            if (log.isDebugEnabled()) {
                log.debug("File: {}", file.getAbsolutePath());
                log.debug("File Size: {} bytes", file.length());
                log.debug("File Readable: {}", file.canRead());
            }

            try {
                final InputStream is = delegate.apply(file);
                log.info("Producer successfully converted file to input stream: {}", is.getClass().getName());

                return is;
            } catch (RuntimeException e) {
                log.error("Producer encountered the exception.", e);

                throw e;
            }
        }
    }

    /**
     * Wrapping {@code SevenZFile} into {@code InputStream}.
     */
    @RequiredArgsConstructor(staticName = "of")
    private static class SevenZFileInputStream extends InputStream {
        @NonNull
        private final SevenZFile file;

        @Override
        public int read(@Nonnull final byte[] b) throws IOException {
            return file.read(b);
        }

        @Override
        public int read(@Nonnull final byte[] b, final int off, final int len) throws IOException {
            return file.read(b, off, len);
        }

        @Override
        public int read() throws IOException {
            return file.read();
        }

        @Override
        public void close() throws IOException {
            file.close();
        }
    }

    /**
     * To help read all entries in archive.
     */
    @Slf4j
    @ToString
    private static class IteratingArchiveInputStream extends FilterInputStream {
        @NonNull
        private final ArchiveInputStream in;

        private IteratingArchiveInputStream(final ArchiveInputStream in) {
            super(in);
            this.in = in;
        }

        @Override
        public int read(@Nonnull final byte[] buff, final int off, final int len) throws IOException {
            final int read = in.read(buff, off, len);

            return read == -1 ? in.getNextEntry() == null ? read : in.read(buff, off, len) : read;
        }
    }
}
