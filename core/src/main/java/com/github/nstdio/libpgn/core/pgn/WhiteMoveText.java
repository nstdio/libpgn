package com.github.nstdio.libpgn.core.pgn;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Optional;

@Accessors(fluent = true)
@Value
class WhiteMoveText implements MoveText {
    private final int moveNo;
    private final Move move;

    @Override
    public Optional<Move> white() {
        return Optional.of(move);
    }

    @Override
    public Optional<Move> black() {
        return Optional.empty();
    }
}
