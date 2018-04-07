package com.github.nstdio.libpgn.io;

import com.github.nstdio.libpgn.entity.*;
import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.github.nstdio.libpgn.entity.Result.DRAW;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PgnOutputStreamImplTest {

    static Stream<GameAndExpected> resultStream() {
        return Stream.of(
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, "e4")), DRAW),
                        "1. e4 1/2-1/2"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, "e4")), Result.WHITE),
                        "1. e4 1-0"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, "e4")), Result.BLACK),
                        "1. e4 0-1"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, "e4")), Result.UNKNOWN),
                        "1. e4 *"
                )
        );
    }

    static Stream<GameAndExpected> commentStream() {
        return Stream.of(
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, Move.of("e4", "comment"))), Result.UNKNOWN),
                        "1. e4 {comment} *"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, Move.of("e4", ""))), Result.UNKNOWN),
                        "1. e4 *"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, Move.of("e4".getBytes(), new byte[0]))), Result.UNKNOWN),
                        "1. e4 *"
                ),
                GameAndExpected.of(
                        new Game(null, singletonList(MoveText.ofWhite(1, Move.of("e4".getBytes(), new byte[1]))), Result.UNKNOWN),
                        "1. e4 {\0} *"
                )
        );
    }

    static Stream<GameAndExpected> variationStream() {
        return Stream.of(
                GameAndExpected.of(
                        new Game(null, Arrays.asList(
                                MoveText.ofWhite(1, Move.of("e4", MoveText.moves("e5")))
                        ), Result.UNKNOWN),
                        "1. e4 (1. e5) *"
                ),
                GameAndExpected.of(
                        new Game(null, Arrays.asList(
                                MoveText.of(1, Move.of("e4"), Move.of("c5", Arrays.asList(
                                        MoveText.ofBlack(1, "a6")
                                )))
                        ), Result.UNKNOWN),
                        "1. e4 c5 (1... a6) *"
                )
        );
    }

    @Test
    public void tagPair() throws IOException {
        final List<TagPair> tagPairs = new ArrayList<>();
        tagPairs.add(TagPair.of("Event", "a"));

        final MoveText of = MoveText.of(1, "e4", "e5");
        final Game game = new Game(tagPairs, singletonList(of), DRAW);

        assertGameEquals(game, "[Event \"a\"]\n1. e4 e5 1/2-1/2");
    }

    @ParameterizedTest
    @MethodSource("commentStream")
    public void comment(final GameAndExpected arg) throws IOException {
        assertGameEquals(arg.game, arg.expected);
    }

    @ParameterizedTest
    @MethodSource("resultStream")
    public void result(final GameAndExpected arg) throws IOException {
        assertGameEquals(arg.game, arg.expected);
    }

    @ParameterizedTest
    @MethodSource("variationStream")
    public void variation(final GameAndExpected arg) throws IOException {
        assertGameEquals(arg.game, arg.expected);
    }

    private void assertGameEquals(final Game game, final String expected) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);

        try (final PgnOutputStream outputStream = new PgnOutputStreamImpl(dos)) {
            outputStream.write(game);
            assertThat(byteArrayOutputStream.toString()).isEqualTo(expected);
        }
    }

    @Value(staticConstructor = "of")
    private static class GameAndExpected {
        @NonNull
        Game game;

        @NonNull
        String expected;
    }
}