package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.TokenTypes;
import com.asatryan.libpgn.core.internal.CharUtils;

import static com.asatryan.libpgn.core.TokenTypes.*;

// TODO: 03.05.2017 make this public.
class PgnLexer {
    private int line = 1;
    private char[] data;
    private int dataPosition;
    private int dataLength;
    private byte lastToken;
    private int tokenLength;
    private byte scope;

    void init(char[] data) {
        this.data = data;
        dataLength = data.length;
        dataPosition = 0;
        tokenLength = 0;
        lastToken = UNDEFINED;

        skipWhiteSpace();
        defineScope();
    }

    private void defineScope() {
        final char current = data[dataPosition];

        if (current == '[') {
            scope = LexicalScope.TAG_PAIR;
        } else if (Character.isLetterOrDigit(current)) {
            scope = LexicalScope.MOVE_TEXT;
        } else {
            scope = LexicalScope.UNDEFINED;
        }
    }

    byte nextToken() {
        try {
            switch (scope) {
                case LexicalScope.TAG_PAIR:
                    tagPair();
                    break;
                case LexicalScope.MOVE_TEXT:
                    moveText();
                    break;
                case LexicalScope.GAMETERM:
                    skipWhiteSpace();
                    defineScope();
                    nextToken();
                    break;
                case LexicalScope.UNDEFINED:
                    lastToken = UNDEFINED;
                    break;
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            lastToken = UNDEFINED;
            scope = UNDEFINED;
        }

        return lastToken;
    }

    String extract() {
        int startPos = dataPosition;
        positionAlign();

        return new String(data, startPos, tokenLength);
    }

    char[] data() {
        return data;
    }

    byte nextAlignedToken() {
        nextToken();
        positionAlign();

        return lastToken;
    }

    @Override
    public String toString() {
        return "PgnLexer{" +
                "lastToken=" + TokenTypes.descOf(lastToken) +
                '}';
    }

    private void moveText() {
        final char current = data[dataPosition];

        if (((current == '1' || current == '0') && (data[dataPosition + 1] == '-' || data[dataPosition + 1] == '/'))
                || current == '*') {
            lastToken = GAMETERM;
            scope = LexicalScope.GAMETERM;

            if (current == '*') {
                tokenLength = 1;
            } else if (data[dataPosition + 1] != '/') {
                tokenLength = 3;
            } else {
                tokenLength = 7;
            }

        } else if (lastToken == COMMENT_BEGIN && CharUtils.isDefined(current)) {
            lastToken = COMMENT;
            final int commentEnd = CharUtils.unescapedChar(data, dataPosition + 1, '}');
            tokenLength = commentEnd - dataPosition;
        } else if (CharUtils.isDigit(current)) {
            lastToken = MOVE_NUMBER;
            final int whiteSpace = moveNumberEndPos() + 1;
            final int i = whiteSpace - dataPosition;
            tokenLength = i == 1 ? 1 : i - 1;
        } else if (current == '.') {
            if (data[dataPosition + 1] == '.' && data[dataPosition + 2] == '.') {
                lastToken = SKIP_PREV_MOVE;
                dataPosition++;
                tokenLength = 2;
            } else {
                lastToken = DOT;
                dataPosition++;
                tokenLength = 1;
            }
        } else if (current == ' ') {
            skipWhiteSpace();
            nextToken();
        } else if (CharUtils.isLetter(current)) {
            if (lastToken == DOT || lastToken == UNDEFINED || lastToken == MOVE_BLACK) {
                lastToken = MOVE_WHITE;
                tokenLengthForMove();
            } else if (lastToken == MOVE_WHITE || lastToken == COMMENT_END || lastToken == VARIATION_END
                    || lastToken == NAG || lastToken == SKIP_PREV_MOVE || lastToken == ROL_COMMENT) {
                lastToken = MOVE_BLACK;
                tokenLengthForMove();
            }
        } else if (current == '{') {
            lastToken = COMMENT_BEGIN;
            dataPosition++;
            tokenLength = 1;
        } else if (current == '}') {
            lastToken = COMMENT_END;
            dataPosition++;
            tokenLength = 1;
        } else if (current == ';') {
            lastToken = ROL_COMMENT;
            dataPosition++;
            final int commentEnd = CharUtils.newLine(data, dataPosition);
            tokenLength = commentEnd - dataPosition;
        } else if (current == '(') {
            lastToken = VARIATION_BEGIN;
            dataPosition++;
            tokenLength = 1;
        } else if (current == ')') {
            lastToken = VARIATION_END;
            dataPosition++;
            tokenLength = 1;
        } else if (current == '$') {
            lastToken = NAG;
            final int endPos = CharUtils.whitespaceOrChar(data, dataPosition + 1, '$', '{', '(', ')', '*');
            tokenLength = endPos - dataPosition;
        } else if (current == '\n' || current == '\r') {
            if (current == '\n') {
                line++;
            }
            dataPosition++;
            nextToken();
        }
    }

    private void tokenLengthForMove() {
        final int moveEnd = moveEndPos();
        tokenLength = moveEnd - dataPosition;
    }

    private int moveNumberEndPos() {
        return CharUtils.whitespaceOrChar(data, dataPosition, ' ', '.');
    }

    private int moveEndPos() {
        return CharUtils.moveEnd(data, dataPosition);
    }

    private void tagPair() {
        final char current = data[dataPosition];

        if (current == '[') {
            lastToken = TP_BEGIN;
            dataPosition++;
            tokenLength = 1;
        } else if (CharUtils.isLetter(current)) {
            lastTokenIfCurrentLetter();
        } else if (current == '"') {
            lastTokenIfCurrentQuote();
        } else if (current == ']') {
            lastToken = TP_END;
            dataPosition++;
            tokenLength = 1;
        } else if (current == ' ' && lastToken == TP_NAME) {
            lastToken = TP_NAME_VALUE_SEP;
            dataPosition++;
            tokenLength = 1;
        } else if (Character.isDefined(current) && lastToken == TP_VALUE_BEGIN) {
            lastTokenIfCurrentLetter();
        } else if (current == '\r' || current == '\n') {
            dataPosition++;
            if (current == '\n') {
                line++;
            }
            nextToken();
        } else {
            skipWhiteSpace();
            defineScope();
            nextToken();
        }

    }

    private void lastTokenIfCurrentQuote() {
        switch (lastToken) {
            case TP_VALUE:
                lastToken = TP_VALUE_END;
                dataPosition++;
                tokenLength = 1;
                break;
            case TP_NAME:
                lastToken = TP_VALUE_BEGIN;
                dataPosition++;
                tokenLength = 1;
                break;
            case TP_NAME_VALUE_SEP:
                lastToken = TP_VALUE_BEGIN;
                dataPosition++;
                tokenLength = 1;
                break;
            case TP_VALUE_BEGIN:
                lastToken = TP_VALUE_END;
                dataPosition++;
                tokenLength = 1;
                break;
            default:
                lastToken = UNDEFINED;
                break;
        }
    }

    private void lastTokenIfCurrentLetter() {
        switch (lastToken) {
            case TP_BEGIN:
                lastToken = TP_NAME;
                final int whiteSpace = untilChar(' ');
                tokenLength = whiteSpace - dataPosition;
                break;
            case TP_VALUE_BEGIN:
                lastToken = TP_VALUE;
                final int ch = untilChar('"');
                tokenLength = ch - dataPosition;
                break;
            default:
                lastToken = UNDEFINED;
                break;
        }
    }

    private int untilChar(char c) {
        int pos = dataPosition;
        final char[] data = this.data;
        final int dataLength = this.dataLength;

        for (pos++; pos < dataLength; pos++) {
            if (data[pos] == c) {
                return pos;
            }
        }

        return pos;
    }

    private void skipWhiteSpace() {
        boolean isWhiteSpace = true;
        final char[] data = this.data;

        while (isWhiteSpace) {
            switch (data[dataPosition]) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    dataPosition++;
                    break;
                default:
                    isWhiteSpace = false;
            }
        }
    }

    byte lastToken() {
        return lastToken;
    }

    int position() {
        return dataPosition;
    }

    void positionOffset(int offset) {
        dataPosition += offset;
    }

    void positionAlign() {
        positionOffset(tokenLength);
    }

    int tokenLength() {
        return tokenLength;
    }

    int line() {
        return line;
    }
}
