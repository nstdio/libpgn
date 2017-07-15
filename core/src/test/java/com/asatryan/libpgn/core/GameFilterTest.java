package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.GameFilter.GameFilterBuilder;
import com.asatryan.libpgn.core.parser.PgnParser;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.List;

import static com.asatryan.libpgn.core.Matchers.size;

public class GameFilterTest {
    private static byte[] data;
    private static List<Game> games;
    private GameFilterBuilder builder;

    @BeforeClass
    public static void setUpClass() throws IOException {
        data = IOUtils.toByteArray(GameFilterTest.class.getResource("/Aronian.pgn"));

        games = parseData();
        Assert.assertThat(games, size(2228));
    }

    private static List<Game> parseData() {
        return parseData(null);
    }

    private static List<Game> parseData(final GameFilter gameFilter) {
        Configuration config = Configuration
                .defaultBuilder()
                .gameFilter(gameFilter)
                .build();

        return new PgnParser(config).parse(data);
    }

    @Before
    public void setUp() {
        builder = GameFilter.builder();
    }
}