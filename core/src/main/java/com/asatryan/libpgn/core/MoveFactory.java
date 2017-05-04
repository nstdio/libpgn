package com.asatryan.libpgn.core;

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

    public static Move of(String move, short[] nag) {
        return new Move(move, null, nag, null);
    }
}
