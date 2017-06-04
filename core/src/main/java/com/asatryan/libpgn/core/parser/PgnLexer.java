package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.TokenTypes;
import com.asatryan.libpgn.core.internal.ByteUtils;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static com.asatryan.libpgn.core.internal.EmptyArrays.EMPTY_BYTE_ARRAY;

/**
 * Converts input data to specific tokens declared in {@link TokenTypes}.
 */
@SuppressWarnings("WeakerAccess")
public class PgnLexer {
    private int line = 1;
    private byte[] data;
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
    public PgnLexer(@Nonnull final byte[] data) {
        initInternal(data);
    }

    /**
     * Constructs a new {@code PgnLexer} with copy of {@code data}.
     *
     * @param data The input data that need to be tokenized.
     * @param copy This parameter is needed to indicate whether copy {@code data} or not.
     */
    public PgnLexer(@Nonnull final byte[] data, boolean copy) {
        init(data, copy);
    }

    /**
     * Constructs a new {@code PgnLexer} with empty input data.
     */
    public PgnLexer() {
        this(EMPTY_BYTE_ARRAY);
    }

    /**
     * Single difference from {@link #init(byte[])} is that this method will create a copy of array.
     *
     * @param data The input data that need to be tokenized.
     * @param copy The parameter that indicates that input array will be copied. This parameter expected always true.
     */
    public void init(@Nonnull final byte[] data, @SuppressWarnings("unused") boolean copy) {
        initInternal(Arrays.copyOf(data, data.length));
    }

    /**
     * NO COPIES of the input array are made. Any changes will affect internal state of object. If the object was
     * initialized with {@link #PgnLexer(byte[])} no need to call this method before calling {@link #nextToken()}. This
     * method exists for reinitializing an instance. If you construct object with default constructor first you must
     * call this method before any other.
     *
     * @param data The input data that need to be tokenized.
     */
    public void init(@Nonnull final byte[] data) {
        initInternal(data);
    }

    /**
     * <p>
     * Tries to find the next token. Returned {@code
     * byte} value will be one of {@link TokenTypes} constants. If lexer cannot find any valid token it'll return {@link
     * TokenTypes#UNDEFINED}.
     * Preconditions: The caller must call {@link #init(byte[])} or create this object with {@link #PgnLexer(byte[])} or
     * {@link #PgnLexer(byte[], boolean)} to initialize the array.
     *
     * @return The next occurred token.
     */
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

    /**
     * Extracts the string.
     * <p>
     * Preconditions: The caller must call {@link #init(byte[])} first to initialize the array.
     * This method creates new {@code String} object each time.
     *
     * @return The slice of input {@code data} from {@link #position()} to {@link #tokenLength()} + {@link #position()}
     * @see #positionAlign()
     */
    public String extract() {
        return new String(data, dataPosition - tokenLength, tokenLength);
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
     * Preconditions: The caller must call {@link #init(byte[])} first to initialize the array.
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

    /**
     * Records all tokens between {@link #lastToken()} until {@link TokenTypes#UNDEFINED} first occurrence.
     *
     * @return The tokens {@code Queue} between last token and {@link TokenTypes#UNDEFINED}.
     * @see #stream(byte)
     */
    public Queue<Byte> stream() {
        return stream(UNDEFINED);
    }

    /**
     * Records all tokens between {@link #lastToken()} and {@code terminationToken}. This implementation will not
     * include {@code terminationToken} as last element in returned {@code Queue}.
     *
     * @param terminationToken When lexer occurs this token it'll stops.
     *
     * @return The tokens {@code Queue} between current token and {@code terminationToken}
     */
    public Queue<Byte> stream(final byte terminationToken) {
        final ArrayDeque<Byte> stream = new ArrayDeque<>();

        do {
            stream.add(nextToken());
        } while (lastToken != terminationToken && lastToken != UNDEFINED);

        if (stream.getLast() == terminationToken) {
            stream.removeLast();
        }

        return stream;
    }

    /**
     * Polls all tokens until first occurrence of {@code terminationToken} or {@link TokenTypes#UNDEFINED}. In result of
     * method invocation {@code lastToken() == terminationToken} condition will be {@code true}.
     *
     * @param terminationToken When lexer occurs this token or {@link TokenTypes#UNDEFINED} it'll stops.
     */
    public void poll(final byte terminationToken) {
        while (lastToken != terminationToken && lastToken != UNDEFINED) {
            nextToken();
        }
    }

    byte[] data() {
        return data;
    }

    private void initInternal(@Nonnull final byte[] data) {
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

        final byte current = data[dataPosition];

        if (current == '[') {
            scope = LexicalScope.TAG_PAIR;
        } else if (Character.isLetterOrDigit(current)) {
            scope = LexicalScope.MOVE_TEXT;
        } else {
            scope = LexicalScope.UNDEFINED;
        }
    }

    @Override
    public String toString() {
        return "PgnLexer{" +
                "lastToken=" + TokenTypes.descOf(lastToken) +
                '}';
    }

    private void moveText() {
        final byte current = data[dataPosition];

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
            positionAlign();
        } else if (lastToken == COMMENT_BEGIN && ByteUtils.isDefined(current)) {
            lastToken = COMMENT;
            final int commentEnd = ByteUtils.unescapedChar(data, dataPosition + 1, '}');
            tokenLength = commentEnd - dataPosition;
            positionAlign();
        } else if (ByteUtils.isDigit(current)) {
            lastToken = MOVE_NUMBER;
            final int whiteSpace = ByteUtils.whitespaceOrChar(data, dataPosition, ' ', '.') + 1;
            final int i = whiteSpace - dataPosition;
            tokenLength = i == 1 ? 1 : i - 1;
            positionAlign();
        } else if (current == '.') {
            if (data[dataPosition + 1] == '.' && data[dataPosition + 2] == '.') {
                lastToken = SKIP_PREV_MOVE;
                tokenLength = 3;
                positionAlign();
            } else {
                lastToken = DOT;
                dataPosition++;
                tokenLength = 1;
            }
        } else if (current == ' ') {
            skipWhiteSpace();
            nextToken();
        } else if (ByteUtils.isLetter(current)) {
            if (lastToken == DOT || lastToken == UNDEFINED || lastToken == MOVE_BLACK) {
                lastToken = MOVE_WHITE;
                tokenLength = ByteUtils.moveEnd(data, dataPosition) - dataPosition;
                positionAlign();
            } else if (lastToken == MOVE_WHITE || lastToken == COMMENT_END || lastToken == VARIATION_END
                    || lastToken == NAG || lastToken == SKIP_PREV_MOVE || lastToken == ROL_COMMENT) {
                lastToken = MOVE_BLACK;
                tokenLength = ByteUtils.moveEnd(data, dataPosition) - dataPosition;
                positionAlign();
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
            final int commentEnd = ByteUtils.newLine(data, dataPosition);
            tokenLength = commentEnd - dataPosition;
            positionAlign();
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
            final int endPos = ByteUtils.whitespaceOrChar(data, dataPosition + 1, '$', '{', '(', ')', '*');
            tokenLength = endPos - dataPosition;
            positionAlign();
        } else if (current == '\n' || current == '\r') {
            if (current == '\n') {
                line++;
            }
            dataPosition++;
            nextToken();
        }
    }

    private void tagPair() {
        final byte current = data[dataPosition];

        if (current == '[') {
            lastToken = TP_BEGIN;
            dataPosition++;
            tokenLength = 1;
        } else if (ByteUtils.isLetter(current)) {
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
                positionAlign();
                break;
            case TP_VALUE_BEGIN:
                lastToken = TP_VALUE;
                final int ch = untilChar('"');
                tokenLength = ch - dataPosition;
                positionAlign();
                break;
            default:
                lastToken = UNDEFINED;
                break;
        }
    }

    private int untilChar(char c) {
        int pos = dataPosition;
        final byte[] data = this.data;
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
        final byte[] data = this.data;
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
}
