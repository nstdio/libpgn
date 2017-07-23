package com.github.nstdio.libpgn.core;

/**
 * Represents all possible tokens that PGN file can have.
 */
public class TokenTypes {
    /**
     * Indicates that cannot determine type of input.
     */
    public static final byte UNDEFINED = 0;

    /**
     * Tag pair begin. Represents '[' character.
     */
    public static final byte TP_BEGIN = 1;

    /**
     * Tag pair name. Represents "[Event "URS-World"]" ^--^
     */
    public static final byte TP_NAME = 2;

    /**
     * Tag pair value begin. Represents first '"' character after {@link #TP_NAME_VALUE_SEP}
     */
    public static final byte TP_VALUE_BEGIN = 3;

    /**
     * Tag pair value. Represents "[Event "URS-World"]".
     *                                     ^-------^
     */
    public static final byte TP_VALUE = 4;

    /**
     * Tag pair value end. Represents first '"' character after {@link #TP_VALUE}
     */
    public static final byte TP_VALUE_END = 5;

    /**
     * Tag pair end. Represents ']' character.
     */
    public static final byte TP_END = 6;

    /**
     * Move number. Represents move number indicator. Digit characters only. Occurred before {@link #MOVE_WHITE} or
     * {@link #SKIP_PREV_MOVE}
     */
    public static final byte MOVE_NUMBER = 7;

    /**
     * Skip previous move. Represents "..." character sequence.
     */
    public static final byte SKIP_PREV_MOVE = 8;

    /**
     * Move white. Represents "e4", "d5" or similar character sequences.
     */
    public static final byte MOVE_WHITE = 9;

    /**
     * Black move. Represents "e4", "d5" or similar character sequences.
     */
    public static final byte MOVE_BLACK = 10;

    /**
     * Comment begin. Represents '{' character. Indicates that next characters should be threat as comment.
     */
    public static final byte COMMENT_BEGIN = 11;

    /**
     * Comment. Represents characters sequence between {@link #COMMENT_BEGIN} and {@link #COMMENT_END}
     */
    public static final byte COMMENT = 12;

    /**
     * Comment end. Represents '}' character. Indicates that comment text is ended.
     */
    public static final byte COMMENT_END = 13;

    /**
     * Variation begin. Represents '(' character. Indicates that characters until first occurrence of {@link
     * #VARIATION_END} will be thread as variations to move. Can be nested as well.
     */
    public static final byte VARIATION_BEGIN = 14;

    /**
     * Variation end. Represents ')' character. Indicates that variation ended.
     */
    public static final byte VARIATION_END = 15;

    /**
     * Numeric Annotation Glyphs. Represents characters sequences link "$1$2", "$3".
     */
    public static final byte NAG = 16;

    /**
     * Game termination, i.e. result of the game. Represents one of following values:
     * <p>
     * <ul><li>1-0<li/><li>0-1<li/><li>1/2-1/2<li/><li>*<li/><ul/>
     */
    public static final byte GAMETERM = 17;

    /**
     * Dot. Represent's '.' character.
     */
    public static final byte DOT = 18;

    /**
     * Tag pair name and value separator. Represents ' '(whitespace) character.
     */
    public static final byte TP_NAME_VALUE_SEP = 19;

    /**
     * Rest of the line comment. Represents ';' character. Indicates that next characters until first occurrence of '\n'
     * will be threat as comment.
     */
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

    /**
     *
     * @param token One of {@link TokenTypes} constants.
     * @return The string description of token.
     */
    public static String descOf(byte token) {
        return LITERALS[token];
    }
}
