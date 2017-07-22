package com.github.nstdio.libpgn.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.nstdio.libpgn.core.filter.Filters.blackMates;
import static com.github.nstdio.libpgn.core.filter.Filters.whiteMates;

public class Main {

    public static void main(String[] args) throws IOException {
        benchFactory();
    }

    private static void benchFactory() {
        String path = "C:\\Users\\Asatryan\\Desktop\\pgn";

        Configuration config = Configuration.defaultBuilder()
                .gameFilter()
                .movetextFilter(whiteMates().or(blackMates()))
                .build();

        measure(() -> {
            final Map<String, List<Game>> games = PgnParserFactory.fromDir(path, config);
            System.out.println("a");
        });
    }

    private static void measure(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();

        System.out.printf("Rumtime: %d", System.currentTimeMillis() - startTime);
    }
}
