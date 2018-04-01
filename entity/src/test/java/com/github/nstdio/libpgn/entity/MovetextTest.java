package com.github.nstdio.libpgn.entity;

import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MovetextTest {
    private MoveText movetext;
    private String moveNumber;
    private String whiteMove;
    private String whiteComment;
    private String blackMove;
    private String blackComment;

    @BeforeEach
    public void setUp() {
        moveNumber = "1";
        whiteMove = "e4";
        whiteComment = "White Comment";
        blackMove = "e5";
        blackComment = "Black Comment";

    }

    @AfterEach
    public void tearDown() {
        movetext = null;
    }

    @Test
    public void gettersWithNoVariation() {
        createDefault();

        assertThat(Integer.parseInt(moveNumber)).isEqualTo(movetext.moveNo());
        assertThat(movetext.white().map(Move::move).orElse(null)).containsExactly(whiteMove.getBytes());
        assertThat(movetext.white().map(Move::comment).orElse(null)).containsExactly(whiteComment.getBytes());
        assertThat(movetext.black().map(Move::move).orElse(null)).containsExactly(blackMove.getBytes());
        assertThat(movetext.black().map(Move::comment).orElse(null)).containsExactly(blackComment.getBytes());
    }

    private void createDefault() {
        Move move = Move.of(whiteMove, whiteComment);
        Move move1 = Move.of(blackMove, blackComment);

        movetext = MoveText.of(Integer.parseInt(moveNumber), move, move1);
    }

    @Test
    public void equalsSimple() {
        MoveText m1 = MoveText.ofWhite(1, Move.of("d4"));
        MoveText m2 = MoveText.ofWhite(1, Move.of("d4"));

        assertThat(m1).isEqualTo(m2);
        m1 = MoveText.ofBlack(1, "d5");
        m2 = MoveText.ofBlack(1, "d5");

        assertThat(m1).isEqualTo(m2);

        m1 = MoveText.ofWhite(1, "d5");
        m2 = MoveText.ofWhite(2, "d5");

        assertThat(m1).isNotEqualTo(m2);

        m1 = MoveText.ofWhite(1, "d5");
        m2 = MoveText.ofWhite(1, "d4");

        assertThat(m1).isNotEqualTo(m2);

        m1 = MoveText.ofBlack(1, "d5");
        m2 = MoveText.ofBlack(2, "d4");

        assertThat(m1).isNotEqualTo(m2);
    }

    @Test
    public void nullStrings() {
        assertThatNullPointerException()
                .isThrownBy(() -> MoveText.of(1, (String) null, null));
    }

    @Test
    public void nullMoves() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MoveText.of(1, (Move) null, null));
    }

    @Test
    public void factory() {
        MoveText m = MoveText.ofWhite(1, "d4");
        MoveText m2 = MoveText.ofWhite(1, "d4");
        MoveText m3 = MoveText.ofWhite(1, Move.of("d4"));

        assertThat(m).isEqualTo(m2).isEqualTo(m3);
        assertThat(m2).isEqualTo(m3);

        assertThat(m.moveNo()).isEqualTo(1);
        assertThat(m.white().map(Move::move).orElse(null))
                .containsExactly("d4".getBytes());
        assertThat(m.black()).isNotPresent();


        m = MoveText.ofBlack(1, "d4");

        assertThat(m.moveNo()).isEqualTo(1);
        assertThat(m.black().map(Move::move).orElse(null))
                .containsExactly("d4".getBytes());
        assertThat(m.white()).isNotPresent();
    }

    @Test
    public void listFactory() {
        List<MoveText> expected = Arrays.asList(
                MoveText.of(1, "d4", "Nf6"),
                MoveText.of(2, "c4", "e6"),
                MoveText.of(3, "Nc3", "d5"),
                MoveText.of(4, "Bg5", "Nbd7"),
                MoveText.of(5, "e3", "Be7"),
                MoveText.of(6, "Nf3", "O-O"),
                MoveText.of(7, "Qc2", "c5")
        );

        List<MoveText> actual = MoveText.moves("d4", "Nf6", "c4", "e6", "Nc3", "d5", "Bg5", "Nbd7", "e3", "Be7",
                "Nf3", "O-O", "Qc2", "c5");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void listFactoryWithOdd() {
        final List<MoveText> expected = Collections.singletonList(
                MoveText.ofWhite(1, "d4")
        );

        final List<MoveText> actual = MoveText.moves("d4");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void listFactoryWithEmptyInput() {
        final List<MoveText> moves = MoveText.moves();

        assertThat(moves)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void passingNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> MoveText.moves((String) null));
    }
}