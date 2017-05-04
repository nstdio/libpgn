package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Movetext implements StringConvertible {
    private final int moveNo;
    @Nullable
    private final Move white;
    @Nullable
    private final Move black;

    public Movetext(String moveNo, @Nullable Move white, @Nullable Move black) {
        this(Integer.parseInt(moveNo), white, black);
    }

    public Movetext(int moveNo, @Nullable Move white, @Nullable Move black) {
        this.moveNo = moveNo;
        this.white = white;
        this.black = black;
    }

    /**
     * Creates instance with white and black moves only.
     * <p>
     * <pre>
     * {@code
     * Movetext move = Movetext.of(1, "d4", "f5");
     * move.toPgnString(); // "1. d4 f5"
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

    public static Movetext black(int moveNo, @Nonnull String move) {
        return of(moveNo, null, move);
    }

    public static Movetext white(int moveNo, @Nonnull String move) {
        return of(moveNo, move, null);
    }

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
                return Collections.singletonList(of(moveNoStart, moves[0]));
            case 2:
                return Collections.singletonList(of(moveNoStart, moves[0], moves[1]));
            case 3:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2]));
            case 4:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]));
            case 5:
                return Arrays.asList(of(moveNoStart, moves[0], moves[1]), of(++moveNoStart, moves[2], moves[3]), of(++moveNoStart, moves[4]));
        }


        Movetext[] ret = new Movetext[size];

        for (int i = 0, n = ret.length; i < n; i++) {
            final int k = i * 2;
            if (i + 1 > n || k + 1 >= movesLength) {
                ret[i] = of(moveNoStart++, moves[k]);
            } else {
                ret[i] = of(moveNoStart++, moves[k], moves[k + 1]);
            }
        }

        return Arrays.asList(ret);
    }

    /**
     * Creates instance with white move only.
     * <p>Examples:
     * <blockquote><pre>
     * Movetext.of(1, "d4"); // "1. d4"
     * </pre></blockquote>
     *
     * @param moveNo The move number.
     * @param white  The white move.
     *
     * @return Movetext created from provided white move moves.
     * @throws IllegalArgumentException When {@code white == null}
     * @see #of(int, Move)
     */
    public static Movetext of(int moveNo, @Nonnull String white) {
        return of(moveNo, MoveFactory.of(white));
    }

    public static Movetext of(int moveNo, @Nonnull Move white) {
        return of(moveNo, white, null);
    }

    public static Movetext of(String moveNo, String white, String black) {
        return of(Integer.valueOf(moveNo), white, black);
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
     * @throws IllegalArgumentException When {@code white == null && black == null}.
     */
    public static Movetext of(int moveNo, Move white, Move black) {
        if (white == null && black == null) {
            throw new IllegalArgumentException("white == null && black == null");
        }
        return new Movetext(moveNo, white, black);
    }

    static Movetext mergeWhiteAndBlack(Movetext white, Movetext black) {
        return of(white.moveNo, white.white, black.black);
    }

    public int moveNo() {
        return moveNo;
    }

    @Nullable
    public Move white() {
        return white;
    }

    @Nullable
    public Move black() {
        return black;
    }

    public String whiteMove() {
        return white != null ? white.move() : null;
    }

    public short[] whiteNag() {
        return white != null ? white.nag() : null;
    }

    public String whiteComment() {
        return white != null ? white.comment() : null;
    }

    public List<Movetext> whiteVariations() {
        return white != null ? white.variations() : null;
    }

    public String blackMove() {
        return black != null ? black.move() : null;
    }

    public short[] blackNag() {
        return black != null ? black.nag() : null;
    }

    public String blackComment() {
        return black != null ? black.comment() : null;
    }

    public List<Movetext> blackVariations() {
        return black != null ? black.variations() : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(moveNo);

        if (white == null || white.move() == null) {
            sb.append("... ");
        } else {
            sb.append(". ").append(white.toPgnString());
        }

        if (black != null && black.move() != null) {
            if (white != null && !white.variations().isEmpty()) {
                sb.append(moveNo).append("... ");
            }

            if (sb.charAt(sb.length() - 1) != ' ') {
                sb.append(' ');
            }
            sb.append(black.toPgnString()).append(' ');
        }

        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        Movetext movetext = (Movetext) obj;


        final boolean moveNoEq = moveNo == movetext.moveNo;
        if (!moveNoEq) {
            return false;
        }

        if (white == null && movetext.white == null && black == null && movetext.black == null) {
            return false;
        }

        final boolean whiteEq = white != null && movetext.white != null && white.equals(movetext.white);
        final boolean blackEq = (black != null && movetext.black != null && black.equals(movetext.black));

        return whiteEq && black == movetext.black
                || blackEq && white == movetext.white
                || whiteEq && blackEq;


    }

    @Override
    public String toPgnString() {
        return toString();
    }
}
