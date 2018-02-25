package com.github.nstdio.libpgn.core.pgn;

import com.github.nstdio.libpgn.core.internal.ArrayUtils;
import org.junit.Test;

import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class MoveTest {
    @Test
    public void equality() {
        final String moveStr = "e4";

        final Move move = Move.of(moveStr.getBytes());

        org.assertj.core.api.Assertions.assertThat(move)
                .isEqualTo(Move.of(moveStr))
                .isEqualTo(Move.of(moveStr.getBytes(), null, null, null))
                .isEqualTo(Move.of(moveStr.getBytes(), null));
    }

    @Test
    public void mutableAndImmutableEquality() {
        final String moveStr = "d4";
        final String commentStr = "Comment";

        final Move move = Move.of(moveStr.getBytes());
        final Move immutableMove = Move.ofImmutable(moveStr.getBytes());

        assertThat(immutableMove).isEqualTo(move);

        final Move move1 = Move.of(moveStr.getBytes(), commentStr.getBytes(), null, null);
        final Move immutableMove1 = Move.ofImmutable(moveStr.getBytes(), commentStr.getBytes(), null, null);

        assertThat(move1).isEqualTo(immutableMove1);
    }

    @Test
    public void defaultValues() {
        final String moveStr = "e4";

        final Move move = Move.of(moveStr.getBytes());

        assertThat(move)
                .moveIsEqualTo(moveStr)
                .commentIsSameAs(ArrayUtils.EMPTY_BYTE_ARRAY)
                .nagIsSameAs(ArrayUtils.EMPTY_SHORT_ARRAY)
                .variationsIsEmpty();
    }

    @Test
    public void mutabilityViaExternalChanges() {
        final String moveStr = "Nxf5";
        final String expected = "Rxf5";
        final byte[] bytes = moveStr.getBytes();

        final Move move = Move.of(bytes);

        bytes[0] = (byte) expected.codePointAt(0);

        assertThat(move).moveIsEqualTo(expected);
    }

    @Test
    public void mutabilityUsingGetters() {
        final String moveStr = "Nxf5";
        final String expected = "Rxf5";

        final Move move = Move.of(moveStr.getBytes());

        final byte[] moveBytes = move.move();
        moveBytes[0] = (byte) expected.codePointAt(0);

        assertThat(move).moveIsEqualTo(expected);
    }

    @Test
    public void emptyMove() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Move.of((byte[]) null))
                .withMessage("move size must be greater then 2");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Move.ofImmutable((byte[]) null))
                .withMessage("move size must be greater then 2");

        assertThatNullPointerException()
                .isThrownBy(() -> Move.of((String) null))
                .withMessage("move cannot be null");

        assertThatNullPointerException()
                .isThrownBy(() -> Move.ofImmutable((String) null))
                .withMessage("move cannot be null");

    }

    @Test
    public void nullToEmpty() {
        final byte[] bytes = "d4".getBytes();
        final Move mutableMove = Move.of(bytes, null, null, null);

        assertThat(mutableMove)
                .moveIsEqualTo(bytes)
                .commentIsNotNull()
                .nagIsNotNull()
                .variationsIsNotNull();
    }
}