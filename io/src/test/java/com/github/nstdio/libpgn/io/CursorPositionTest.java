package com.github.nstdio.libpgn.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CursorPositionTest {
    CursorPosition cp;

    @BeforeEach
    void setUp() {
        cp = new CursorPosition();
    }

    @Test
    void defaultValues() {
        assertThat(cp.line()).isEqualTo(1);
        assertThat(cp.offset()).isEqualTo(0);
    }

    @Test
    void addOffsetShouldBeOk() {
        cp.addOffset(2);
        assertThat(cp.offset()).isEqualTo(2);
    }

    @Test
    void incrementLineShouldBeOk() {
        assertThat(cp.incrementLineAndGet()).isEqualTo(2);
        assertThat(cp.incrementLineAndGet()).isEqualTo(3);
    }

    @Test
    void whenIncrementingLineOffsetShouldReset() {
        cp.addOffset(15);
        cp.incrementLineAndGet();

        assertThat(cp.offset()).isEqualTo(0);
    }
}