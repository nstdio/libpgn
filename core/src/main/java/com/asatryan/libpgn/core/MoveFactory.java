package com.asatryan.libpgn.core;

import java.util.Collections;
import java.util.List;

public class MoveFactory {

    public static Move of(String move) {
        return of(move, (String) null);
    }

    public static Move of(String move, String comment) {
        return new Move(move, comment, null, null);
    }

    public static Move of(String move, List<Movetext> variations) {
        return new Move(move, null, null, variations);
    }

    public static Move of(String move, String comment, List<Movetext> variation) {
        return of(move, comment, null, variation);
    }

    public static Move of(String move, String comment, short[] nag, List<Movetext> variation) {
        return new Move(move, comment, nag, variation);
    }


    /**
     * @param move      The move.
     * @param moveNo    Variation move number.
     * @param variation The variation single move.
     *
     * @return The move object with single white variation.
     */
    public static Move withWhiteVariation(String move, int moveNo, String variation) {
        return new Move(move, null, null,
                Collections.singletonList(Movetext.of(moveNo, variation)));
    }

    /**
     * @param move      The move.
     * @param moveNo    Variation move number.
     * @param variation The variation single move.
     *
     * @return The move object with single black variation.
     */
    public static Move withBlackVariation(String move, int moveNo, String variation) {
        return new Move(move, null, null,
                Collections.singletonList(Movetext.of(moveNo, null, variation)));
    }

    public static Move of(String move, short[] nag) {
        return new Move(move, null, nag, null);
    }
}
