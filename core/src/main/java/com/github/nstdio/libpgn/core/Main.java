package com.github.nstdio.libpgn.core;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        benchFactory();
    }

    private static void benchFactory() {
        String path = "C:\\Users\\Asatryan\\Desktop\\pgn";

        GameFilter gf = GameFilter.builder()
                .movetextFilter(movetexts -> movetexts.size() == 1)
                .build();

        Configuration config = Configuration.defaultBuilder()
                .build();

        measure(() -> PgnParserFactory.fromDir(path, config));
    }

    private static void measure(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();

        System.out.printf("Rumtime: %d", System.currentTimeMillis() - startTime);
    }
}
