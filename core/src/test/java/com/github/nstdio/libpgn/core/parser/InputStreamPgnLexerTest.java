package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.TokenTypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThatLexer;
import static org.assertj.core.api.Assertions.assertThat;

public class InputStreamPgnLexerTest {
    @Test
    public void tagPair() {
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

        assertThatLexer(input)
                .outputContainsExactly(tokens);
    }

    @Test
    public void comments() {
        final byte tokens[] = {
                MOVE_NUMBER, DOT, MOVE_WHITE, NAG, NAG,
                COMMENT_BEGIN, COMMENT, COMMENT_END,
                MOVE_BLACK, COMMENT_BEGIN, COMMENT, COMMENT_END,
                GAMETERM,
        };

        final String[] games = {
                "1.d4 $1$2 {1e789} e5 {`% #1`  } *",
                "1.d4 $1$2 {  } e5 {  } *",
                //"1.d4 $1$2 {Com\\}ment} e5 {Co\\}mmen\\}t} *",
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
                "   1\n.d4$1$2{Comment}e5{Comment}*",
                "\n\n   1.\r\nd4$1$2{Comment}\n\ne5\n\n{Comment}\n*",
        };

        assertSameOutput(games, tokens);
    }

    @Test
    public void moveTextSimple() {
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

        assertSameOutput(inputs, tokens);
    }

    @Test
    public void moveTextWithVariation() {
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

        assertThatLexer(input).outputContainsExactly(tokens);
    }

    @Test
    public void moveTextWithNag() {
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

        assertSameOutput(inputs, tokens);
    }

    @Test
    public void blackVariation() {
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

        assertSameOutput(inputs, tokens);
    }

    @Test
    public void restOfTheLineComment() {
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

        assertSameOutput(games, tokens);
    }

    @Test
    public void withoutMoveNumbers() {
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

        assertSameOutput(games, tokens);
    }

    @Test
    public void emptyTagPairValue() {
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

        assertSameOutput(games, tokens);
    }

    @Test
    public void skipPrevMove() {
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

        assertSameOutput(games, tokens);
    }

    @Test
    public void nag() {
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

        assertSameOutput(games, tokens);
    }

    private void assertSameOutput(final String[] games, final byte[] tokens) {
        assertThat(games).allSatisfy(s -> assertThatLexer(s).outputContainsExactly(tokens));
    }

    @Test
    public void sequentialComments() {
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

        assertSameOutput(games, tokens);
    }

    @Test
    public void emptyInput() {
        assertThatLexer("")
                .nextTokenIsEqualTo(UNDEFINED)
                .readIsNull();
    }

    @Test
    public void illegalInput() {
        final String[] inputs = {
                "\n", "", "\n\r", "  \t", ";", ".", "\0",
                "\r\n", "abc",
        };

        assertThat(inputs).allSatisfy(strings -> assertThatLexer(strings).nextTokenIsEqualTo(UNDEFINED)
                .readIsNull());
    }

    @Test
    @Disabled("todo")
    public void lineNumber() {
        final Map<Integer, String> inputs = new HashMap<>();

        inputs.put(3, "1. e4 {This is\n multiline\n comment} d5");
        inputs.put(1, "1. e4 {This is comment} d5");
        inputs.put(5, "1.\n e4 {\nThis\n is multiline \ncomment} d5");

        inputs.forEach((key, value) -> assertThatLexer(value).linesCountIsEqualTo(key));

    }

    @Test
    public void poll() {
        final String input = "1. e4 $6$2$3$4$5$1$7 *";

        final InputStreamPgnLexer lexer = InputStreamPgnLexer.of(input.getBytes());
        lexer.next();
        lexer.poll(NAG);

        assertThatLexer(lexer)
                .lastTokenIsEqualTo(NAG).readIsEqualTo("$6")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$2")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$3")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$4")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$5")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$1")
                .nextTokenIsEqualTo(NAG).readIsEqualTo("$7")
                .nextTokenIsEqualTo(TokenTypes.GAMETERM).readIsEqualTo("*")
                .nextTokenIsEqualTo(TokenTypes.UNDEFINED).readIsNull();
    }
}