package com.github.nstdio.libpgn.core.parser;

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
import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThatLexer;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.nstdio.libpgn.core.TokenTypes;

public class InputStreamPgnLexerTest {
    private static Stream<Arguments> lineNumberSource() {
        return Stream.of(
                Arguments.of(1, "1. e4 {No new lines should be counted as one line} d5"),
                Arguments.of(2, "1. e4\n {Two lines} \nd5"),
                Arguments.of(3, "1. e4 {This is\n multiline\n comment} d5"),
                Arguments.of(4, "1.\n e4 {\nThis is multiline \ncomment} d5")
        );
    }

    @Test
    void tagPair() {
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
    void comments() {
        final byte[] tokens = {
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
    void moveTextSimple() {
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
    void moveTextWithVariation() {
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
    void moveTextWithNag() {
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
    void blackVariation() {
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
    void restOfTheLineComment() {
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
    void withoutMoveNumbers() {
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
    void emptyTagPairValue() {
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
    void skipPrevMove() {
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
    void nag() {
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
    void sequentialComments() {
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
    void emptyInput() {
        assertThatLexer("")
                .nextTokenIsEqualTo(UNDEFINED)
                .readIsNull();
    }

    @Test
    void illegalInput() {
        final String[] inputs = {
                "\n", "", "\n\r", "  \t", ";", ".", "\0",
                "\r\n", "abc",
        };

        assertThat(inputs).allSatisfy(strings -> assertThatLexer(strings).nextTokenIsEqualTo(UNDEFINED)
                .readIsNull());
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("lineNumberSource")
    void lineNumber(int lines, String input) {
        assertThatLexer(input).linesCountIsEqualTo(lines);
    }

    @Test
    void poll() {
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

    @Test
    void preGameComment() {
        final String input = "{Comment} 1. e4 *";

        assertThatLexer(input)
                .commentReadIsEqualTo("Comment")
                .nextTokenIsEqualTo(MOVE_NUMBER).readIsEqualTo("1")
                .nextTokenIsEqualTo(DOT).readIsEqualTo(".")
                .nextTokenIsEqualTo(MOVE_WHITE).readIsEqualTo("e4")
                .nextTokenIsEqualTo(GAMETERM).readIsEqualTo("*")
                .nextTokenIsEqualTo(UNDEFINED).readIsNull();
    }

    @Test
    void tagPairNameIsMissing() {
        final String input = "[\"Value\"] 1. e4 *";

        assertThatLexer(input)
                .nextTokenIsEqualTo(TP_BEGIN).readIsEqualTo("[")
                .nextTokenIsEqualTo(TP_VALUE_BEGIN).readIsEqualTo("\"")
                .nextTokenIsEqualTo(TP_VALUE).readIsEqualTo("Value")
                .nextTokenIsEqualTo(TP_VALUE_END).readIsEqualTo("\"")
                .nextTokenIsEqualTo(TP_END).readIsEqualTo("]");
    }

    @Test
    void tagPairNameOpeningQuoteIsMissing() {
        final String input = "[Name Value\"]";

        assertThatLexer(input)
                .nextTokenIsEqualTo(TP_BEGIN).readIsEqualTo("[")
                .nextTokenIsEqualTo(TP_NAME).readIsEqualTo("Name")
                .nextTokenIsEqualTo(TP_NAME_VALUE_SEP).readIsEqualTo(" ")
                .nextTokenIsEqualTo(TP_VALUE).readIsEqualTo("Value")
                .nextTokenIsEqualTo(TP_VALUE_END).readIsEqualTo("\"")
                .nextTokenIsEqualTo(TP_END).readIsEqualTo("]");
    }
}