package com.github.nstdio.libpgn.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleFileNamingStrategyTest {

    @Test
    public void dummy() {
        final SimpleFileNamingStrategy simpleFileNamingStrategy = new SimpleFileNamingStrategy();

        final Path path = Paths.get("/home/edgar/Desktop/pgn/lichess_db_standard_rated_2013-01.pgn");

        Assertions.assertThat(simpleFileNamingStrategy.name(path, 1))
                .hasFileName("lichess_db_standard_rated_2013-01_01.pgn");

        Assertions.assertThat(simpleFileNamingStrategy.name(path, 12))
                .hasFileName("lichess_db_standard_rated_2013-01_12.pgn");
    }
}