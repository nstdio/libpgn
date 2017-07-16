package com.github.nstdio.libpgn.core.parser;

import javax.annotation.Nullable;

/**
 * One of two variants of the parser. The implementation of this type of parser does not assume input data. The class
 * implementing this interface should consider the next or the last token from the lexer.
 *
 * @param <T> This type of data parser should return after processing the data from the lexer.
 */
public interface Parser<T> {

    /**
     * It is assumed that the next lexer token is intended specifically for this parser. In this regard, the parser can
     * immediately go to the process of analyzing the input stream from the lexer by rolling up each next token until
     * the terminal token is traced. The parser can throw an exception if the first token that it counted from the lexer
     * does not match its expectations.
     *
     * @return The parsed data.
     */
    T parse();

    /**
     * There is no assumption that the last token is an important token for a particular parser. At the very least, this
     * method should check the last token from the lexer's output stream for suitability for analysis. If a particular
     * implementation of the method decides that the token corresponds to it in the case must enter {@link #parse()}
     * logic. If, however, the token is not suitable for processing, this method MUST return <code>null</code>
     *
     * @return The parsed data or <code>null</code>.
     */
    @Nullable
    T tryParse();
}
