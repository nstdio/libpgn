package com.asatryan.libpgn.core;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Game implements StringConvertible {
    private final List<TagPair> tagPairs;
    private final List<Movetext> moves;
    private final Result result;

    public Game(List<TagPair> tagPairs, List<Movetext> moves, Result result) {
        this.tagPairs = tagPairs;
        this.moves = moves;
        this.result = result;
    }

    public List<Movetext> moves() {
        return moves;
    }

    public List<TagPair> tagPairSection() {
        return tagPairs;
    }

    public Result gameResult() {
        return result;
    }

    public String white() {
        return tag("White");
    }

    public String black() {
        return tag("Black");
    }

    public String event() {
        return tag("Event");
    }

    public String site() {
        return tag("Site");
    }

    public String date() {
        return tag("Date");
    }

    public String round() {
        return tag("Round");
    }

    public String result() {
        return tag("Result");
    }

    public String whiteElo() {
        return tag("WhiteElo");
    }

    public String blackElo() {
        return tag("BlackElo");
    }

    public String eco() {
        return tag("ECO");
    }

    public String tag(String name) {
        if (tagPairs == null) {
            return null;
        }

        for (TagPair tp : tagPairs) {
            if (tp.getTag().equals(name)) {
                return tp.getValue();
            }
        }

        return null;
    }

    @Override
    public String toPgnString() {
        StringBuilder builder = new StringBuilder(512);

        if (tagPairs != null) {
            for (TagPair tp : tagPairs) {
                builder.append(tp.toPgnString()).append('\n');
            }
            builder.append('\n');
        }

        for (Movetext tp : moves) {
            builder.append(tp.toPgnString()).append(' ');
        }

        builder.append('\n').append(result.term).append('\n');

        return builder.toString();
    }

    @Override
    public String toString() {
        return toPgnString();
    }

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
}
