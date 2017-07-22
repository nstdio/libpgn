package com.github.nstdio.libpgn.core;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class GameFilter {
    private final Predicate<List<TagPair>> tagPairFilter;
    private final Predicate<List<Movetext>> movetextFilter;

    GameFilter(final Predicate<List<TagPair>> filters, final Predicate<List<Movetext>> movetextFilter) {
        tagPairFilter = filters;
        this.movetextFilter = movetextFilter;
    }

    static GameFilterBuilder builder() {
        return new GameFilterBuilder();
    }

    public boolean test(final List<TagPair> tagPairs) {
        return testImpl(tagPairFilter, Objects.requireNonNull(tagPairs));
    }

    public boolean testMovetext(final List<Movetext> movetextList) {
        return testImpl(movetextFilter, Objects.requireNonNull(movetextList));
    }

    private <T> boolean testImpl(final Predicate<T> filters, final T input) {
        return filters == null || filters.test(input);

    }

    static final class GameFilterBuilder {
        private Predicate<List<TagPair>> tagPairFilter;
        private Predicate<List<Movetext>> movetextFilter;

        GameFilterBuilder() {
        }

        void tagPairFilter(final Predicate<List<TagPair>> filter) {
            Objects.requireNonNull(filter);
            if (tagPairFilter == null) {
                tagPairFilter = filter;
            } else {
                tagPairFilter = tagPairFilter.and(filter);
            }
        }

        void movetextFilter(final Predicate<List<Movetext>> movetextFilter) {
            Objects.requireNonNull(movetextFilter);
            if (this.movetextFilter == null) {
                this.movetextFilter = movetextFilter;
            } else {
                this.movetextFilter = this.movetextFilter.and(movetextFilter);
            }
        }

        GameFilter build() {
            return new GameFilter(tagPairFilter, movetextFilter);
        }
    }
}
