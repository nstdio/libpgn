package com.github.nstdio.libpgn.core.pgn;

import java.util.List;
import java.util.Optional;

/**
 * Represents the chess move.
 */
public interface MoveText {
    static MoveText ofWhite(int moveNo, String move) {
        return MoveTextFactory.create(moveNo, Move.of(move), null);
    }

    static MoveText ofWhite(int moveNo, Move move) {
        return MoveTextFactory.create(moveNo, move, null);
    }

    static MoveText ofBlack(int moveNo, String move) {
        return MoveTextFactory.create(moveNo, null, Move.of(move));
    }

    static MoveText ofBlack(int moveNo, Move move) {
        return MoveTextFactory.create(moveNo, null, move);
    }

    static MoveText of(int moveNo, Move white, Move black) {
        return MoveTextFactory.create(moveNo, white, black);
    }

    static MoveText of(int moveNo, String white, String black) {
        return MoveTextFactory.create(moveNo, Move.of(white), Move.of(black));
    }

    /**
     * Creates sequence of moves starting from 1 move number.
     *
     * @param moves Elements on even indices will be white moves and odd indices will be black moves.
     *
     * @return Sequence of moves.
     *
     * @see #moves(int, String...)
     */
    static List<MoveText> moves(String... moves) {
        return moves(1, moves);
    }

    /**
     * @param moveNoStart The start move number. At each iteration will be incremented. Providing negative number has no
     *                    effect, move number will be set to {@code 1}.
     * @param moves       The moves. Elements on even indices will be white moves and odd indices will be black moves.
     *
     * @return Sequence of moves.
     */
    static List<MoveText> moves(int moveNoStart, String... moves) {
        return MoveTextFactory.moves(moveNoStart, moves);
    }

    int moveNo();

    Optional<Move> white();

    Optional<Move> black();
}
