package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GameFilter {
    private final List<Predicate<List<TagPair>>> tagPairFilter;
    private final List<Predicate<List<Movetext>>> movetextFilter;

    private final boolean negate;

    public GameFilter(List<Predicate<List<TagPair>>> filters, List<Predicate<List<Movetext>>> movetextFilter, boolean negate) {
        tagPairFilter = filters;
        this.movetextFilter = movetextFilter;
        this.negate = negate;
    }

    public GameFilter() {
        this(null, null, false);
    }

    public static GameFilterBuilder builder() {
        return GameFilterBuilder.builder();
    }

    public boolean test(final @Nonnull List<TagPair> tagPairs) {
        return testImpl(tagPairFilter, tagPairs);
    }

    public boolean testMovetext(final @Nonnull List<Movetext> movetextList) {
        return testImpl(movetextFilter, movetextList);
    }

    private <T> boolean testImpl(final List<Predicate<T>> filters, final T input) {
        if (filters == null) {
            return true;
        }

        for (Predicate<T> predicate : filters) {
            if (!predicate.test(input)) {
                return false;
            }
        }

        return true;
    }

    public static final class GameFilterBuilder {
        private List<Predicate<List<TagPair>>> tagPairFilter;
        private List<Predicate<List<Movetext>>> movetextFilter;
        private boolean negate;

        private GameFilterBuilder() {
        }

        public static GameFilterBuilder builder() {
            return new GameFilterBuilder();
        }

        public GameFilterBuilder tagPairFilter(final @Nonnull Predicate<List<TagPair>> filter) {
            tagPairs().add(filter);

            return this;
        }

        public GameFilterBuilder movetextFilter(final @Nonnull Predicate<List<Movetext>> filter) {
            movetext().add(filter);

            return this;
        }

        public GameFilterBuilder negate(final boolean negate) {
            this.negate = negate;

            return this;
        }

        public GameFilter build() {
            return new GameFilter(tagPairFilter, movetextFilter, negate);
        }

        private List<Predicate<List<TagPair>>> tagPairs() {
            tagPairFilter = lazyCreateContainer(tagPairFilter);

            return tagPairFilter;
        }

        private List<Predicate<List<Movetext>>> movetext() {
            movetextFilter = lazyCreateContainer(movetextFilter);

            return movetextFilter;
        }

        private <T> List<Predicate<T>> lazyCreateContainer(List<Predicate<T>> container) {
            if (container == null) {
                return new ArrayList<>();
            }

            return container;
        }
    }
}
