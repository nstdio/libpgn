package com.asatryan.libpgn.core.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static org.junit.Assert.*;

public class PgnLexerTest {
    private PgnLexer lexer;

    @Before
    public void setUp() throws Exception {
        lexer = new PgnLexer();
    }

    @Test
    public void initialState() throws Exception {
        lexer = new PgnLexer();

        assertEquals(1, lexer.line());

        assertNotNull(lexer.data());
        assertEquals(0, lexer.length());

        assertEquals(UNDEFINED, lexer.lastToken());
        assertEquals(0, lexer.position());
        assertEquals(0, lexer.tokenLength());
    }

    @Test
    public void noCopyInput() throws Exception {
        final char[] input = "1. e4 *".toCharArray();

        lexer.init(input);

        assertSame(input, lexer.data());
    }

    @Test
    public void copyInput() throws Exception {
        final char[] input = "1. e4 *".toCharArray();

        lexer.init(input, true);

        assertNotSame(input, lexer.data());
        assertArrayEquals(input, lexer.data());
    }

    @Test
    public void tagPair() throws Exception {
        final String input = "[Event \"Rapid 15m+4s\"]\n" +
                "[Empty \"\"]\n" +
                "[Site \"?\"]\n" +
                "[Date \"????.??.??\"]\n" +
                "[Round \"?\"]\n" +
                "[White \"Doe, John\"]\n" +
                "[Black \"Stockfish 8 64\"]\n" +
                "[Result \"*\"]\n" +
                "[PlyCount \"4\"]\n" +
                "[TimeControl \"900+4\"]\n" +
                "[WhiteElo \"\"]\n" +
                "[BlackElo \"\"]\n";

        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
        };


        AssertUtils.assertTokensEqual(lexer, input, tokens);
    }

    @Test
    public void moveTextSimple() throws Exception {
        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                MOVE_NUMBER, DOT, MOVE_WHITE, COMMENT_BEGIN, COMMENT, COMMENT_END, MOVE_BLACK, COMMENT_BEGIN, COMMENT, COMMENT_END,
                MOVE_NUMBER, DOT, MOVE_WHITE, MOVE_BLACK,
                MOVE_NUMBER, DOT, MOVE_WHITE, MOVE_BLACK,
                MOVE_NUMBER, DOT, MOVE_WHITE, MOVE_BLACK,
                GAMETERM
        };

        final String[] inputs = {
                "[Event \"Rapid 15m+4s\"]\n\n1. d4 {White Comment} Nf6 {Black Comment} 2. c4 e6 3. Nc3 d5 4. Bg5 Nbd7 1/2-1/2",
                "[Event \"Rapid 15m+4s\"]\n\n1.d4{White Comment}Nf6{Black Comment}2.c4 e6 3.Nc3 d5 4.Bg5 Nbd7 1-0",
                "[Event \"Rapid 15m+4s\"]\n\n1  .  d4   {White Comment}  Nf6  {Black Comment}  2  .c4 e6 3 . Nc3 d5 4 .    Bg5 Nbd7 0-1",
        };

        AssertUtils.assertSameResult(lexer, inputs, tokens);
    }

    @Test
    public void moveTextWithVariation() throws Exception {
        final String input = "[Event \"Rapid 15m+4s\"]\n" +
                "\n" +
                "1. d4 (1. Nc3 {Black comment var} d5) Nf6 2. c4 {Comment} e6 1/2-1/2";

        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                MOVE_NUMBER, DOT, MOVE_WHITE,
                VARIATION_BEGIN,
                MOVE_NUMBER, DOT, MOVE_WHITE, COMMENT_BEGIN, COMMENT, COMMENT_END, MOVE_BLACK,
                VARIATION_END,
                MOVE_BLACK,
                MOVE_NUMBER, DOT, MOVE_WHITE, COMMENT_BEGIN, COMMENT, COMMENT_END, MOVE_BLACK,
                GAMETERM
        };

        AssertUtils.assertTokensEqual(lexer, input, tokens);
    }

    @Test
    public void moveTextWithNag() throws Exception {
        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                MOVE_NUMBER, DOT, MOVE_WHITE, NAG, NAG, NAG, MOVE_BLACK, NAG,
                GAMETERM
        };

        final String[] inputs = {
                "[Event \"Rapid 15m+4s\"]\n" +
                        "\n" +
                        "1. d4 $1$2$3 e5 $11 *",
                "[Event \"Rapid 15m+4s\"]\n\n1. d4$1 $2 $3 e5$11 *",
        };

        AssertUtils.assertSameResult(lexer, inputs, tokens);
    }

    @Test
    public void blackVariation() throws Exception {
        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE, TP_VALUE_END, TP_END,
                MOVE_NUMBER, DOT, MOVE_WHITE, MOVE_BLACK,
                VARIATION_BEGIN,
                MOVE_NUMBER, SKIP_PREV_MOVE, MOVE_BLACK, COMMENT_BEGIN, COMMENT, COMMENT_END,
                VARIATION_END,
                GAMETERM
        };

        final String[] inputs = {
                "[Event \"Rapid 15m+4s\"]\n\n1. d4 e5 (1... Nf6 {C}) *",
                "[Event \"Rapid 15m+4s\"]\n\n1.d4 e5(1...Nf6{C})*",
                "[Event \"Rapid 15m+4s\"]\n\n   1  .  d4   e5 (   1   ...   Nf6     {C})   *   ",
        };

        AssertUtils.assertSameResult(lexer, inputs, tokens);
    }

    @Test
    public void comments() throws Exception {
        final byte tokens[] = {
                MOVE_NUMBER, DOT, MOVE_WHITE, NAG, NAG,
                COMMENT_BEGIN, COMMENT, COMMENT_END,
                MOVE_BLACK, COMMENT_BEGIN, COMMENT, COMMENT_END,
                GAMETERM,
        };
        final String[] games = {
                "1.d4 $1$2 {1e789} e5 {`% #1`  } *",
                "1.d4 $1$2 {  } e5 {  } *",
                "1.d4 $1$2 {Com\\}ment} e5 {Co\\}mmen\\}t} *",
                "1.d4 $1$2 {c} e5 { } *",
                "1.d4 $1$2 {@$#@!a} e5 {!a} *",
                "1. d4$1$2{Comment} e5{Comment} *",
                "1 . d4$1$2 {Comment} e5 {Comment} *",
                "1 . d4$1 $2 {Comment} e5 {Comment} *",
                "1. d4 $1 $2   {Comment} e5    {Comment} *",
                "1. d4$1 $2   {Comment} e5{Comment} *",
                "1.d4$1$2   {Comment}e5{Comment} *",
                "1.d4$1$2{Comment}e5{Comment}*",
                "   1.d4$1$2{Comment}e5{Comment}*",
                "   1.d4$1$2{Comment}e5{Comment}*",
                "   1\n.d4$1$2{Comment}e5{Comment}*",
                "\n\n   1.\r\nd4$1$2{Comment}\n\ne5\n\n{Comment}\n*",
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void restOfTheLineComment() throws Exception {
        final byte[] tokens = {
                MOVE_NUMBER, DOT, MOVE_WHITE, ROL_COMMENT, MOVE_BLACK,
                GAMETERM
        };

        final String[] games = {
                "1. d4 ;This is comment for rest of this line\nd5 *",
                "1. d4;This is comment for rest of this line\n d5 *",
                "1. d4;\n d5 *",
                "1. d4    ;   \n d5 *",
                "1. d4;d3\n\n\nd5\n*",
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void withoutMoveNumbers() throws Exception {
        final byte[] tokens = {
                MOVE_WHITE, COMMENT_BEGIN, COMMENT, COMMENT_END, MOVE_BLACK,
                MOVE_WHITE, MOVE_BLACK,
                GAMETERM
        };

        final String[] games = {
                "d4 {Comment} a5 Nf3 Bc6 *",
                "d4{Comment} a5 Nf3 Bc6 *",
                "d4{Comment}a5 \n\n\nNf3 Bc6 *",
                "  d4     {Comment}a5      Nf3 Bc6 *",
                "\n\n\n  d4     {Comment}a5 Nf3 Bc6 *",
                "\n\n\n  d4     {Comment}a5    Nf3     Bc6     *     ",
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void emptyTagPairValue() throws Exception {
        final String[] games = {
                "[Result \"\"]\n" +
                        "[WhiteElo \"\"]\n" +
                        "[BlackElo \"\"]\n" +
                        "[ECO \"\"]"
        };

        final byte[] tokens = {
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
                TP_BEGIN, TP_NAME, TP_NAME_VALUE_SEP, TP_VALUE_BEGIN, TP_VALUE_END, TP_END,
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void skipPrevMove() throws Exception {
        final String[] games = {
                "1.d4(1.d5 d6)1...f5*",
                "1.d4 (1. d5 d6) 1... f5 *",
                "1.d4(1.d5 d6) 1... f5 *",
                "1.d4(1.d5 d6)1... f5 *",
        };

        final byte[] tokens = {
                MOVE_NUMBER, DOT, MOVE_WHITE,
                VARIATION_BEGIN,
                MOVE_NUMBER, DOT, MOVE_WHITE, MOVE_BLACK,
                VARIATION_END,
                MOVE_NUMBER, SKIP_PREV_MOVE, MOVE_BLACK,
                GAMETERM
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void nag() throws Exception {
        final String[] games = {
                "1. e4 $1$2$3$4$5$6 *",
                "1. e4$1$2$3$4$5$6 *",
                "1.e4$1$2$3$4$5$6*",
                "1. e4 $1    $2$3$4$5$6 *",
                "1. e4 $1 $2    $3$4$5$6 *",
                "1. e4 $1  $2$3 $4$5 $6 *",
                "1. e4 $1    $2 $3 $4$5$6 *",
        };

        final byte[] tokens = {
                MOVE_NUMBER, DOT, MOVE_WHITE,
                NAG, NAG, NAG, NAG, NAG, NAG,
                GAMETERM
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void sequentialComments() throws Exception {
        final String[] games = {
                "1. e4 {Comment} {Comment} {Comment} *",
                "1. e4 {Comment}{Comment}{Comment} *",
                "1. e4{Comment}{Comment}{Comment} *",
                "1.e4{Comment}{Comment}{Comment} *",
                "1.e4{Comment}{Comment}{Comment}*",
                "1.e4{C}{C}{C}*",
                "1.e4{C}{C} {C}*",
                "1.e4{C}{C} {C} *",
                "1.e4{C} {C} {C} *",
                "1.e4 {C}  {C} {C} *",
        };

        final byte[] tokens = {
                MOVE_NUMBER, DOT, MOVE_WHITE,
                COMMENT_BEGIN, COMMENT, COMMENT_END,
                COMMENT_BEGIN, COMMENT, COMMENT_END,
                COMMENT_BEGIN, COMMENT, COMMENT_END,
                GAMETERM
        };

        AssertUtils.assertSameResult(lexer, games, tokens);
    }

    @Test
    public void emptyInput() throws Exception {
        final char[] data = "".toCharArray();

        lexer.init(data);

        assertEquals(UNDEFINED, lexer.nextToken());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = IllegalArgumentException.class)
    public void nullInputWithInit() throws Exception {
        lexer.init(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = IllegalArgumentException.class)
    public void nullInputWithConstructor() throws Exception {
        new PgnLexer(null);
    }

    @Test
    public void illegalInput() throws Exception {
        final String[] inputs = {
                "\n", "", "\n\r", "  \t", ";", ".", "\0",
                "\r\n",
        };

        for (String input : inputs) {
            lexer.init(input.toCharArray());
            assertEquals(UNDEFINED, lexer.nextToken());
        }
    }

    @Test
    public void lineNumber() throws Exception {
        String input = "1.e4 d4\r\n2.e4 d4\r\n *";

        lexer.init(input.toCharArray());

        final Queue<Byte> stream = lexer.stream();
    }
}
