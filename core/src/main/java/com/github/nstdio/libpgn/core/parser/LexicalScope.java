package com.github.nstdio.libpgn.core.parser;

/**
 * Used by a lexer to find out in which part of the PGN text it is located.
 */
class LexicalScope {
    /**
     * Means that the lexer can not understand in what context he is.
     */
    static final byte SCOPE_UNDEFINED = -1;

    /**
     * Means that the lexer is currently analyzing tag pair section.
     */
    static final byte SCOPE_TAG_PAIR = 0;

    /**
     * Means that the lexer is currently analyzing move text section.
     */
    static final byte SCOPE_MOVE_TEXT = 1;

    /**
     * Means that the lexer is currently analyzing result section.
     */
    static final byte SCOPE_GAMETERM = 2;

    static String descOf(final byte scope) {
        switch (scope) {
            case SCOPE_UNDEFINED:
                return "SCOPE_UNDEFINED";
            case SCOPE_TAG_PAIR:
                return "SCOPE_TAG_PAIR";
            case SCOPE_MOVE_TEXT:
                return "SCOPE_MOVE_TEXT";
            case SCOPE_GAMETERM:
                return "SCOPE_GAMETERM";
            default:
                throw new IllegalArgumentException(String.format("Unknown scope: %d", scope));
        }
    }
}
