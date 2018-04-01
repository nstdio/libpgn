package com.github.nstdio.libpgn.entity;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Optional;

@Accessors(fluent = true)
@Value
class DefaultMoveText implements MoveText {
    private int moveNo;
    @NonNull
    private final Move white;
    @NonNull
    private final Move black;

    @Override
    public int moveNo() {
        return moveNo;
    }

    @Override
    public Optional<Move> white() {
        return Optional.of(white);
    }

    @Override
    public Optional<Move> black() {
        return Optional.of(black);
    }
}
