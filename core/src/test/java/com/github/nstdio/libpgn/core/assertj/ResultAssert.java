package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.Game;
import org.assertj.core.api.AbstractAssert;

import static com.github.nstdio.libpgn.core.Game.Result.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ResultAssert extends AbstractAssert<ResultAssert, Game.Result> {
    public ResultAssert(final Game.Result result) {
        super(result, ResultAssert.class);
    }

    public ResultAssert isUnknown() {
        assertThat(actual).isEqualTo(UNKNOWN);
        return this;
    }

    public ResultAssert isWhiteWin() {
        assertThat(actual).isEqualTo(WHITE);
        return this;
    }

    public ResultAssert isBlackWin() {
        assertThat(actual).isEqualTo(BLACK);
        return this;
    }
}
