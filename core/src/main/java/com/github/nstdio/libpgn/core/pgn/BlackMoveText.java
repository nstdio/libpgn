package com.github.nstdio.libpgn.core.pgn;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Optional;

@Accessors(fluent = true)
@Value
class BlackMoveText implements MoveText {
    private final int moveNo;
    private final Move move;

    @Override
    public int moveNo() {
        return moveNo;
    }

    @Override
    public Optional<Move> white() {
        return Optional.empty();
    }

    @Override
    public Optional<Move> black() {
        return Optional.of(move);
    }
}
