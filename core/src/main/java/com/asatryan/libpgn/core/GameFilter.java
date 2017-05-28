package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.filter.Filter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GameFilter {
    private final List<Filter<List<TagPair>>> tagPairFilter;
    private final boolean negate;

    public GameFilter(List<Filter<List<TagPair>>> filters, boolean negate) {
        tagPairFilter = filters;
        this.negate = negate;
    }

    public GameFilter() {
        this(null, false);
    }

    public static GameFilterBuilder builder() {
        return GameFilterBuilder.builder();
    }

    public boolean test(final @Nonnull List<TagPair> tagPairs) {
        if (tagPairFilter == null) {
            return true;
        }

        for (Filter<List<TagPair>> filter : tagPairFilter) {
            if (!filter.test(tagPairs)) {
                return false;
            }
        }

        return true;
    }

    public static final class GameFilterBuilder {
        private List<Filter<List<TagPair>>> tagPairFilter;
        private boolean negate;

        private GameFilterBuilder() {
        }

        public static GameFilterBuilder builder() {
            return new GameFilterBuilder();
        }

        public GameFilterBuilder tagPairFilter(final @Nonnull Filter<List<TagPair>> filter) {
            tagPairs().add(filter);

            return this;
        }

        public GameFilterBuilder negate(final boolean negate) {
            this.negate = negate;

            return this;
        }

        public GameFilter build() {
            return new GameFilter(tagPairFilter, negate);
        }

        private List<Filter<List<TagPair>>> tagPairs() {
            if (tagPairFilter == null) {
                tagPairFilter = new ArrayList<>();
            }

            return tagPairFilter;
        }
    }
}
