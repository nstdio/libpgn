package com.github.nstdio.libpgn.core.collector;

import com.github.nstdio.libpgn.core.internal.IntPair;

import java.util.Objects;

public class ResultStatistic {
    private final IntPair games;
    private final IntPair win;
    private final IntPair draw;

    public ResultStatistic(final IntPair games, final IntPair win, final IntPair draw) {
        this.games = Objects.requireNonNull(games);
        this.win = Objects.requireNonNull(win);
        this.draw = Objects.requireNonNull(draw);
    }

    private static String asWhiteAndBlack(final IntPair intPair) {
        return "{white=" + intPair.first + ", black=" + intPair.second + "}";
    }

    public int whiteGames() {
        return games.first;
    }

    public int blackGames() {
        return games.second;
    }

    public int games() {
        return games.sum();
    }

    public int wins() {
        return win.sum();
    }

    public int whiteWins() {
        return win.first;
    }

    public int blackWins() {
        return win.second;
    }

    public int whiteDraws() {
        return draw.first;
    }

    public int blackDraws() {
        return draw.second;
    }

    public int draws() {
        return draw.sum();
    }

    @Override
    public String toString() {
        return "ResultStatistic{" +
                "games=" + asWhiteAndBlack(games) +
                ", win=" + asWhiteAndBlack(win) +
                ", draw=" + asWhiteAndBlack(draw) +
                '}';
    }
}
