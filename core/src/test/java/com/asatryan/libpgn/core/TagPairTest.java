package com.asatryan.libpgn.core;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * class TagPairTest
 * Created by Asatryan on 28.02.2017.
 */
public class TagPairTest {

    @Test
    public void tagPairToString() throws Exception {
        final String exp1 = "[White \"Kasparov, Garry\"]";
        final TagPair tp1 = new TagPair("White", "Kasparov, Garry");

        assertEquals(exp1, tp1.toString());

        final String exp2 = "[White \"\"]";
        final TagPair tp2 = new TagPair("White", null);
        assertEquals(exp2, tp2.toString());
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