package com.asatryan.libpgn.core.exception;

public class PgnSyntaxException extends PgnException {

    private final int line;
    private final int pos;

    public PgnSyntaxException(String message, int line, int pos) {
        super(message);

        this.line = line;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        return String.format("SyntaxError(%d, %d): %s", line, pos, super.getMessage());
    }
}
