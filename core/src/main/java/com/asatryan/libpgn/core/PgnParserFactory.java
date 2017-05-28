package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.parser.PgnParser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asatryan.libpgn.core.Configuration.defaultConfiguration;

public class PgnParserFactory {

    public static List<Game> from(PgnParser parser, File file, Charset charset) {
        final char[] input = readFile(file.getPath(), charset);

        return parser.parse(input);
    }

    public static List<Game> from(File file, Charset charset, Configuration config) {
        return from(new PgnParser(config), file, charset);
    }

    public static List<Game> from(File file, Configuration config) {
        return from(file, Charset.forName("UTF-8"), config);
    }

    public static List<Game> from(File file, Charset charset) {
        return from(file, charset, defaultConfiguration());
    }

    public static List<Game> from(File file) {
        return from(file, defaultConfiguration());
    }

    public static List<Game> from(String path, Charset charset, Configuration config) {
        return from(new File(path), charset, config);
    }

    public static List<Game> from(String path) {
        return from(new File(path));
    }

    public static List<Game> from(String path, Configuration configuration) {
        return from(new File(path), configuration);
    }

    public static List<Game> from(String path, Charset charset) {
        return from(path, charset, defaultConfiguration());
    }

    public static Map<String, List<Game>> fromDir(final @Nonnull String dir, final @Nonnull Configuration config) {
        return fromDir(new File(dir), config);
    }

    public static Map<String, List<Game>> fromDir(final @Nonnull File dir) {
        return fromDir(dir, Configuration.defaultConfiguration());
    }

    public static Map<String, List<Game>> fromDir(final @Nonnull File dir, final @Nonnull Configuration config) {
        final Map<String, List<Game>> map = new HashMap<>();

        final File[] list = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".pgn");
            }
        });

        if (list == null) {
            return map;
        }

        final PgnParser parser = new PgnParser(config);
        final Charset charset = Charset.forName("UTF-8");
        for (File file : list) {
            map.put(file.getName(), from(parser, file, charset));
        }

        return map;
    }

    private static char[] readFile(String path, Charset charset) {
        byte[] bytes = new byte[0];

        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes, charset).toCharArray();
    }
}
