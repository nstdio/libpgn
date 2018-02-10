package com.github.nstdio.libpgn.core.fen;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FENTest extends FenTestCaseReader {

    @Test
    public void pawn() {
        file("pawn");
    }

    @Test
    public void knight() {
        file("knight");
    }

    @Test
    public void rook() {
        file("rook");
    }

    @Test
    public void master() {
        file("master");
    }

    @Test
    public void castling() {
        file("castling");
    }

    @Test
    public void moveOnImported() {
        final FEN fen = FENs.initial();

        assertThat(fen.move("d4"))
                .isEqualTo("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3 0 1");

        assertThat(fen.move("Nf6"))
                .isEqualTo("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2");

        assertThat(fen.move("c4"))
                .isEqualTo("rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq c3 0 2");

        assertThat(fen.move("g6"))
                .isEqualTo("rnbqkb1r/pppppp1p/5np1/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3");

        assertThat(fen.move("Nc3"))
                .isEqualTo("rnbqkb1r/pppppp1p/5np1/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq - 1 3");
    }

    private void file(final String dir) {
        forEachTestCaseIn(dir, fenTestCase -> {
            final List<String> fens = fenTestCase.getFens();
            for (int i = 0; i < fens.size(); i++) {
                final String actual = FENs.ofPlainPgn(fenTestCase.getMoves(), i + 1);
                assertThat(actual)
                        .as(fenTestCase.getFilename())
                        .isEqualTo(fens.get(i));
            }
        });
    }
}