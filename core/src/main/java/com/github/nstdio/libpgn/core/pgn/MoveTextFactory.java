package com.github.nstdio.libpgn.core.pgn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.nstdio.libpgn.core.pgn.MoveText.of;
import static com.github.nstdio.libpgn.core.pgn.MoveText.ofWhite;

final class MoveTextFactory {
    private MoveTextFactory() {
    }

    static MoveText create(int moveNo, Move white, Move black) {
        if (white == null && black == null) {
            throw new IllegalArgumentException("white and black cannot be null at the same time");
        }

        if (black == null) {
            return new WhiteMoveText(moveNo, white);
        }

        if (white == null) {
            return new BlackMoveText(moveNo, black);
        }

        return new DefaultMoveText(moveNo, white, black);
    }

    static List<MoveText> moves(int moveNoStart, String... moves) {
        if (moveNoStart <= 0) {
            moveNoStart = 1;
        }

        if (moves == null) {
            return Collections.emptyList();
        }

        final int movesLength = moves.length;
        final int size = Math.round((float) movesLength / 2);

        if (moveNoStart == Integer.MAX_VALUE) {
            moveNoStart = Integer.MAX_VALUE - size;
        }

        switch (movesLength) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(ofWhite(moveNoStart, moves[0]));
            case 2:
                return Collections.singletonList(of(moveNoStart, moves[0], moves[1]));
            case 3:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), ofWhite(++moveNoStart, moves[2]));
            case 4:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]));
            case 5:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]), ofWhite(++moveNoStart, moves[4]));
        }


        final MoveText[] ret = new MoveText[size];

        for (int i = 0, n = ret.length; i < n; i++) {
            final int k = i * 2;
            if (i + 1 > n || k + 1 >= movesLength) {
                ret[i] = ofWhite(moveNoStart++, moves[k]);
            } else {
                ret[i] = of(moveNoStart++, moves[k], moves[k + 1]);
            }
        }

        return Arrays.asList(ret);
    }
}
