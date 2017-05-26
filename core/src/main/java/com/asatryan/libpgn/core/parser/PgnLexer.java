package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.TokenTypes;
import com.asatryan.libpgn.core.internal.CharUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.asatryan.libpgn.core.TokenTypes.*;

/**
 * Converts input data to specific tokens declared in {@link TokenTypes}.
 */
@SuppressWarnings("WeakerAccess")
public class PgnLexer {
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];
    private int line = 1;
    private char[] data;
    private int dataPosition;
    private byte lastToken;
    private int tokenLength;
    private byte scope = LexicalScope.UNDEFINED;

    /**
     * Constructs a new {@code PgnLexer} with {@code data}. No copies of {@code data} are made. Any changes on {@code
     * data} from outside will affect internal state of object.
     *
     * @param data The input data that need to be tokenized.
     */
    public PgnLexer(@Nonnull final char[] data) {
        initInternal(data);
    }

    /**
     * Constructs a new {@code PgnLexer} with copy of {@code data}.
     *
     * @param data The input data that need to be tokenized.
     * @param copy This parameter is needed to indicate whether copy {@code data} or not.
     */
    public PgnLexer(@Nonnull final char[] data, boolean copy) {
        init(data, copy);
    }

    /**
     * Constructs a new {@code PgnLexer} with empty input data.
     */
    public PgnLexer() {
        this(EMPTY_CHAR_ARRAY);
    }

    /**
     * Single difference from {@link #init(char[])} is that this method will create a copy of array.
     *
     * @param data The input data that need to be tokenized.
     * @param copy The parameter that indicates that input array will be copied. This parameter expected always true.
     */
    public void init(@Nonnull final char[] data, boolean copy) {
        initInternal(Arrays.copyOf(data, data.length));
    }

    /**
     * NO COPIES of the input array are made. Any changes will affect internal state of object. If the object was
     * initialized with {@link #PgnLexer(char[])} no need to call this method before calling {@link #nextToken()}. This
     * method exists for reinitializing an instance. If you construct object with default constructor first you must
     * call this method before any other.
     *
     * @param data The input data that need to be tokenized.
     */
    public void init(@Nonnull final char[] data) {
        initInternal(data);
    }

    private void initInternal(@Nonnull final char[] data) {
        this.data = data;
        dataPosition = 0;
        tokenLength = 0;
        line = 1;
        lastToken = UNDEFINED;
        scope = LexicalScope.UNDEFINED;

        if (data.length > 0) {
            skipWhiteSpace();
            determineScope();
        }
    }

    private void determineScope() {
        if (dataPosition >= data.length) {
            scope = LexicalScope.UNDEFINED;
            return;
        }

        final char current = data[dataPosition];

        if (current == '[') {
            scope = LexicalScope.TAG_PAIR;
        } else if (Character.isLetterOrDigit(current)) {
            scope = LexicalScope.MOVE_TEXT;
        } else {
            scope = LexicalScope.UNDEFINED;
        }
    }

    public byte nextToken() {
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
                    determineScope();
                    nextToken();
                    break;
                case LexicalScope.UNDEFINED:
                    lastToken = UNDEFINED;
                    break;
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            lastToken = UNDEFINED;
            scope = UNDEFINED;
            dataPosition = data.length - 1;
        }

        return lastToken;
    }

    char[] data() {
        return data;
    }

    @Override
    public String toString() {
        return "PgnLexer{" +
                "lastToken=" + TokenTypes.descOf(lastToken) +
                '}';
    }

    private void moveText() {
        final char current = data[dataPosition];

        if (((current == '1' || current == '0') && (dataPosition < data.length - 1 && (data[dataPosition + 1] == '-' || data[dataPosition + 1] == '/')))
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
            determineScope();
            nextToken();
        }

    }

    /**
     * Determine last token if current character is {@literal "}
     */
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
        final int dataLength = data.length;

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
        final int length = data.length;

        while (isWhiteSpace && dataPosition < length) {
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

    /**
     * Extracts the string.
     * <p>
     * Preconditions: The caller must call {@link #init(char[])} first to initialize the array.
     * This method creates new {@code String} object each time and aligns the position of lexer by
     * {@link #tokenLength()}.
     *
     * @return The slice of input {@code data} from {@link #position()} to {@link #tokenLength()} + {@link #position()}
     * @see #positionAlign()
     */
    public String extract() {
        int startPos = dataPosition;
        positionAlign();

        return new String(data, startPos, tokenLength);
    }

    /**
     * The last determined token.
     *
     * @return The last determined token.
     */
    public byte lastToken() {
        return lastToken;
    }

    /**
     * Preconditions: The caller must call {@link #init(char[])} first to initialize the array.
     *
     * @return The length of the input array.
     */
    public int length() {
        return data.length;
    }

    /**
     * @return The position of the lexer in the input data.
     */
    public int position() {
        return dataPosition;
    }

    /**
     * Changes the position by {@code offset} steps. If {@code offset} is negative, the position will shift back.
     * There are no checks to go beyond the array.
     *
     * @param offset How much to move the position.
     */
    public void positionOffset(int offset) {
        dataPosition += offset;
    }

    /**
     * Aligns the position with the length of the last token.
     */
    public void positionAlign() {
        positionOffset(tokenLength);
    }

    /**
     * @return The token length.
     */
    public int tokenLength() {
        return tokenLength;
    }

    /**
     * The line number is incremented only when {@literal \n} occurred.
     *
     * @return The current line.
     */
    public int line() {
        return line;
    }
}
