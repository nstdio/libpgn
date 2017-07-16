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
}
