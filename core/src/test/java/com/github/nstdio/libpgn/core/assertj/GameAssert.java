package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import org.assertj.core.api.AbstractAssert;

import java.util.Optional;
import java.util.function.Function;

import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class GameAssert extends AbstractAssert<GameAssert, Game> {
    public GameAssert(final Game game) {
        super(game, GameAssert.class);
    }

    public GameAssert isWhiteMoveEqualTo(final int moveNo, final String expectedMove) {

        assertThat(actual.moves())
                .element(moveNo - 1)
                .satisfies(movetext -> {
                    assertThat(movetext.moveNo()).isEqualTo(moveNo);
                    assertThat(movetext.white())
                            .isPresent()
                            .map(Move::move)
                            .hasValue(expectedMove.getBytes());
                });

        return isMoveEqualTo(moveNo, expectedMove, MoveText::white);
    }

    public GameAssert isBlackMoveEqualTo(final int moveNo, final String expectedMove) {
        return isMoveEqualTo(moveNo, expectedMove, MoveText::black);
    }

    private GameAssert isMoveEqualTo(final int moveNo, final String expectedMove, final Function<MoveText, Optional<Move>> moveExtractor) {
        assertThat(actual.moves())
                .element(moveNo - 1)
                .satisfies(movetext -> {
                    assertThat(movetext.moveNo()).isEqualTo(moveNo);
                    assertThat(moveExtractor.apply(movetext).map(Move::move).orElse(null)).isEqualTo(expectedMove.getBytes());
                });

        return this;
    }

    public ResultAssert result() {
        return assertThat(actual.gameResult());
    }

    public GameAssert isResultUnknown() {
        result().isUnknown();
        return this;
    }

    public GameAssert isResultWhiteWin() {
        result().isWhiteWin();
        return this;
    }
}
