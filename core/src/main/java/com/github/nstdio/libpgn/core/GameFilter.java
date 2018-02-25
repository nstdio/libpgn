package com.github.nstdio.libpgn.core;

import com.github.nstdio.libpgn.core.pgn.MoveText;
import com.github.nstdio.libpgn.core.pgn.TagPair;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class GameFilter {
    private final Predicate<List<TagPair>> tagPairFilter;
    private final Predicate<List<MoveText>> movetextFilter;

    GameFilter(final Predicate<List<TagPair>> filters, final Predicate<List<MoveText>> movetextFilter) {
        tagPairFilter = filters;
        this.movetextFilter = movetextFilter;
    }

    static GameFilterBuilder builder() {
        return new GameFilterBuilder();
    }

    public boolean test(final List<TagPair> tagPairs) {
        return testImpl(tagPairFilter, Objects.requireNonNull(tagPairs));
    }

    public boolean testMoveText(final List<MoveText> moveTextList) {
        return testImpl(movetextFilter, Objects.requireNonNull(moveTextList));
    }

    private <T> boolean testImpl(final Predicate<T> filters, final T input) {
        return filters == null || filters.test(input);
    }

    static final class GameFilterBuilder {
        private Predicate<List<TagPair>> tagPairFilter;
        private Predicate<List<MoveText>> moveTextFilter;

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

        void movetextFilter(final Predicate<List<MoveText>> moveTextFilter) {
            Objects.requireNonNull(moveTextFilter);
            if (this.moveTextFilter == null) {
                this.moveTextFilter = moveTextFilter;
            } else {
                this.moveTextFilter = this.moveTextFilter.and(moveTextFilter);
            }
        }

        GameFilter build() {
            return new GameFilter(tagPairFilter, moveTextFilter);
        }
    }
}
