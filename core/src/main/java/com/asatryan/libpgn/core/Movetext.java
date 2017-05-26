package com.asatryan.libpgn.core;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Abstraction for representing the move number and White and Black players moves.
 */
@SuppressWarnings("WeakerAccess")
public class Movetext implements StringConvertible {
    private final int moveNo;
    @Nullable
    private final Move white;
    @Nullable
    private final Move black;

    /**
     * @param moveNo The <code>String</code> representation of move number.
     * @param white  The {@code Move} object representing White's move.
     * @param black  The {@code Move} object representing Black's move.
     *
     * @throws NumberFormatException When {@code moveNo} is not valid integer string.
     */
    public Movetext(String moveNo, @Nullable Move white, @Nullable Move black) {
        this(Integer.parseInt(moveNo), white, black);
    }

    /**
     * Should be considered as primary constructor.
     *
     * @param moveNo The move number.
     * @param white  The {@code Move} object representing White's move.
     * @param black  The {@code Move} object representing Blacks's move.
     *
     * @throws IllegalArgumentException If {@code white == null && black == null}.
     */
    public Movetext(int moveNo, @Nullable Move white, @Nullable Move black) {
        this.moveNo = moveNo;

        if (white == null && black == null) {
            throw new IllegalArgumentException("white == null && black == null");
        }
        this.white = white;
        this.black = black;
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
