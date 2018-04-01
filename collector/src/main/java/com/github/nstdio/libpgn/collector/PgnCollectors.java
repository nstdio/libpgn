package com.github.nstdio.libpgn.collector;

import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.filter.Filters;
import com.github.nstdio.libpgn.common.IntPair;
import com.github.nstdio.libpgn.common.Pair;
import com.github.nstdio.libpgn.common.StringUtils;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    public static Collector<? extends Game, ?, ResultStatistic> toResultStatistics(final String lastName) {
        final Pair<Predicate<List<TagPair>>, Predicate<List<TagPair>>> pair = lastNamePredicate(lastName);

        return Collector.of(MutableStatistics::new, (mutableStatistics, game) -> accept(pair, mutableStatistics, game), MutableStatistics::combine, MutableStatistics::toImmutable);
    }

    /**
     * Returns a {@code Collector} that accumulates game result toResultStatistics from {@code Collection} according the
     * last name of the player.
     *
     * @param lastName The last name of the player.
     *
     * @return a {@code Collector} that collects game toResultStatistics for {@code lastName} player.
     */
    public static Collector<Collection<? extends Game>, ?, ResultStatistic> toResultStatisticsFromCollection(final String lastName) {
        final Pair<Predicate<List<TagPair>>, Predicate<List<TagPair>>> predicatePair = lastNamePredicate(lastName);

        return Collector.of(MutableStatistics::new, (mutableStatistics, games) -> games.forEach(game ->
                accept(predicatePair, mutableStatistics, game)), MutableStatistics::combine, MutableStatistics::toImmutable);
    }

    public static Collector<Game, ?, Map<String, OpeningStatistics>> toOpeningResultStatisticsMap(final String lastName, final Supplier<Map<String, OpeningStatistics>> mapSupplier) {
        final Pair<Predicate<List<TagPair>>, Predicate<List<TagPair>>> pair = lastNamePredicate(lastName);

        return Collectors.toMap(Game::eco, game -> {
            final MutableStatistics mutableStats = new MutableStatistics();
            accept(pair, mutableStats, game);

            return new OpeningStatistics(game.eco(), mutableStats.toImmutable());
        }, OpeningStatistics::merge, mapSupplier);
    }

    public static Collector<Game, ?, Map<String, OpeningStatistics>> toOpeningResultStatisticsMap(final String lastName) {
        return toOpeningResultStatisticsMap(lastName, TreeMap::new);
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByEco() {
        return groupingByTag("ECO");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingBySite() {
        return groupingByTag("Site");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByEvent() {
        return groupingByTag("Event");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByDate() {
        return groupingByTag("Date");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByWhite() {
        return groupingByTag("White");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByBlack() {
        return groupingByTag("Black");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByResult() {
        return groupingByTag("Result");
    }

    public static Collector<Game, ?, Map<String, List<Game>>> groupingByTag(final String tag) {
        return Collectors.groupingBy(game -> StringUtils.nullTo(game.tag(tag), "EMPTY_IDENITY"));
    }

    private static void accept(final Pair<Predicate<List<TagPair>>, Predicate<List<TagPair>>> pair, final MutableStatistics mutableStats, final Game game) {
        if (pair.first.test(game.tagPairSection())) {
            mutableStats.accept(game, Result.WHITE, mutableStats.white);
        } else if (pair.second.test(game.tagPairSection())) {
            mutableStats.accept(game, Result.BLACK, mutableStats.black);
        }
    }

    private static Pair<Predicate<List<TagPair>>, Predicate<List<TagPair>>> lastNamePredicate(final String lastName) {
        Objects.requireNonNull(lastName);

        return Pair.of(Filters.whiteLastNameEquals(lastName), Filters.blackLastNameEquals(lastName));
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

        void accept(final Game game, final Result winResult, final ResultStatistics resultStatistics) {
            resultStatistics.games++;

            if (game.gameResult() == winResult) {
                resultStatistics.wins++;
            } else if (game.gameResult() == Result.DRAW) {
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
