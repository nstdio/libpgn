package com.github.nstdio.libpgn.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveTextToStringTest {

    @Test
    public void withNag() throws Exception {
        Move whiteMove = MoveFactory.of("e4", "White Comment", new short[]{1});

        Move blackMove = MoveFactory.of("d4", "Black Comment", new short[]{1});

        Movetext movetext = new Movetext(1, whiteMove, blackMove);

        String expected = "1. e4! {White Comment} d4! {Black Comment}";

        assertEquals(expected, movetext.toPgnString());
    }

    @Test
    public void blackMoveOnly() {
        Movetext movetext = new Movetext(24, null, MoveFactory.of("Nf6"));

        String expected = "24... Nf6";

        assertEquals(expected, movetext.toPgnString());
    }
}
