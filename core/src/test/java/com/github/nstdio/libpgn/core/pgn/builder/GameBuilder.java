package com.github.nstdio.libpgn.core.pgn.builder;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import com.github.nstdio.libpgn.core.pgn.TagPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class GameBuilder {
    private final List<TagPair> tagPairs = new ArrayList<>();
    private final List<Move> moves = new ArrayList<>();
    private Game.Result result;

    private GameBuilder() {
    }

    public static GameBuilder gameBuilder() {
        return new GameBuilder();
    }

    public GameBuilder tagPair(final String tag, final String value) {
        tagPairs.add(TagPair.of(tag.getBytes(), value.getBytes()));

        return this;
    }

    public GameBuilder move(final Move move) {
        moves.add(move);

        return this;
    }

    public GameBuilder move(final String move) {
        return move(Move.of(move));
    }

    public GameBuilder move(final String move, final String comment) {
        moves.add(Move.of(move, comment));
        return this;
    }

    public GameBuilder move(final String move, final String... variations) {
        moves.add(Move.of(move.getBytes(), null, null, MoveText.moves(variations)));

        return this;
    }

    public GameBuilder result(final Game.Result result) {
        this.result = result;
        return this;
    }

    public Game build() {
        final List<MoveText> collect = IntStream.range(0, moves.size())
                .filter(value -> value % 2 == 0)
                .mapToObj(idx -> {
                    final int moveNo = (idx / 2) + 1;

                    return idx + 1 < moves.size() ?
                            MoveText.of(moveNo, moves.get(idx), moves.get(idx + 1)) :
                            MoveText.ofWhite(moveNo, moves.get(idx));
                })
                .collect(toList());

        return new Game(tagPairs, collect, result);
    }
}
