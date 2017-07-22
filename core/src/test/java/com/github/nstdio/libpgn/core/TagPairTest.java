package com.github.nstdio.libpgn.core;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * class TagPairTest Created by Asatryan on 28.02.2017.
 */
public class TagPairTest {

    @Test
    public void tagPairToString() throws Exception {
        final String exp1 = "[White \"Kasparov, Garry\"]";
        final TagPair tp1 = new TagPair("White", "Kasparov, Garry");

        assertThat(exp1).isEqualTo(tp1.toString());

        final String exp2 = "[White \"\"]";
        final TagPair tp2 = new TagPair("White", null);

        assertThat(exp2).isEqualTo(tp2.toString());
    }

    @Test(expected = NullPointerException.class)
    public void tagNameCannotBeNull() throws Exception {
        //noinspection ConstantConditions
        new TagPair(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tagNameCannotBeEmpty() throws Exception {
        new TagPair("", "");
    }

    @Test
    public void getters() throws Exception {
        final TagPair tp1 = new TagPair("White", "Kasparov, Garry");
        assertEquals("White", tp1.getTag());
        assertEquals("Kasparov, Garry", tp1.getValue());

        final TagPair tp2 = new TagPair("White", null);
        assertEquals("White", tp2.getTag());
        assertEquals("", tp2.getValue()); // Ensure that null replaced by empty string
    }

    @Test
    public void tagPairEquals() throws Exception {
        final TagPair tp1 = new TagPair("White", "Kasparov, Garry");
        final TagPair tp2 = new TagPair("White", "Kasparov, Garry");
        final TagPair tp3 = new TagPair("Black", "Kasparov, Garry");
        final Object tp4 = new Object();

        assertEquals(tp1, tp2);
        assertFalse(tp1.equals(tp3));
        assertFalse(tp3.equals(tp4));
        //noinspection ObjectEqualsNull
        assertFalse(tp3.equals(null));
    }

    @Test
    public void staticGenerationMethods() {
        final TagPair actual = TagPair.ofWhite(null, null);
        assertThat(actual.getTag()).isEqualTo("White");
        assertThat(actual.getValue()).isEqualTo("");

        final TagPair actual1 = TagPair.ofWhite("Kasparov", null);
        assertThat(actual1.getTag()).isEqualTo("White");
        assertThat(actual1.getValue()).isEqualTo("Kasparov");

        final TagPair actual2 = TagPair.ofWhite(null, "Garry");
        assertThat(actual2.getTag()).isEqualTo("White");
        assertThat(actual2.getValue()).isEqualTo("Garry");

        final TagPair actual3 = TagPair.ofWhite(" ", "Garry");
        assertThat(actual3.getTag()).isEqualTo("White");
        assertThat(actual3.getValue()).isEqualTo("Garry");

        final TagPair actual4 = TagPair.ofWhite("", "");
        assertThat(actual4.getTag()).isEqualTo("White");
        assertThat(actual4.getValue()).isEqualTo("");
    }

    @Test
    public void tagPairInSet() throws Exception {
        final TagPair white1 = TagPair.of("White", "Kasparov, Garry");
        final TagPair white2 = TagPair.of("White", "Kasparov, Garry");

        final Set<TagPair> tagPairs = new HashSet<>();

        tagPairs.add(white1);
        tagPairs.add(white2);

        assertFalse(tagPairs.size() == 2);
        assertTrue(tagPairs.size() == 1);
    }
}