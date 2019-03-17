package com.github.nstdio.libpgn.io;

import lombok.ToString;

@ToString
final class CursorPosition {
    private int line = 1;
    private int offset;

    int incrementLineAndGet() {
        offset = 0;
        return ++line;
    }

    void incrementLine() {
        ++line;
        offset = 0;
    }

    void addOffset(int delta) {
        offset += delta;
    }

    int line() {
        return line;
    }

    int offset() {
        return offset;
    }
}
