package com.github.nstdio.libpgn.io;

import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;
import com.github.nstdio.libpgn.io.PgnOutputStream;
import com.github.nstdio.libpgn.io.PgnOutputStreamImpl;
import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    @Test
    public void tagPair() throws IOException {
        final List<TagPair> tagPairs = new ArrayList<>();
        tagPairs.add(TagPair.of("Event", "a"));

        final MoveText of = MoveText.of(1, "e4", "e5");
        final Game game = new Game(tagPairs, singletonList(of), DRAW);

        assertGameEquals(game, "[Event \"a\"]\n1. e4 e5 1/2-1/2");
    }

    @Test
    public void comment() {
    }

    @ParameterizedTest
    @MethodSource("resultStream")
    public void result(final GameAndExpected arg) throws IOException {
        /*
        final Game build = gameBuilder()
                .tagPair("Event", "FIDE Berlin Candidates 2018")
                .move("d4", new String[]{"e3"}).move("c6")
                .move("c4").move("d5", "D10 Slav Defence")
                .move("g3").move("Bf5")
                .result(DRAW)
                .build();
        assertGameEquals(arg.game, arg.expected);
        */
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