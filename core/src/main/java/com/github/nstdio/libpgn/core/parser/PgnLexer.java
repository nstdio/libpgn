package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TokenTypes;

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
public interface PgnLexer extends AutoCloseable {
    /**
     * <p>
     * Tries to find the next token. Returned {@code byte} value will be one of {@link TokenTypes} constants. If lexer
     * cannot find any valid token it'll return {@link TokenTypes#UNDEFINED}.
     *
     * @return The next occurred token.
     */
    byte next();

    /**
     * Extracts the data.
     */
    byte[] read();

    /**
     * The last determined token. If lexer not initialized or in "Done" state {@link TokenTypes#UNDEFINED} will be
     * returned.
     *
     * @return The last determined token.
     */
    byte last();

    /**
     * Suppresses a certain number of bytes. The number of bytes is determined by the length of the last token read. A
     * more effective version of {@linkplain #read()} depending on the downstream resource.
     */
    void skip();

    /**
     * Polls all tokens until first occurrence of {@code terminationToken} or {@link TokenTypes#UNDEFINED}. In result of
     * method invocation {@code last() == terminationToken || last() == TokenTypes.UNDEFINED} condition will be {@code
     * true}.
     *
     * @param terminationToken When lexer occurs this token or {@link TokenTypes#UNDEFINED} it'll stops.
     */
    void poll(byte terminationToken);

    /**
     * Closes all underlying resources. After this method invocation any subsequent interaction with this object may
     * throw an exception.
     */
    void close();
}
