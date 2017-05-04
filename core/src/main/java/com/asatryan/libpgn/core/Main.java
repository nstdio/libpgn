package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.parser.PgnParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        bench();
    }

    private static void benchFactory() {
        String path = "C:\\Users\\Asatryan\\Desktop\\game.pgn";

        final List<Game> games = PgnParserFactory.from(path);

        System.out.println(Arrays.toString(games.toArray()));
    }

    private static void bench() throws IOException {
        String input = readFile("C:\\Users\\Asatryan\\Downloads\\lichess_pgn_2017.04.16_GianMy_vs_ed_asatryan.5z3tyWKR.pgn");

        Configuration config = Configuration.defaultBuilder()
                .cacheTagPair(true)
                .build();

        PgnParser parser = new PgnParser(config);
        final int count = 500;
        long avgMs = 0, maxMs = Integer.MIN_VALUE, minMs = Integer.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            long startTime = System.currentTimeMillis();

            parser.parse(input);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            avgMs += totalTime;
            maxMs = Math.max(maxMs, totalTime);
            minMs = Math.min(minMs, totalTime);
            System.out.format("#%d. %d ms.\n", i + 1, totalTime);
        }

        System.out.format("Average: %d ms, Max: %d ms, Min: %d ms.\n", avgMs / count, maxMs, minMs);
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));

        return new String(encoded, Charset.forName("UTF-8"));
    }
}
