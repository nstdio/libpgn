package com.github.nstdio.libpgn.entity.assertj;

import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import org.assertj.core.api.AbstractAssert;

import java.util.Optional;
import java.util.function.Function;

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

    public GameAssert isResultUnknown() {
        assertThat(actual.gameResult()).isEqualTo(Result.UNKNOWN);
        return this;
    }

    public GameAssert isResultWhiteWin() {
        assertThat(actual.gameResult()).isEqualTo(Result.WHITE);
        return this;
    }
}
