package com.github.nstdio.libpgn.core;

public class TokenTypes {
    public static final byte UNDEFINED = 0;
    public static final byte TP_BEGIN = 1;
    public static final byte TP_NAME = 2;
    public static final byte TP_VALUE_BEGIN = 3;
    public static final byte TP_VALUE = 4;
    public static final byte TP_VALUE_END = 5;
    public static final byte TP_END = 6;
    public static final byte MOVE_NUMBER = 7;
    public static final byte SKIP_PREV_MOVE = 8;
    public static final byte MOVE_WHITE = 9;
    public static final byte MOVE_BLACK = 10;
    public static final byte COMMENT_BEGIN = 11;
    public static final byte COMMENT = 12;
    public static final byte COMMENT_END = 13;
    public static final byte VARIATION_BEGIN = 14;
    public static final byte VARIATION_END = 15;
    public static final byte NAG = 16;
    public static final byte GAMETERM = 17;
    public static final byte DOT = 18;
    public static final byte TP_NAME_VALUE_SEP = 19;
    public static final byte ROL_COMMENT = 20;

    private static final String[] LITERALS = {
            "UNDEFINED",
            "TP_BEGIN",
            "TP_NAME",
            "TP_VALUE_BEGIN",
            "TP_VALUE",
            "TP_VALUE_END",
            "TP_END",
            "MOVE_NUMBER",
            "SKIP_PREV_MOVE",
            "MOVE_WHITE",
            "MOVE_BLACK",
            "COMMENT_BEGIN",
            "COMMENT",
            "COMMENT_END",
            "VARIATION_BEGIN",
            "VARIATION_END",
            "NAG",
            "GAMETERM",
            "DOT",
            "TP_NAME_VALUE_SEP",
            "ROL_COMMENT",
    };

    public static String descOf(byte token) {
        return LITERALS[token];
    }
}
