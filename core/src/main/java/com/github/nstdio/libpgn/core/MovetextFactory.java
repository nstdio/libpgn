package com.github.nstdio.libpgn.core;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MovetextFactory {

    /**
     * Creates sequence of moves starting from 1 move number.
     *
     * @param moves Elements on even indices will be white moves and odd indices will be black moves.
     *
     * @return Sequence of moves.
     * @see #moves(int, String...)
     */
    public static List<Movetext> moves(String... moves) {
        return moves(1, moves);
    }

    /**
     * @param moveNoStart The start move number. At each iteration will be incremented. Providing negative number has no
     *                    effect, move number will be set to {@code 1}.
     * @param moves       The moves. Elements on even indices will be white moves and odd indices will be black moves.
     *
     * @return Sequence of moves.
     */
    public static List<Movetext> moves(int moveNoStart, String... moves) {
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
                return Collections.singletonList(white(moveNoStart, moves[0]));
            case 2:
                return Collections.singletonList(of(moveNoStart, moves[0], moves[1]));
            case 3:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), white(++moveNoStart, moves[2]));
            case 4:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]));
            case 5:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]), white(++moveNoStart, moves[4]));
        }


        Movetext[] ret = new Movetext[size];

        for (int i = 0, n = ret.length; i < n; i++) {
            final int k = i * 2;
            if (i + 1 > n || k + 1 >= movesLength) {
                ret[i] = white(moveNoStart++, moves[k]);
            } else {
                ret[i] = of(moveNoStart++, moves[k], moves[k + 1]);
            }
        }

        return Arrays.asList(ret);
    }

    /**
     * Creates instance with white and black moves only.
     * <p>
     * <pre>
     * {@code
     * Movetext.of(1, "d4", "f5"); // "1. d4 f5"
     * }
     * </pre>
     *
     * @param moveNo The move number.
     * @param white  The white move.
     * @param black  The black move.
     *
     * @return Movetext created from provided moves.
     * @throws IllegalArgumentException When {@code white == null && black == null}.
     */
    public static Movetext of(int moveNo, String white, String black) {
        if (white == null && black == null) {
            throw new IllegalArgumentException("white == null && black == null");
        }
        return new Movetext(moveNo,
                white == null ? null : MoveFactory.of(white),
                black == null ? null : MoveFactory.of(black)
        );
    }

    /**
     * Creates instance with move number and black move.
     * <p>Examples:
     * <blockquote><pre>
     * Movetext.black(2, "d5"); // "2... d5"
     * Movetext.black(13, "Qc2"); // "13... Qc2"
     * </pre></blockquote>
     *
     * @param moveNo    The move number.
     * @param blackMove The {@code String} representation of Black's move.
     *
     * @return Movetext created from provided move number and black move.
     */
    public static Movetext black(int moveNo, @Nonnull String blackMove) {
        return of(moveNo, null, blackMove);
    }

    /**
     * Creates instance with move number and white move.
     * <p>Examples:
     * <blockquote><pre>
     * Movetext.white(1, "e4"); // "1. e4"
     * Movetext.white(3, "exd5"); // "3. exd5"
     * </pre></blockquote>
     *
     * @param moveNo    The move number.
     * @param whiteMove The {@code String} representation of White's move.
     *
     * @return Movetext created from provided move number and white move.
     */
    public static Movetext white(int moveNo, @Nonnull String whiteMove) {
        return of(moveNo, whiteMove, null);
    }

    public static Movetext white(int moveNo, @Nonnull Move whiteMove) {
        return of(moveNo, whiteMove, null);
    }

    public static Movetext of(String moveNumber, Move white, Move black) {
        return of(Integer.parseInt(moveNumber), white, black);
    }

    /**
     * @param moveNo The move number.
     * @param white  The {@code Move} object for white.
     * @param black  The {@code Move} object for black.
     *
     * @return {@code Movetext} created from provided white and black {@code Move}s.
     */
    public static Movetext of(int moveNo, Move white, Move black) {
        return new Movetext(moveNo, white, black);
    }
}
