package com.github.nstdio.libpgn.core.internal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "    ", "\n\t\r \0"})
    public void empty(final String isEmpty) {
        assertThat(StringUtils.emptyToNull(isEmpty))
                .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"A\n\t\r \0", "a", "\na"})
    public void notEmpty(final String notEmpty) {
        assertThat(StringUtils.emptyToNull(notEmpty))
                .isSameAs(notEmpty);
    }
}