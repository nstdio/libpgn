package com.github.nstdio.libpgn.entity.assertj;

import com.github.nstdio.libpgn.entity.Result;
import org.assertj.core.api.AbstractAssert;

import static com.github.nstdio.libpgn.entity.Result.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ResultAssert extends AbstractAssert<ResultAssert, Result> {
    public ResultAssert(final Result result) {
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
