package com.github.nstdio.libpgn.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayUtilsTest {
    @Test
    public void isEmpty() {
        assertThat(ArrayUtils.isEmptyOrNull(new byte[0])).isTrue();
        assertThat(ArrayUtils.isEmptyOrNull((byte[]) null)).isTrue();

        assertThat(ArrayUtils.isEmptyOrNull(new byte[5])).isFalse();
    }

    @Test
    public void isNotEmpty() {
        assertThat(ArrayUtils.isNotEmptyOrNull(new byte[0])).isFalse();
        assertThat(ArrayUtils.isNotEmptyOrNull(null)).isFalse();

        assertThat(ArrayUtils.isNotEmptyOrNull(new byte[5])).isTrue();
    }

    @Test
    public void concatByteArrays() {
        final byte[] first = new byte[]{1, 2};
        final byte[] second = new byte[]{3, 4};

        final byte[] expected = new byte[]{1, 2, 3, 4};

        final byte[] actual = ArrayUtils.concat(first, second);

        assertThat(actual).containsExactly(expected);
    }

    @Test
    public void concatByteArrays_FirstEmpty() {
        final byte[] first = new byte[0];
        final byte[] second = new byte[]{3, 4};

        final byte[] expected = new byte[]{3, 4};

        final byte[] actual = ArrayUtils.concat(first, second);

        assertThat(actual)
                .isSameAs(second)
                .containsExactly(expected);
    }

    @Test
    public void concatByteArrays_SecondEmpty() {
        final byte[] first = new byte[]{1, 2};
        final byte[] second = new byte[0];

        final byte[] expected = new byte[]{1, 2};

        final byte[] actual = ArrayUtils.concat(first, second);

        assertThat(actual)
                .isSameAs(first)
                .containsExactly(expected);
    }
}