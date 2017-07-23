package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.*;
import com.github.nstdio.libpgn.core.internal.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.nstdio.libpgn.core.filter.TagPairFilter.Elo.*;
import static com.github.nstdio.libpgn.core.filter.TagPairFilter.LastNameEquals;

/**
 * This is entry point for all filters.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Filters {
    private Filters() {
    }

    /**
     * Filter object to filter by tag pair name and value.
     *
     * @param name  The tag pair name.
     * @param value The tag pair value.
     *
     * @return The filter object.
     */
    public static Predicate<List<TagPair>> tagEquals(final String name, final String value) {
        return new TagPairFilter(name, value);
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "1-0".
     *
     * @return The filter object.
     */
    public static Predicate<List<TagPair>> whiteWins() {
        return tagEquals("Result", "1-0");
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "0-1".
     *
     * @return The filter object.
     */
    public static Predicate<List<TagPair>> blackWins() {
        return tagEquals("Result", "0-1");
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "1/2-1/2".
     *
     * @return The filter object.
     */
    public static Predicate<List<TagPair>> draw() {
        return tagEquals("Result", "1/2-1/2");
    }

    /**
     * Filters on the full name of the White player in case sensitive manner. Shorthand for {@link #tagEquals(String,
     * String)} <p>Examples:
     * <blockquote><pre>
     * [White "Kasparov, Garry"] -> whiteEquals("kasparov, garry") // false.
     * [White "Kasparov, Garry"] -> whiteEquals("Kasparov, Garry") // true.
     * [White "Kasparov, G"] -> whiteEquals("Kasparov, Garry") // false.
     * </pre>
     * </blockquote>
     *
     * @param whiteName The White player full name.
     *
     * @return The filter object.
     */
    public static Predicate<List<TagPair>> whiteEquals(final String whiteName) {
        return tagEquals("White", whiteName);
    }

    /**
     * Filters on the full name of the Black player in case sensitive manner. Shorthand for {@link #tagEquals(String,
     * String)}
     *
     * @param blackName The Black player full name.
     *
     * @return The filter object.
     * @see Filters#whiteEquals(String)
     * @see Filters#tagEquals(String, String)
     */
    public static Predicate<List<TagPair>> blackEquals(final String blackName) {
        return tagEquals("Black", blackName);
    }

    public static Predicate<List<TagPair>> eventEquals(final String event) {
        return tagEquals("Event", event);
    }

    public static Predicate<List<TagPair>> siteEquals(final String site) {
        return tagEquals("Site", site);
    }

    public static Predicate<List<TagPair>> ecoEquals(final String eco) {
        if (!StringUtils.isEco(eco.toUpperCase())) {
            throw new IllegalArgumentException("eco not valid");
        }

        return tagEquals("ECO", eco);
    }

    public static Predicate<List<TagPair>> whiteLastNameEquals(final String whiteLastName) {
        return LastNameEquals.whiteLastNameEquals(whiteLastName);
    }

    public static Predicate<List<TagPair>> blackLastNameEquals(final String blackLastName) {
        return LastNameEquals.blackLastNameEquals(blackLastName);
    }

    public static Predicate<List<TagPair>> lastNameEquals(final String playerLastName) {
        return whiteLastNameEquals(playerLastName).or(blackLastNameEquals(playerLastName));
    }

    public static Predicate<List<TagPair>> whiteEloGreaterThen(final int elo) {
        return whiteGreaterThen(elo);
    }

    public static Predicate<List<TagPair>> whiteEloLessThen(final int elo) {
        return whiteLessThen(elo);
    }

    public static Predicate<List<TagPair>> blackEloGreaterThen(final int elo) {
        return blackGreaterThen(elo);
    }

    public static Predicate<List<TagPair>> blackEloLessThen(final int elo) {
        return blackLessThen(elo);
    }

    public static Predicate<List<TagPair>> eloGreaterThen(final int elo) {
        return whiteEloGreaterThen(elo).or(blackEloGreaterThen(elo));
    }

    public static Predicate<List<TagPair>> eloLessThen(final int elo) {
        return whiteEloLessThen(elo).or(blackEloLessThen(elo));
    }

    public static Predicate<List<TagPair>> yearEquals(final int year) {
        return YearFilter.yearEquals(year);
    }

    public static Predicate<List<TagPair>> yearGreaterThen(final int year) {
        return YearFilter.yearGreaterThen(year);
    }

    public static Predicate<List<TagPair>> yearGreaterThenOrEquals(final int year) {
        return yearGreaterThen(year).or(yearEquals(year));
    }

    public static Predicate<List<TagPair>> yearLessThen(final int year) {
        return YearFilter.yearLessThen(year);
    }

    public static Predicate<List<TagPair>> yearLessThenOrEquals(final int year) {
        return yearLessThen(year).or(yearEquals(year));
    }

    public static Predicate<List<TagPair>> yearBetween(final int start, final int end) {
        return yearGreaterThenOrEquals(start).and(yearLessThenOrEquals(end));
    }

    public static Predicate<List<Movetext>> opening(final List<Movetext> moves) {
        Objects.requireNonNull(moves);
        return new StartsWithMovesFilter(moves);
    }

    public static Predicate<List<Movetext>> opening(final String... moves) {
        return new StartsWithMovesFilter(MovetextFactory.moves(moves));
    }

    public static Predicate<List<Movetext>> containsMoves(final List<Movetext> moves) {
        Objects.requireNonNull(moves);
        return movetexts -> movetexts.containsAll(moves);
    }

    public static Predicate<List<Movetext>> whiteMates() {
        return moves -> Optional.ofNullable(moves.get(moves.size() - 1).white()).filter(Move::isMate).isPresent();
    }

    public static Predicate<List<Movetext>> blackMates() {
        return moves -> Optional.ofNullable(moves.get(moves.size() - 1).black()).filter(Move::isMate).isPresent();
    }

    public static Predicate<List<Movetext>> moveCountEquals(final int moveCount) {
        return moves -> moves.size() == moveCount;
    }

    public static Predicate<List<Movetext>> moveCountGreaterThen(final int moveCount) {
        return moves -> moves.size() > moveCount;
    }

    public static Predicate<List<Movetext>> moveCountGreaterThenOrEquals(final int moveCount) {
        return moves -> moves.size() >= moveCount;
    }

    public static Predicate<List<Movetext>> moveCountLessThen(final int moveCount) {
        return moves -> moves.size() < moveCount;
    }

    public static Predicate<List<Movetext>> moveCountLessThenOrEquals(final int moveCount) {
        return moves -> moves.size() <= moveCount;
    }

    public static Predicate<List<Movetext>> moveCountBetween(final int start, final int end) {
        return moveCountGreaterThenOrEquals(start).and(moveCountLessThenOrEquals(end));
    }

    public static Predicate<Game> decorateMoves(final Predicate<List<Movetext>> predicate) {
        return game -> predicate.test(game.moves());
    }

    public static Predicate<Game> decorateTagPair(final Predicate<List<TagPair>> predicate) {
        return game -> predicate.test(game.tagPairSection());
    }
}
