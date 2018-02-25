package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveAssert extends AbstractAssert<MoveAssert, Move> {
    public MoveAssert(final Move move) {
        super(move, MoveAssert.class);
    }

    public MoveAssert moveIsEqualTo(final String expected) {
        return moveIsEqualTo(expected.getBytes());
    }

    public MoveAssert commentIsNotNull() {
        assertThat(actual.comment()).isNotNull();

        return this;
    }

    public MoveAssert commentIsEqualTo(final String expected) {
        commentIsNotNull();
        return commentIsEqualTo(expected.getBytes());
    }

    public MoveAssert commentIsEqualTo(final byte[] expected) {
        assertThat(expected).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual.comment()).containsExactly(expected);

        return this;
    }

    public MoveAssert commentIsSameAs(final byte[] expected) {
        assertThat(actual).isNotNull();
        assertThat(actual.comment()).isSameAs(expected);

        return this;
    }

    public MoveAssert nagIsSameAs(final short[] expected) {
        assertThat(actual).isNotNull();
        assertThat(actual.nag()).isSameAs(expected);

        return this;
    }

    public MoveAssert nagIsNotNull() {
        assertThat(actual).isNotNull();
        assertThat(actual.nag()).isNotNull();

        return this;
    }

    public MoveAssert moveIsEqualTo(final byte[] expected) {
        assertThat(expected).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual.move()).containsExactly(expected);

        return this;
    }

    public MoveAssert variationsIsEqualTo(final List<MoveText> expected) {
        assertThat(actual.variations()).containsAll(expected);

        return this;
    }

    public MoveAssert variationsIsEmpty() {
        assertThat(actual.variations()).isEmpty();
        return this;
    }

    public MoveAssert variationsIsNotNull() {
        assertThat(actual.variations()).isNotNull();
        return this;
    }
}
