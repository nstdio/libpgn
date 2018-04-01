package com.github.nstdio.libpgn.fen;


import com.github.nstdio.libpgn.fen.assertj.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.nstdio.libpgn.fen.FEN.*;
import static com.github.nstdio.libpgn.fen.assertj.Assertions.assertThatUnexpectedCharacter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FENsTest {

    @Test
    public void importStringCastling() {
        final Map<String, Integer> map = new LinkedHashMap<>();

        map.put("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR b KQkq - 0 4", C_WHITE_KING | C_WHITE_QUEEN | C_BLACK_KING | C_BLACK_QUEEN);
        map.put("r1b4r/pp2kppp/2nN1n2/4p3/2P5/8/P3PPPP/R1B1KB1R w KQ - 1 13", C_WHITE_KING | C_WHITE_QUEEN);
        map.put("3rr3/pp4pp/3B1k2/2P1p3/3nP3/3B1PPb/P4K1P/R6R b - - 2 23", C_NONE);
        map.put("r3kb1r/p1pN2pp/1N6/8/8/1n6/P3PKPP/2B2n1R w kq - 2 14", C_BLACK_KING | C_BLACK_QUEEN);
        map.put("N3kb1r/p1pN2pp/8/8/8/1n6/P3PKPP/2B2n1R b k - 0 14", C_BLACK_KING);
        map.put("rnbqkb1r/pppp1ppp/4p3/8/4n3/5N2/PPPPBPPP/RNBQK1R1 b Qkq - 1 4", C_WHITE_QUEEN | C_BLACK_KING | C_BLACK_QUEEN);
        map.put("rnbqkb1r/pppp1ppp/4p3/8/4n3/5N2/PPPPBPPP/RNBQK1R1 b q - 1 4", C_BLACK_QUEEN);
        map.put("rnbqkb1r/pppp1ppp/4p3/8/4n3/5N2/PPPPBPPP/RNBQK1R1 b Qq - 1 4", C_WHITE_QUEEN | C_BLACK_QUEEN);

        map.forEach((fenStr, castle) -> assertThat(FENs.of(fenStr))
                .extracting("castle")
                .containsExactly(castle));
    }

    @Test
    public void enPassantPresent() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR b KQkq e6 0 4"))
                .extracting("inPassingRank", "inPassingFile")
                .containsExactly(5, 4);
    }

    @Test
    public void enPassantAbsent() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR b KQkq - 0 4"))
                .extracting("inPassingRank", "inPassingFile")
                .containsExactly(0, 0);
    }

    @Test
    public void withoutCastlingAndEnPassant() {
        assertThat(FENs.of("3rr3/pp4pp/3B1k2/2P1p3/3nP3/3B1PPb/P4K1P/R6R b - - 2 23"))
                .extracting("inPassingRank", "inPassingFile", "castle")
                .containsExactly(0, 0, C_NONE);
    }

    @Test
    public void enPassantWithHalfClock() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR b KQkq e6 8 4"))
                .extracting("inPassingRank", "inPassingFile", "halfMoveClock")
                .containsExactly(5, 4, 8);
    }

    @Test
    public void moveTurn() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR b KQkq - 0 4"))
                .extracting("whiteMove")
                .containsExactly(false);

        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 0 4"))
                .extracting("whiteMove")
                .containsExactly(true);
    }

    @Test
    public void moveNumber() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 0 4"))
                .extracting("move")
                .containsExactly(4);
    }

    @Test
    public void halfMoveClock() {
        assertThat(FENs.of("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 2 4"))
                .extracting("halfMoveClock")
                .containsExactly(2);
    }

    @Test
    public void moreThen8Rows() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FENs.of("pp1p2pp/pp1p2pp/pp1p2pp/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 2 4"))
                .withMessage("Attempt to parse more than 8 rows.");
    }

    @Test
    public void lessThen8Rows() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FENs.of("2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 2 4"))
                .withMessage("8 rows were expected but actual parsed %d", 6);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FENs.of("RNBQKBNR w KQkq - 2 4"))
                .withMessage("8 rows were expected but actual parsed %d", 1);
    }

    @Test
    public void noRows() {
        assertThatUnexpectedCharacter("w KQkq - 2 4", 'w', 0);
    }

    @Test
    public void spaceInRows() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FENs.of("pP2p3 /pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - 0 4"))
                .withMessage("8 rows were expected but actual parsed %d", 1);
    }

    @Test
    public void invalidMoveTurn() {
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR c KQkq - 0 4", 'c', 47);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR 1 KQkq - 0 4", '1', 47);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR KQkq - 0 4", 'K', 47);
    }

    @Test
    public void invalidCastling() {
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w 1Qkq - 0 4", '1', 49);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w QLkq - 0 4", 'L', 50);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w L - 0 4", 'L', 49);
    }

    @Test
    public void invalidEnPassant() {
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq j3 0 4", 'j', 54);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq 3 0 4", '3', 54);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq d5 0 4", '5', 55);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq d9 0 4", '9', 55);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq d1 0 4", '1', 55);
    }

    @Test
    public void invalidHalfClockMove() {
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq d3 -1 4", '-', 57);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - v 4", 'v', 56);
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq - ~ 4", '~', 56);
    }

    @Test
    public void invalidMoveCount() {
        assertThatUnexpectedCharacter("pP2p3/pp1p2pp/2P2p2/4p3/2P5/8/PP2PPPP/RNBQKBNR w KQkq d3 1 w", 'w', 59);
    }
}