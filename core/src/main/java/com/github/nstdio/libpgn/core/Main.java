package com.github.nstdio.libpgn.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        final File file = new File("/home/edgar/Desktop/pgn/lichess_db_standard_rated_2014-07.pgn");
        try (final Stream<List<Game>> listStream = GameFactory.pageableStream(file, 2048)) {

            listStream.forEach(games -> {

            });
        }
    }
}
