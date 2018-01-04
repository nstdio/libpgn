package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.Movetext;
import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.core.parser.PgnParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.github.nstdio.libpgn.core.filter.Filters.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class MoveCountFilterTest {

    private static List<Game> games;

    @BeforeClass
    public static void setUp() throws Exception {
        final InputStream file = MoveCountFilterTest.class.getClassLoader().getResourceAsStream("Aronian.pgn");

        games = new PgnParser(InputStreamPgnLexer.of(file)).parse();
    }

    @Test
    public void dummy() {
        final int moveCount = 32;

        assertThatPredicate(moveCountEquals(moveCount), moves -> assertThat(moves).hasSize(moveCount));
        assertThatPredicate(moveCountLessThen(moveCount), moves -> assertThat(moves.size()).isLessThan(moveCount));
        assertThatPredicate(moveCountLessThenOrEquals(moveCount), moves -> assertThat(moves.size()).isLessThanOrEqualTo(moveCount));
        assertThatPredicate(moveCountGreaterThen(moveCount), moves -> assertThat(moves.size()).isGreaterThan(moveCount));
        assertThatPredicate(moveCountGreaterThenOrEquals(moveCount), moves -> assertThat(moves.size()).isGreaterThanOrEqualTo(moveCount));
    }

    private void assertThatPredicate(final Predicate<List<Movetext>> predicate, final Consumer<List<Movetext>> assertion) {
        games.stream().filter(game -> predicate.test(game.moves())).map(Game::moves).collect(toList())
                .forEach(assertion);
    }
}
