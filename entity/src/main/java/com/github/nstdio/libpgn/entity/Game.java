package com.github.nstdio.libpgn.entity;

import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.nstdio.libpgn.common.ArrayUtils.EMPTY_STRING_ARRAY;

@SuppressWarnings("WeakerAccess")
@ToString
public final class Game {
    private final List<TagPair> tagPairs;
    private final List<MoveText> moves;
    private final Result result;

    public Game(final List<TagPair> tagPairs, final List<MoveText> moves, final Result result) {
        this.tagPairs = tagPairs;
        this.moves = moves;
        this.result = result;
    }

    public List<MoveText> moves() {
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
        final byte[] nameBytes = name.getBytes();
        return Optional.ofNullable(tagPairs)
                .flatMap(tp -> tp.stream()
                        .filter(tagPair -> Arrays.equals(tagPair.getTag(), nameBytes))
                        .map(TagPair::getValue)
                        .map(String::new)
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

}
