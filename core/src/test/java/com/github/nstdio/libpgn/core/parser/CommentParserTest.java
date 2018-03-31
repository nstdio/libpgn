package com.github.nstdio.libpgn.core.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.nstdio.libpgn.core.Configuration.COMMENT_LENGTH_UNLIMITED;
import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentParserTest extends MockedEnvAware {
    private Parser<byte[]> commentParser;

    @BeforeEach
    public void setUp() {
        commentParser = new CommentParser(mockLexer, mockConfiguration);
    }

    @Test
    public void sequentialComments() {
        final String firstComment = "First Comment";
        final String secondComment = "Second Comment";

        doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT)
                .when(mockLexer).last();

        doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .when(mockLexer).next();

        doReturn(firstComment.getBytes()).doReturn(secondComment.getBytes())
                .when(mockLexer).read();

        when(mockConfiguration.trimComment()).thenReturn(false);
        when(mockConfiguration.commentMaxLength()).thenReturn(Integer.MAX_VALUE);

        final byte[] comment = commentParser.tryParse();
        assertThat(comment)
                .containsExactly((firstComment + secondComment).getBytes());
    }

    @Test
    public void commentMaxLength() {
        final int maxLen = 4;
        final String comment = "Comment";
        final String expected = comment.substring(0, maxLen);

        when(mockConfiguration.commentMaxLength()).thenReturn(maxLen);

        mockLexerMethodResults(comment);

        final byte[] actual = commentParser.tryParse();

        assertThat(actual)
                .containsExactly(expected.getBytes());
    }


    @Test
    public void trimComment() throws Exception {
        final String comment = "    Comment   ";
        final String expected = comment.trim();

        when(mockConfiguration.commentMaxLength()).thenReturn(COMMENT_LENGTH_UNLIMITED);
        when(mockConfiguration.trimComment()).thenReturn(true);

        mockLexerMethodResults(comment);

        final byte[] actual = commentParser.tryParse();

        assertThat(actual).containsExactly(expected.getBytes());
    }

    @Test
    public void skipComment() {
        when(mockConfiguration.skipComment()).thenReturn(true);

        assertThat(commentParser.tryParse()).isNull();
    }

    private void mockLexerMethodResults(String comment) {
        doReturn(COMMENT_BEGIN)
                .doReturn(COMMENT)
                .when(mockLexer).last();

        doReturn(COMMENT)
                .doReturn(COMMENT_END)
                .when(mockLexer).next();

        doReturn(comment.getBytes())
                .when(mockLexer).read();
    }
}