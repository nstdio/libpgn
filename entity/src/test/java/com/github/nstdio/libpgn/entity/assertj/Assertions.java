package com.github.nstdio.libpgn.entity.assertj;

import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.Result;

public class Assertions {
    public static ResultAssert assertThat(final Result result) {
        return new ResultAssert(result);
    }

    public static MoveAssert assertThat(final Move move) {
        return new MoveAssert(move);
    }

    public static GameAssert assertThat(final Game game) {
        return new GameAssert(game);
    }
}
