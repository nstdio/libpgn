package com.github.nstdio.libpgn.core;

import static com.github.nstdio.libpgn.core.Configuration.defaultConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.core.parser.PageablePgnParser;
import com.github.nstdio.libpgn.core.parser.PgnParser;
import com.github.nstdio.libpgn.entity.Game;

public class GameFactory {

    public static Stream<List<Game>> pageableStream(File file) {
        return pageableStream(file, defaultConfiguration());
    }

    public static Stream<List<Game>> pageableStream(File file, Configuration config) {
        return pageableStream(file, config, 8192);
    }

    public static Stream<List<Game>> pageableStream(File file, final int size) {
        return pageableStream(file, defaultConfiguration(), size);
    }

    public static Stream<List<Game>> pageableStream(File file, Configuration config, final int size) {
        return pageableStream(file, config, () -> listImpl(false), size);
    }

    public static Stream<List<Game>> pageableStream(File file, Configuration config, final Supplier<List<Game>> listSupplier,
                                                    final int size) {
        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(file);
        return StreamSupport.stream(new PageablePgnParser(lexer, config, listSupplier, size).spliterator(), false)
                .onClose(lexer::close);
    }

    public static Stream<List<Game>> pageableStream(final InputStream in, Configuration config, final Supplier<List<Game>> listSupplier,
                                                    final int size) {
        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(in);
        return StreamSupport.stream(new PageablePgnParser(lexer, config, listSupplier, size).spliterator(), false)
                .onClose(lexer::close);
    }

    /**
     * Creates the games stream.
     * <p>
     * WARNING: The caller must perform {@link Stream#close()} to free up resources.
     *
     * @param in     The input stream of PGN database to use when constructing lexer.
     * @param config The parser configuration.
     *
     * @return The stream of games.
     */
    public static Stream<Game> stream(InputStream in, Configuration config) {
        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(in);
        return StreamSupport.stream(new PgnParser(lexer, config).spliterator(), false)
                .onClose(lexer::close);
    }

    /**
     * Creates the games stream.
     * <p>
     * WARNING: The caller must perform {@link Stream#close()} to free up resources.
     *
     * @param file   The PGN database file.
     * @param config The parser configuration.
     *
     * @return The stream of games.
     */
    public static Stream<Game> stream(File file, Configuration config) {
        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(file);
        return StreamSupport.stream(new PgnParser(lexer, config).spliterator(), false)
                .onClose(lexer::close);
    }

    /**
     * Creates the games stream with default configuration.
     * <p>
     * WARNING: The caller must perform {@link Stream#close()} to free up resources.
     *
     * @param file The PGN database file.
     *
     * @return The stream of games.
     */
    public static Stream<Game> stream(File file) {
        return stream(file, defaultConfiguration());
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final String dir, final Configuration config) {
        return fromDir(dir, config, false);
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final String dir, final Configuration config, final boolean parallel) {
        return fromDir(new File(dir), config, parallel);
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final File dir) {
        return fromDir(dir, Configuration.defaultConfiguration());
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final String dir) {
        return fromDir(dir, Configuration.defaultConfiguration());
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final File dir, final Configuration config) {
        return fromDir(dir, config, false);
    }

    /**
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static Map<String, List<Game>> fromDir(final File dir, final Configuration config, final boolean parallel) {
        return fromDir(dir, config, parallel, null);
    }

    /**
     * Reads files from the directory and tries to parse them. If the file in the {@code dir} does not have the
     * extension .pgn, it will be ignored. In case where {@code parallel} enabled, according that fact that {@link
     * PgnParser} is not thread safe, for each file in {@code dir} will be created his own instance of parser, and as a
     * consequence, if {@code parallel} enabled and caller apply {@code config} with enabled {@link
     * Configuration#cacheTagPair} cache container will be shared across instances.
     *
     * @param dir       The directory to search pgn files.
     * @param config    The {@link PgnParser} configuration to apply.
     * @param parallel  Whether use parallel stream or not.
     * @param container The container to fill parsed data. Note that if {@code parallel} is {@code true} caller must
     *                  provide thread safe map implementation.
     *
     * @return The map where the keys are the name of the file, and the value is the list of games contained in this
     * file.
     */
    public static Map<String, List<Game>> fromDir(final File dir, final Configuration config, final boolean parallel, final Map<String, List<Game>> container) {
        final Map<String, List<Game>> map = container == null ? mapImpl(parallel) : container;
        fromDirInternal(dir, config, (file, gameList) -> map.put(file.getName(), gameList), parallel);

        return map;
    }

    /**
     * @see #flatFromDir(File, Configuration, boolean, List)
     */
    public static List<Game> flatFromDir(final String dir) {
        return flatFromDir(dir, Configuration.defaultConfiguration());
    }

    /**
     * @see #flatFromDir(File, Configuration, boolean, List)
     */
    public static List<Game> flatFromDir(final String dir, final Configuration config) {
        return flatFromDir(new File(dir), config);
    }

    /**
     * @see #flatFromDir(File, Configuration, boolean, List)
     */
    public static List<Game> flatFromDir(final File dir, final Configuration config) {
        return flatFromDir(dir, config, false);
    }

    /**
     * @see #flatFromDir(File, Configuration, boolean, List)
     */
    public static List<Game> flatFromDir(final File dir, final Configuration config, final boolean parallel) {
        return flatFromDir(dir, config, parallel, null);
    }

    /**
     * In contrast to the {@linkplain #fromDir(File, Configuration, boolean, Map)}, combine all the data into one list.
     *
     * @param dir       The directory to search pgn files.
     * @param config    The {@link PgnParser} configuration to apply.
     * @param parallel  Whether use parallel stream or not.
     * @param container The container to fill parsed data. Note that if {@code parallel} is {@code true} caller must
     *                  provide thread safe map implementation.
     *
     * @return Combined data of all parsed files.
     *
     * @see #fromDir(File, Configuration, boolean, Map)
     */
    public static List<Game> flatFromDir(final File dir, final Configuration config, final boolean parallel, final List<Game> container) {
        final List<Game> games = container == null ? listImpl(parallel) : container;
        fromDirInternal(dir, config, (file, gameList) -> games.addAll(gameList), parallel);

        return games;
    }

    /**
     * Common part for building containers from parsed directory parsed files.
     *
     * @param dir               The directory to search PGN files.
     * @param config            The configuration to apply on {@link PgnParser}.
     * @param containerConsumer The function which adds parsed data to container.
     * @param parallel          Whether use parallel stream or not.
     */
    private static void fromDirInternal(final File dir, final Configuration config,
                                        final BiConsumer<File, List<Game>> containerConsumer, final boolean parallel) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(config);

        final File[] list = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".pgn"));

        Optional.ofNullable(list)
                .map(Arrays::asList)
                .filter(files -> files.size() > 0)
                .ifPresent(files -> {
                    final Stream<File> fileStream = parallel ? files.parallelStream() : files.stream();
                    fileStream.forEach(file -> {
                        try (Stream<Game> gameStream = stream(file, config)) {
                            containerConsumer.accept(file, gameStream.collect(Collectors.toList()));
                        }
                    });
                });
    }

    /**
     * Chooses {@code Map} implementation according to {@code parallel}.
     *
     * @param parallel Whether use thread-safe map or not.
     * @param <K>      Type of keys.
     * @param <V>      Type of values.
     *
     * @return The {@code Map} according to {@code parallel}
     */
    private static <K, V> Map<K, V> mapImpl(final boolean parallel) {
        return parallel ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    /**
     * Chooses {@code List} implementation according to {@code parallel}.
     *
     * @param parallel Whether use thread-safe list or not.
     * @param <T>      The element type.
     *
     * @return The {@code List} according to {@code parallel}
     */
    private static <T> List<T> listImpl(final boolean parallel) {
        return parallel ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
    }
}
