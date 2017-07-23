package com.github.nstdio.libpgn.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.github.nstdio.libpgn.core.internal.EmptyArrays.EMPTY_STRING_ARRAY;

@SuppressWarnings("WeakerAccess")
public class Game implements StringConvertible {
    private final List<TagPair> tagPairs;
    private final List<Movetext> moves;
    private final Result result;

    public Game(final List<TagPair> tagPairs, final List<Movetext> moves, final Result result) {
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

    public String whiteLastName() {
        return lastName(white()).orElse(null);
    }

    public String blackLastName() {
        return lastName(black()).orElse(null);
    }

    @Nonnull
    public String[] whiteMultiple() {
        return multiple(white());
    }

    public String black() {
        return tag("Black");
    }

    @Nonnull
    public String[] blackMultiple() {
        return multiple(black());
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
        return Optional.ofNullable(tagPairs)
                .flatMap(tp -> tp.stream()
                        .filter(tagPair -> tagPair.getTag().equals(name))
                        .map(TagPair::getValue)
                        .findFirst())
                .orElse(null);
    }

    @Nonnull
    private String[] multiple(@Nullable final String wb) {
        return Optional.ofNullable(wb).map(s -> s.split(":")).orElse(EMPTY_STRING_ARRAY);
    }

    private Optional<String> lastName(@Nullable final String fullName) {
        return Optional.ofNullable(fullName)
                .map(s -> s.split(",", 2))
                .filter(parts -> parts.length > 0)
                .map(parts -> parts[0]);
    }

    @Override
    public String toPgnString() {
        StringBuilder builder = new StringBuilder(512);

        Optional.ofNullable(tagPairs)
                .ifPresent(input -> {
                    input.forEach(tagPair -> builder.append(tagPair.toPgnString()).append('\n'));
                    builder.append('\n');
                });

        moves.forEach(movetext -> builder.append(movetext.toPgnString()).append(' '));

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
