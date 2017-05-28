package com.asatryan.libpgn.core.filter;

import com.asatryan.libpgn.core.TagPair;

import javax.annotation.Nonnull;
import java.util.List;

import static com.asatryan.libpgn.core.filter.TagPairFilter.Elo.*;
import static com.asatryan.libpgn.core.filter.TagPairFilter.LastNameEquals;
import static com.asatryan.libpgn.core.filter.TagPairFilter.PlayerElo.greaterThen;
import static com.asatryan.libpgn.core.filter.TagPairFilter.PlayerElo.lessThen;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Filters {

    /**
     * Filter object to filter by tag pair name and value.
     *
     * @param name  The tag pair name.
     * @param value The tag pair value.
     *
     * @return The filter object.
     */
    public static Filter<List<TagPair>> tagEquals(final @Nonnull String name, final @Nonnull String value) {
        return new TagPairFilter(name, value);
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "1-0".
     *
     * @return The filter object.
     */
    public static Filter<List<TagPair>> whiteWins() {
        return tagEquals("Result", "1-0");
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "0-1".
     *
     * @return The filter object.
     */
    public static Filter<List<TagPair>> blackWins() {
        return tagEquals("Result", "0-1");
    }

    /**
     * Filter object to filer by Result tag pair which is equal to "1/2-1/2".
     *
     * @return The filter object.
     */
    public static Filter<List<TagPair>> draw() {
        return tagEquals("Result", "1/2-1/2");
    }

    /**
     * Filters on the full name of the White player in case sensitive manner. Shorthand for {@link #tagEquals(String,
     * String)}
     * <p>Examples:
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
    public static Filter<List<TagPair>> whiteEquals(final @Nonnull String whiteName) {
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
    public static Filter<List<TagPair>> blackEquals(final @Nonnull String blackName) {
        return tagEquals("Black", blackName);
    }


    public static Filter<List<TagPair>> eventEquals(final @Nonnull String event) {
        return tagEquals("Event", event);
    }

    public static Filter<List<TagPair>> siteEquals(final @Nonnull String site) {
        return tagEquals("Site", site);
    }

    public static Filter<List<TagPair>> whiteLastNameEquals(final @Nonnull String whiteLastName) {
        return LastNameEquals.whiteLastNameEquals(whiteLastName);
    }

    public static Filter<List<TagPair>> blackLastNameEquals(final @Nonnull String blackLastName) {
        return LastNameEquals.blackLastNameEquals(blackLastName);
    }

    public static Filter<List<TagPair>> lastNameEquals(final @Nonnull String playerLastName) {
        return LastNameEquals.lastNameEquals(playerLastName);
    }

    public static Filter<List<TagPair>> whiteEloGreaterThen(final int elo) {
        return whiteGreaterThen(elo);
    }

    public static Filter<List<TagPair>> whiteEloLessThen(final int elo) {
        return whiteLessThen(elo);
    }

    public static Filter<List<TagPair>> blackEloGreaterThen(final int elo) {
        return blackGreaterThen(elo);
    }

    public static Filter<List<TagPair>> blackEloLessThen(final int elo) {
        return blackLessThen(elo);
    }

    public static Filter<List<TagPair>> eloGreaterThen(final int elo) {
        return greaterThen(elo);
    }

    public static Filter<List<TagPair>> eloLessThen(final int elo) {
        return lessThen(elo);
    }

    public static Filter<List<TagPair>> yearEquals(final int year) {
        return YearFilter.yearEquals(year);
    }

    public static Filter<List<TagPair>> yearGreaterThen(final int year) {
        return YearFilter.yearGreaterThen(year);
    }

    public static Filter<List<TagPair>> yearGreaterThenOrEquals(final int year) {
        return YearFilter.yearGreaterThenOrEquals(year);
    }

    public static Filter<List<TagPair>> yearLessThen(final int year) {
        return YearFilter.yearLessThen(year);
    }

    public static Filter<List<TagPair>> yearLessThenOrEquals(final int year) {
        return YearFilter.yearLessThenOrEquals(year);
    }

    public static Filter<List<TagPair>> yearBetween(final int start, final int end) {
        return YearFilter.yearBetween(start, end);
    }
}