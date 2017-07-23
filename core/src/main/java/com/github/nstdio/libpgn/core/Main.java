package com.github.nstdio.libpgn.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        benchFactory();
    }

    private static void benchFactory() {
        String path = "C:\\Users\\Asatryan\\Desktop\\pgn";

        final Configuration config = Configuration.defaultBuilder()
                .tagPairCacheSize(1024 * 16).build();

        final List<Game> games = PgnParserFactory.flatFromDir(new File(path), config, true);
    }

    private static void measure(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();

        System.out.printf("Rumtime: %d", System.currentTimeMillis() - startTime);
    }
}
