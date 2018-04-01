package com.github.nstdio.libpgn.collector;

import java.util.Objects;

public final class OpeningStatistics {
    private final String eco;
    private final ResultStatistic statistic;

    public OpeningStatistics(final String eco, final ResultStatistic statistic) {
        this.eco = eco;
        this.statistic = Objects.requireNonNull(statistic);
    }

    public OpeningStatistics merge(final OpeningStatistics other) {
        Objects.requireNonNull(other);

        if (!eco.equals(other.eco)) {
            throw new IllegalArgumentException(String.format("Cannot merge OpeningStatistics with different ECO's: %s, %s", eco, other.eco));
        }

        return new OpeningStatistics(
                eco,
                statistic.merge(other.statistic)
        );
    }

    public String eco() {
        return eco;
    }

    public ResultStatistic statistic() {
        return statistic;
    }

    public int count() {
        return statistic.games();
    }

    @Override

    public String toString() {
        return "OpeningStatistics{" +
                "count=" + statistic.games() +
                ", eco='" + eco + '\'' +
                ", statistic=" + statistic +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof OpeningStatistics)) return false;

        final OpeningStatistics that = (OpeningStatistics) o;

        return eco.equals(that.eco) && statistic.equals(that.statistic);
    }

    @Override
    public int hashCode() {
        int result = 31 + eco.hashCode();
        result = 31 * result + statistic.hashCode();
        return result;
    }
}
