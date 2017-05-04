package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.parser.PgnParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.asatryan.libpgn.core.Configuration.defaultConfiguration;

public class PgnParserFactory {
    public static List<Game> from(File file, Charset charset, Configuration config) {
        PgnParser parser = new PgnParser(config);
        final char[] input = readFile(file.getPath(), charset);
        final List<Game> result = parser.parse(input);

        return Collections.unmodifiableList(result);
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
