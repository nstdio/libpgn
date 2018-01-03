package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.Movetext;
import org.assertj.core.api.AbstractAssert;

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
                    assertThat(movetext.whiteMove()).isEqualTo(expectedMove);
                });

        return isMoveEqualTo(moveNo, expectedMove, Movetext::whiteMove);
    }

    public GameAssert isBlackMoveEqualTo(final int moveNo, final String expectedMove) {
        return isMoveEqualTo(moveNo, expectedMove, Movetext::blackMove);
    }

    private GameAssert isMoveEqualTo(final int moveNo, final String expectedMove, final Function<Movetext, String> moveExtractor) {
        assertThat(actual.moves())
                .element(moveNo - 1)
                .satisfies(movetext -> {
                    assertThat(movetext.moveNo()).isEqualTo(moveNo);
                    assertThat(moveExtractor.apply(movetext)).isEqualTo(expectedMove);
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
