package com.github.nstdio.libpgn.core;

import com.github.nstdio.libpgn.core.internal.EmptyArrays;
import com.github.nstdio.libpgn.core.parser.PgnParser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.github.nstdio.libpgn.core.Configuration.defaultConfiguration;

public class PgnParserFactory {

    public static List<Game> from(PgnParser parser, File file) {
        return parser.parse(readFile(file.getPath()));
    }

    public static List<Game> from(File file, Configuration config) {
        return from(new PgnParser(config), file);
    }

    public static List<Game> from(File file) {
        return from(file, defaultConfiguration());
    }

    public static List<Game> from(String path) {
        return from(new File(path));
    }

    public static List<Game> from(String path, Configuration configuration) {
        return from(new File(path), configuration);
    }

    public static Map<String, List<Game>> fromDir(@Nonnull final String dir, @Nonnull final Configuration config) {
        return fromDir(new File(dir), config);
    }

    public static Map<String, List<Game>> fromDir(@Nonnull final File dir) {
        return fromDir(dir, Configuration.defaultConfiguration());
    }

    public static Map<String, List<Game>> fromDir(@Nonnull final String dir) {
        return fromDir(dir, Configuration.defaultConfiguration());
    }

    public static Map<String, List<Game>> fromDir(@Nonnull final File dir, @Nonnull final Configuration config) {
        final File[] list = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".pgn"));

        if (list == null) {
            return Collections.emptyMap();
        }

        final Map<String, List<Game>> map = new HashMap<>(list.length);

        final PgnParser parser = new PgnParser(config);

        Arrays.asList(list)
                .forEach(file -> map.put(file.getName(), from(parser, file)));

        return map;
    }

    private static byte[] readFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EmptyArrays.EMPTY_BYTE_ARRAY;
    }
}
