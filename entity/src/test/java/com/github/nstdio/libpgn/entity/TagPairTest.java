package com.github.nstdio.libpgn.entity;

import com.github.nstdio.libpgn.entity.TagPair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class TagPairTest {

    @Test
    public void creatingWithNulls() {
        assertThatNullPointerException()
                .isThrownBy(() -> TagPair.of(null, new byte[1]))
                .withMessage("tag");

        assertThatNullPointerException()
                .isThrownBy(() -> TagPair.of(new byte[1], null))
                .withMessage("value");
    }

    @Test
    public void getter() {
        final byte[] tag = "White".getBytes();
        final byte[] value = "Value".getBytes();

        final TagPair tagPair = TagPair.of(tag, value);

        assertThat(tagPair.getTag()).isSameAs(tag);
        assertThat(tagPair.getValue()).isSameAs(value);
    }

    @Test
    public void equality() {
        final byte[] tag = "White".getBytes();
        final byte[] value = "Value".getBytes();

        final TagPair tagPair = TagPair.of(tag, value);

        assertThat(TagPair.of(tag, value)).isEqualTo(tagPair);
        assertThat(TagPair.of(Arrays.copyOf(tag, tag.length), Arrays.copyOf(value, value.length))).isEqualTo(tagPair);
    }
}