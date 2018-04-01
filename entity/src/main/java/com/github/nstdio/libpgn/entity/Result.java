package com.github.nstdio.libpgn.entity;

public enum Result {
    WHITE("1-0", "White wins"),
    BLACK("0-1", "Black wins"),
    DRAW("1/2-1/2", "Drawn game"),
    UNKNOWN("*", "Unknown");


    private final String term;
    private String reason;

    Result(final String result, final String reason) {
        term = result;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getTerm() {
        return term;
    }
}
