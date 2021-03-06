package com.github.nstdio.libpgn.core.parser;

import static com.github.nstdio.libpgn.common.ExceptionUtils.wrapChecked;
import static com.github.nstdio.libpgn.core.TokenTypes.COMMENT;
import static com.github.nstdio.libpgn.core.TokenTypes.COMMENT_BEGIN;
import static com.github.nstdio.libpgn.core.TokenTypes.COMMENT_END;
import static com.github.nstdio.libpgn.core.TokenTypes.DOT;
import static com.github.nstdio.libpgn.core.TokenTypes.GAMETERM;
import static com.github.nstdio.libpgn.core.TokenTypes.MOVE_BLACK;
import static com.github.nstdio.libpgn.core.TokenTypes.MOVE_NUMBER;
import static com.github.nstdio.libpgn.core.TokenTypes.MOVE_WHITE;
import static com.github.nstdio.libpgn.core.TokenTypes.NAG;
import static com.github.nstdio.libpgn.core.TokenTypes.ROL_COMMENT;
import static com.github.nstdio.libpgn.core.TokenTypes.SKIP_PREV_MOVE;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_BEGIN;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_END;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_NAME;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_NAME_VALUE_SEP;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_VALUE;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_VALUE_BEGIN;
import static com.github.nstdio.libpgn.core.TokenTypes.TP_VALUE_END;
import static com.github.nstdio.libpgn.core.TokenTypes.UNDEFINED;
import static com.github.nstdio.libpgn.core.TokenTypes.VARIATION_BEGIN;
import static com.github.nstdio.libpgn.core.TokenTypes.VARIATION_END;
import static com.github.nstdio.libpgn.core.parser.LexicalScope.SCOPE_GAMETERM;
import static com.github.nstdio.libpgn.core.parser.LexicalScope.SCOPE_MOVE_TEXT;
import static com.github.nstdio.libpgn.core.parser.LexicalScope.SCOPE_TAG_PAIR;
import static com.github.nstdio.libpgn.core.parser.LexicalScope.SCOPE_UNDEFINED;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.io.PgnInputStream;
import com.github.nstdio.libpgn.io.PgnInputStreamFactory;

public class InputStreamPgnLexer implements PgnLexer {
    private final PgnInputStream in;
    private byte scope = SCOPE_UNDEFINED;
    private byte lastToken = UNDEFINED;
    private int tokenLength;
    private int lastRead;
    private int line;

    /**
     * Constructs the new lexer instance with provided input stream.
     *
     * @param in The data source.
     */
    public InputStreamPgnLexer(final PgnInputStream in) {
        this.in = Objects.requireNonNull(in);
        line = 1;
    }

    /**
     * Constructs the new lexer instance with provided file as data source.
     *
     * @param file The data source.
     *
     * @return The newly created lexer instance.
     *
     * @throws NullPointerException if {@code file} is null.
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static InputStreamPgnLexer of(final File file) {
        Objects.requireNonNull(file);
        return new InputStreamPgnLexer(PgnInputStreamFactory.of(file));
    }

    /**
     * Constructs new lexer with input stream as data source.
     *
     * @param in The input stream.
     *
     * @return The newly created lexer instance.
     *
     * @throws NullPointerException if {@code in} is null.
     */
    public static InputStreamPgnLexer of(final InputStream in) {
        Objects.requireNonNull(in);
        return new InputStreamPgnLexer(PgnInputStreamFactory.of(in));
    }

    /**
     * Constructs new lexer with byte array as data source.
     *
     * @param bytes The input bytes.
     *
     * @return The newly created lexer instance.
     *
     * @throws NullPointerException if {@code bytes} is null.
     */
    public static InputStreamPgnLexer of(final byte[] bytes) {
        Objects.requireNonNull(bytes);
        return of(new ByteArrayInputStream(bytes));
    }

    /**
     * The line number is incremented only when {@literal \n} occurred.
     *
     * @return The current line.
     */
    public int line() {
        return line;
    }

    @Override
    public byte[] read() {
        if (tokenLength == -1) {
            return null;
        }

        if (tokenLength == 1) {
            return new byte[]{(byte) lastRead};
        }

        final byte[] value = new byte[tokenLength];
        value[0] = (byte) lastRead;

        try {
            in.read(value, 1, tokenLength - 1);

            return value;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public byte next() {
        try {
            if (scope == SCOPE_UNDEFINED) {
                determineScope();
            }

            switch (scope) {
                case SCOPE_TAG_PAIR:
                    tagPair();
                    break;
                case SCOPE_MOVE_TEXT:
                    moveText();
                    break;
                case SCOPE_GAMETERM:
                    in.skipWhiteSpace();
                    determineScope();
                    next();
                    break;
                case SCOPE_UNDEFINED:
                    terminate();
                    break;
            }
        } catch (IOException e) {
            // If pointer exceeds array length that means that some thing unexpected happens.
            // We need to set internal state of object "Done".
            try {
                terminate();
            } catch (IOException ignore) {

            }
        }

        return lastToken;
    }

    private void terminate() throws IOException {
        lastToken = UNDEFINED;
        scope = SCOPE_UNDEFINED;
        tokenLength = -1;
        in.close();
    }

    /**
     * Determines in witch part of PGN data lexer currently working.
     *
     * @see LexicalScope
     */
    private void determineScope() throws IOException {
        determineScope(in.readAhead(1));
    }

    private void determineScope(final int read) throws IOException {
        switch (read) {
            case ' ':
            case '\n':
            case '\t':
            case '\r':
                in.skipWhiteSpace();
                determineScope();
                break;
            case '[':
                scope = SCOPE_TAG_PAIR;
                break;
            case '{': // the game comment
                scope = SCOPE_MOVE_TEXT;
                break;
            default:
                scope = Character.isLetterOrDigit(read) ? SCOPE_MOVE_TEXT : SCOPE_UNDEFINED;
                break;
        }
    }

    private void moveText() throws IOException {
        lastRead = in.read();
        final int current = lastRead;

        if (((current == '1' || current == '0') && (in.readAhead(1) == '-' || in.readAhead(1) == '/'))
                || current == '*') {
            lastToken = GAMETERM;
            scope = SCOPE_GAMETERM;

            if (current == '*') {
                tokenLength = 1;
            } else if (in.readAhead(1) != '/') {
                tokenLength = 3;
            } else {
                tokenLength = 7;
            }
        } else if (lastToken == COMMENT_BEGIN && Character.isDefined((int) (byte) current)) {
            lastToken = COMMENT;
            tokenLength = in.until('}');
        } else if (Character.isLetter((byte) current)) {
            switch (lastToken) {
                case DOT:
                case UNDEFINED:
                case MOVE_BLACK:
                    lastToken = MOVE_WHITE;
                    tokenLength = in.until(' ', '\r', '\n', '\t', '{', '(', ')', '$', ';', '*');
                    break;
                case MOVE_WHITE:
                case COMMENT_END:
                case VARIATION_END:
                case NAG:
                case SKIP_PREV_MOVE:
                case ROL_COMMENT:
                    lastToken = MOVE_BLACK;
                    tokenLength = in.until(' ', '\r', '\n', '\t', '{', '(', ')', '$', ';', '*');
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
                    final int whiteSpace = in.until(' ', '\r', '\n', '\t', '.', '\0', '\0', '\0', '\0', '\0') + 1;

                    tokenLength = whiteSpace == 1 ? 1 : whiteSpace - 1;
                    break;
                case '.':
                    if (in.readAhead(1) == '.' && in.readAhead(2) == '.') {
                        lastToken = SKIP_PREV_MOVE;
                        tokenLength = 3;
                    } else {
                        lastToken = DOT;
                        tokenLength = 1;
                    }

                    break;
                case ' ':
                    next();
                    break;
                case '{':
                    lastToken = COMMENT_BEGIN;
                    tokenLength = 1;
                    break;
                case '}':
                    lastToken = COMMENT_END;
                    tokenLength = 1;
                    break;
                case ';':
                    lastToken = ROL_COMMENT;
                    tokenLength = in.until('\n');
                    break;
                case '(':
                    lastToken = VARIATION_BEGIN;
                    tokenLength = 1;
                    break;
                case ')':
                    lastToken = VARIATION_END;
                    tokenLength = 1;
                    break;
                case '$':
                    lastToken = NAG;
                    tokenLength = in.until(' ', '\r', '\n', '\t', '$', '{', '(', ')', '*', '\0');
                    break;
                case '\n':
                    next();
                    break;
                case '\r':
                    next();
                    break;
            }
        }
    }

    private void tagPair() throws IOException {
        lastRead = in.read();
        final int current = lastRead;

        switch (current) {
            case '[':
                lastToken = TP_BEGIN;
                tokenLength = 1;
                break;
            case '"':
                switch (lastToken) {
                    case TP_VALUE:
                    case TP_VALUE_BEGIN:
                        lastToken = TP_VALUE_END;
                        tokenLength = 1;
                        break;
                    default:
                        lastToken = TP_VALUE_BEGIN;
                        tokenLength = 1;
                        break;
                }
                break;
            case ']':
                lastToken = TP_END;
                tokenLength = 1;
                break;
            case ' ':
                if (lastToken == TP_NAME) {
                    lastToken = TP_NAME_VALUE_SEP;
                    tokenLength = 1;
                    break;
                }
            case '\t':
            case '\n':
            case '\r':
                determineScope();
                next();
                break;
            default:
                if (Character.isLetter((byte) lastRead) || (Character.isDefined(lastRead) && lastToken == TP_VALUE_BEGIN)) {
                    switch (lastToken) {
                        case TP_BEGIN:
                            lastToken = TP_NAME;
                            determineTokenLength(' ', TP_NAME_VALUE_SEP);
                            break;
                        case TP_VALUE_BEGIN:
                        case TP_NAME_VALUE_SEP: // The opening quote is missing.
                            lastToken = TP_VALUE;
                            determineTokenLength('"', TP_VALUE_BEGIN, TP_VALUE_END);
                            break;
                        default:
                            lastToken = UNDEFINED;
                            break;
                    }
                } else {
                    determineScope(lastRead);
                    next();
                }
                break;
        }
    }

    private void determineTokenLength(int terminal, byte... expectedTokens) throws IOException {
        try {
            tokenLength = in.until(terminal);
        } catch (EOFException e) {
            terminate();
            throw ExceptionBuilder.unexpectedEOF(this, e, expectedTokens);
        }
    }

    @Override
    public byte last() {
        return lastToken;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void skip() {
        wrapChecked(() -> {
            // to avoid boxing
            in.skip(tokenLength - 1);
        });
    }

    @Override
    public void close() {
        wrapChecked(this::terminate);
    }

    @Override
    public void poll(final byte terminationToken) {
        while (lastToken != terminationToken && lastToken != UNDEFINED) {
            skip();
            next();
        }
    }

    @Override
    public String toString() {
        return "InputStreamPgnLexer{" +
                "lastToken=" + TokenTypes.descOf(lastToken) + ", " +
                "scope=" + LexicalScope.descOf(scope) + ", " +
                "lastRead=" + (char) lastRead +
                '}';
    }
}
