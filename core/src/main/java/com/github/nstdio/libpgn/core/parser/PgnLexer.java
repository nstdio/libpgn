package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.internal.ByteUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.internal.EmptyArrays.EMPTY_BYTE_ARRAY;
import static com.github.nstdio.libpgn.core.parser.LexicalScope.*;

/**
 * Converts input data to specific tokens declared in {@link TokenTypes}.
 * <p>
 * Note: In this implementation lexer expects input as {@code byte[]}. Since PNG files are not binary, this may seem
 * strange. There is some reasons for that. In a very general case, PGN data is read from files. Reading the contents of
 * a file into an array of bytes is somewhat bit more practical than in an array of characters. To avoid unnecessary
 * copies of data by the user, it was decided to use this type of data, although if the user does not want the lexer to
 * work directly with the provided data, he will always be able to use {@link #init(byte[], boolean)} that will copy the
 * input data.
 */
@SuppressWarnings("WeakerAccess")
public class PgnLexer {
    private int line = 1;
    private byte[] data;
    private int pointer;
    private byte lastToken;
    private int tokenLength;
    private byte scope;

    /**
     * Constructs a new {@code PgnLexer} with {@code data}. No copies of {@code data} are made. Any changes on {@code
     * data} from outside will affect internal state of object.
     *
     * @param data The input data that need to be tokenized.
     */
    public PgnLexer(final byte[] data) {
        initInternal(data);
    }

    /**
     * Constructs a new {@code PgnLexer} with copy of {@code data}.
     *
     * @param data The input data that need to be tokenized.
     * @param copy This parameter is needed to indicate whether copy {@code data} or not.
     */
    public PgnLexer(final byte[] data, boolean copy) {
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
    public void init(final byte[] data, @SuppressWarnings("unused") boolean copy) {
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
    public void init(final byte[] data) {
        initInternal(data);
    }

    /**
     * <p>
     * Tries to find the next token. Returned {@code byte} value will be one of {@link TokenTypes} constants. If lexer
     * cannot find any valid token it'll return {@link TokenTypes#UNDEFINED}.
     * Preconditions: The caller must call {@link #init(byte[])} or create this object with {@link #PgnLexer(byte[])} or
     * {@link #PgnLexer(byte[], boolean)} to initialize the array.
     *
     * @return The next occurred token.
     */
    public byte nextToken() {
        try {
            switch (scope) {
                case SCOPE_TAG_PAIR:
                    tagPair();
                    break;
                case SCOPE_MOVE_TEXT:
                    moveText();
                    break;
                case SCOPE_GAMETERM:
                    skipWhiteSpace();
                    determineScope();
                    nextToken();
                    break;
                case SCOPE_UNDEFINED:
                    lastToken = UNDEFINED;
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // If pointer exceeds array length that means that some thing unexpected happens.
            // We need to set internal state of object "Done".
            terminate();
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
        return new String(data, pointer - tokenLength, tokenLength);
    }

    /**
     * The last determined token. If lexer not initialized or in "Done" state {@link TokenTypes#UNDEFINED} will be
     * returned.
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
        return pointer;
    }

    /**
     * Changes the position by {@code offset} steps. If {@code offset} is negative, the position will shift back.
     * In this implementation there are no checks to go beyond the array.
     *
     * @param offset How much to move the position.
     */
    public void positionOffset(int offset) {
        pointer += offset;
    }

    /**
     * Aligns the position with the length of the last token.
     */
    @SuppressWarnings("unused")
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
     * @see #queue(byte)
     */
    public Queue<Byte> queue() {
        return queue(UNDEFINED);
    }

    /**
     * Records all tokens between {@link #lastToken()} and {@code terminationToken}. This implementation will not
     * include {@code terminationToken} as last element in returned {@code Queue}.
     *
     * @param terminationToken When lexer occurs this token it'll stops.
     *
     * @return The tokens {@code Queue} between current token and {@code terminationToken}
     */
    public Queue<Byte> queue(final byte terminationToken) {
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

    @Override
    public String toString() {
        return "PgnLexer{" +
                "lastToken=" + TokenTypes.descOf(lastToken) +
                '}';
    }

    byte[] data() {
        return data;
    }

    void terminate() {
        lastToken = UNDEFINED;
        scope = SCOPE_UNDEFINED;
        pointer = data.length - 1;
        tokenLength = 0;
    }

    /**
     * The main initialization method. This method defines object internal state.
     *
     * @param data The input data that need to be tokenized.
     */
    private void initInternal(final byte[] data) {
        this.data = Objects.requireNonNull(data);
        pointer = 0;
        tokenLength = 0;
        line = 1;
        lastToken = UNDEFINED;
        scope = SCOPE_UNDEFINED;

        if (data.length > 0) {
            skipWhiteSpace();
            determineScope();
        }
    }

    /**
     * Determines in witch part of PGN data lexer currently working.
     *
     * @see LexicalScope
     */
    private void determineScope() {
        if (pointer >= data.length) {
            scope = SCOPE_UNDEFINED;
            return;
        }

        final byte current = data[pointer];

        if (current == '[') {
            scope = SCOPE_TAG_PAIR;
        } else if (Character.isLetterOrDigit(current)) {
            scope = SCOPE_MOVE_TEXT;
        } else {
            scope = SCOPE_UNDEFINED;
        }
    }

    private void moveText() {
        final byte current = data[pointer];

        if (((current == '1' || current == '0') && (pointer < data.length - 1 && (data[pointer + 1] == '-' || data[pointer + 1] == '/')))
                || current == '*') {
            lastToken = GAMETERM;
            scope = SCOPE_GAMETERM;

            if (current == '*') {
                tokenLength = 1;
            } else if (data[pointer + 1] != '/') {
                tokenLength = 3;
            } else {
                tokenLength = 7;
            }
            pointer += tokenLength;
        } else if (lastToken == COMMENT_BEGIN && ByteUtils.isDefined(current)) {
            lastToken = COMMENT;
            final int commentEnd = ByteUtils.unescapedChar(data, pointer + 1, '}');
            tokenLength = commentEnd - pointer;
            pointer += tokenLength;
        } else if (ByteUtils.isLetter(current)) {
            switch (lastToken) {
                case DOT:
                case UNDEFINED:
                case MOVE_BLACK:
                    lastToken = MOVE_WHITE;
                    tokenLength = ByteUtils.moveEnd(data, pointer) - pointer;
                    pointer += tokenLength;
                    break;
                case MOVE_WHITE:
                case COMMENT_END:
                case VARIATION_END:
                case NAG:
                case SKIP_PREV_MOVE:
                case ROL_COMMENT:
                    lastToken = MOVE_BLACK;
                    tokenLength = ByteUtils.moveEnd(data, pointer) - pointer;
                    pointer += tokenLength;
                    break;
            }

        } else {
            switch (current) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    lastToken = MOVE_NUMBER;
                    final int whiteSpace = ByteUtils.whitespaceOrChar(data, pointer, ' ', '.') + 1;
                    final int i = whiteSpace - pointer;
                    tokenLength = i == 1 ? 1 : i - 1;
                    pointer += tokenLength;
                    break;
                case '.':
                    if (data[pointer + 1] == '.' && data[pointer + 2] == '.') {
                        lastToken = SKIP_PREV_MOVE;
                        tokenLength = 3;
                    } else {
                        lastToken = DOT;
                        tokenLength = 1;
                    }

                    pointer += tokenLength;
                    break;
                case ' ':
                    skipWhiteSpace();
                    nextToken();
                    break;
                case '{':
                    lastToken = COMMENT_BEGIN;
                    pointer++;
                    tokenLength = 1;
                    break;
                case '}':
                    lastToken = COMMENT_END;
                    pointer++;
                    tokenLength = 1;
                    break;
                case ';':
                    lastToken = ROL_COMMENT;
                    pointer++;
                    final int commentEnd = ByteUtils.newLine(data, pointer);
                    tokenLength = commentEnd - pointer;
                    pointer += tokenLength;
                    break;
                case '(':
                    lastToken = VARIATION_BEGIN;
                    pointer++;
                    tokenLength = 1;
                    break;
                case ')':
                    lastToken = VARIATION_END;
                    pointer++;
                    tokenLength = 1;
                    break;
                case '$':
                    lastToken = NAG;
                    final int endPos = ByteUtils.whitespaceOrChar(data, pointer + 1, '$', '{', '(', ')', '*');
                    tokenLength = endPos - pointer;
                    pointer += tokenLength;
                    break;
                case '\n':
                    line++;
                    pointer++;
                    nextToken();
                    break;
                case '\r':
                    pointer++;
                    nextToken();
                    break;
            }
        }
    }

    private void tagPair() {
        final byte current = data[pointer];

        switch (current) {
            case '[':
                lastToken = TP_BEGIN;
                pointer++;
                tokenLength = 1;
                break;
            case '"':
                switch (lastToken) {
                    case TP_VALUE:
                        lastToken = TP_VALUE_END;
                        pointer++;
                        tokenLength = 1;
                        break;
                    case TP_NAME:
                        lastToken = TP_VALUE_BEGIN;
                        pointer++;
                        tokenLength = 1;
                        break;
                    case TP_NAME_VALUE_SEP:
                        lastToken = TP_VALUE_BEGIN;
                        pointer++;
                        tokenLength = 1;
                        break;
                    case TP_VALUE_BEGIN:
                        lastToken = TP_VALUE_END;
                        pointer++;
                        tokenLength = 1;
                        break;
                    default:
                        lastToken = UNDEFINED;
                        break;
                }
                break;
            case ']':
                lastToken = TP_END;
                pointer++;
                tokenLength = 1;
                break;
            case ' ':
                if (lastToken == TP_NAME) {
                    lastToken = TP_NAME_VALUE_SEP;
                    pointer++;
                    tokenLength = 1;
                    break;
                }
            case '\n':
                line++;
                pointer++;
                nextToken();
                break;
            case '\r':
                pointer++;
                nextToken();
                break;
            default:
                if (ByteUtils.isLetter(current) || (Character.isDefined(current) && lastToken == TP_VALUE_BEGIN)) {
                    switch (lastToken) {
                        case TP_BEGIN:
                            lastToken = TP_NAME;
                            final int whiteSpace = untilChar(' ');
                            tokenLength = whiteSpace - pointer;
                            pointer += tokenLength;
                            break;
                        case TP_VALUE_BEGIN:
                            lastToken = TP_VALUE;
                            final int pos = untilChar('"');
                            tokenLength = pos - pointer;
                            pointer += tokenLength;
                            break;
                        default:
                            lastToken = UNDEFINED;
                            break;
                    }
                } else {
                    skipWhiteSpace();
                    determineScope();
                    nextToken();
                }
                break;
        }
    }

    private int untilChar(char c) {
        int pos = pointer;
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

        while (isWhiteSpace && pointer < length) {
            switch (data[pointer]) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    pointer++;
                    break;
                default:
                    isWhiteSpace = false;
            }
        }
    }
}
