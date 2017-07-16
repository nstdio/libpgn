package com.github.nstdio.libpgn.core.collector;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.TagPair;
import com.github.nstdio.libpgn.core.filter.Filters;
import com.github.nstdio.libpgn.core.internal.IntPair;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static com.github.nstdio.libpgn.core.Game.Result.BLACK;
import static com.github.nstdio.libpgn.core.Game.Result.WHITE;

public final class PgnCollectors {
    private PgnCollectors() {
    }

    /**
     * Returns a {@code Collector} that accumulates game result statistics according the last name of the player.
     *
     * @param lastName The last name of the player.
     *
     * @return a {@code Collector} that collects game statistics for {@code lastName} player.
     */
    public static Collector<? extends Game, ?, ResultStatistic> statistics(final String lastName) {
        Objects.requireNonNull(lastName);

        final Predicate<List<TagPair>> whiteLastNamePredicate = Filters.whiteLastNameEquals(lastName);
        final Predicate<List<TagPair>> blackLastNamePredicate = Filters.blackLastNameEquals(lastName);

        return Collector.of(MutableStatistics::new, (mutableStatistics, game) -> {
            if (whiteLastNamePredicate.test(game.tagPairSection())) {
                mutableStatistics.accept(game, WHITE, mutableStatistics.white);
            } else if (blackLastNamePredicate.test(game.tagPairSection())) {
                mutableStatistics.accept(game, BLACK, mutableStatistics.black);
            }
        }, MutableStatistics::combine, MutableStatistics::toImmutable);
    }

    /**
     * Returns a {@code Collector} that accumulates game result statistics from {@code Collection} according the last
     * name of the player.
     *
     * @param lastName The last name of the player.
     *
     * @return a {@code Collector} that collects game statistics for {@code lastName} player.
     */
    public static Collector<Collection<? extends Game>, ?, ResultStatistic> statisticsFromCollection(final String lastName) {
        Objects.requireNonNull(lastName);

        final Predicate<List<TagPair>> whiteLastNamePredicate = Filters.whiteLastNameEquals(lastName);
        final Predicate<List<TagPair>> blackLastNamePredicate = Filters.blackLastNameEquals(lastName);

        return Collector.of(MutableStatistics::new, (mutableStatistics, games) -> games.forEach(game -> {
            if (whiteLastNamePredicate.test(game.tagPairSection())) {
                mutableStatistics.accept(game, WHITE, mutableStatistics.white);
            } else if (blackLastNamePredicate.test(game.tagPairSection())) {
                mutableStatistics.accept(game, BLACK, mutableStatistics.black);
            }
        }), MutableStatistics::combine, MutableStatistics::toImmutable);
    }

    /**
     * Holds mutable data related to game results for white and black.
     */
    private static class MutableStatistics {
        private final ResultStatistics white = new ResultStatistics();
        private final ResultStatistics black = new ResultStatistics();

        MutableStatistics combine(final MutableStatistics other) {
            white.accept(other.white);
            black.accept(other.black);

            return this;
        }

        ResultStatistic toImmutable() {
            return new ResultStatistic(
                    IntPair.of(white.games, black.games),
                    IntPair.of(white.wins, black.wins),
                    IntPair.of(white.draws, black.draws)
            );
        }

        void accept(final Game game, final Game.Result winResult, final ResultStatistics resultStatistics) {
            resultStatistics.games++;

            if (game.gameResult() == winResult) {
                resultStatistics.wins++;
            } else if (game.gameResult() == Game.Result.DRAW) {
                resultStatistics.draws++;
            }
        }
    }

    /**
     * A higher abstraction that does not depend on the color of the player's pieces (white or black).
     */
    private static class ResultStatistics implements Consumer<ResultStatistics> {
        private int games;
        private int wins;
        private int draws;

        @Override
        public void accept(ResultStatistics other) {
            games += other.games;
            wins += other.wins;
            draws += other.draws;
        }

        @Override
        public String toString() {
            return "ResultStatistics{" +
                    "games=" + games +
                    ", wins=" + wins +
                    ", draws=" + draws +
                    '}';
        }
    }
}
