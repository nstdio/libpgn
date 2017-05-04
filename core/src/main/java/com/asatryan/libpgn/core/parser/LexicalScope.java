package com.asatryan.libpgn.core.parser;

class LexicalScope {
    static final byte UNDEFINED = -1;
    static final byte TAG_PAIR = 0;
    static final byte MOVE_TEXT = 1;
    static final byte GAMETERM = 2;
}
