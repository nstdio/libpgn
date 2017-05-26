package com.asatryan.libpgn.core.parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.asatryan.libpgn.core.Configuration.COMMENT_LENGTH_UNLIMITED;
import static com.asatryan.libpgn.core.TokenTypes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentParserTest extends MockedLexerAware {
    private Parser<String> commentParser;

    @Before
    public void setUp() throws Exception {
        commentParser = new CommentParser(lexer, configuration);
    }

    @Test
    public void sequentialComments() throws Exception {
        final String firstComment = "First Comment";
        final String secondComment = "Second Comment";

        doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT)
                .when(lexer).lastToken();

        doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .when(lexer).nextToken();

        doReturn(firstComment).doReturn(secondComment)
                .when(lexer).extract();

        when(configuration.trimComment()).thenReturn(false);
        when(configuration.commentMaxLength()).thenReturn(Integer.MAX_VALUE);

        final String comment = commentParser.tryParse();

        assertEquals(firstComment + secondComment, comment);
    }

    @Test
    public void commentMaxLength() throws Exception {
        final int maxLen = 4;
        final String comment = "Comment";
        final String expected = comment.substring(0, maxLen);

        when(configuration.commentMaxLength()).thenReturn(maxLen);

        mockLexerMethodResults(comment);

        final String actual = commentParser.tryParse();

        assertEquals(expected, actual);
    }


    @Test
    public void trimComment() throws Exception {
        final String comment = "    Comment   ";
        final String expected = comment.trim();

        when(configuration.commentMaxLength()).thenReturn(COMMENT_LENGTH_UNLIMITED);
        when(configuration.trimComment()).thenReturn(true);

        mockLexerMethodResults(comment);

        final String actual = commentParser.tryParse();

        assertEquals(expected, actual);
    }

    @Test
    public void skipComment() throws Exception {
        when(configuration.skipComment()).thenReturn(true);

        assertNull(commentParser.tryParse());
    }

    private void mockLexerMethodResults(String comment) {
        doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .when(lexer).lastToken();

        doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .when(lexer).nextToken();

        doReturn(comment)
                .when(lexer).extract();
    }
}