package com.github.nstdio.libpgn.core.exception;

public class PgnSyntaxException extends PgnException {
    public PgnSyntaxException(final String message, final int line, final int pos) {
        super(String.format("SyntaxError(%d, %d): %s", line, pos, message));
    }

    public PgnSyntaxException(final String message, final Throwable cause, final int line, final int pos) {
        super(String.format("SyntaxError(%d, %d): %s", line, pos, message), cause);
    }
}
