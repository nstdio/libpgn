package com.github.nstdio.libpgn.core.internal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @Test
    public void simple() {
        assertThat(StringUtils.emptyToNull("")).isNull();
        assertThat(StringUtils.emptyToNull("    ")).isNull();
        assertThat(StringUtils.emptyToNull("\n\t\r \0")).isNull();
        assertThat(StringUtils.emptyToNull("A \n\t\r \0")).isNotNull();
    }
}