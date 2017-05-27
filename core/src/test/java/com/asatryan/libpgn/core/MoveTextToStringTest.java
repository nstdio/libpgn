package com.asatryan.libpgn.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveTextToStringTest {

    @Test
    public void withNag() throws Exception {
        Move whiteMove = Move.builder()
                .withMove("e4")
                .withComment("White Comment")
                .withNag(new short[]{1})
                .build();

        Move blackMove = Move.builder()
                .withMove("d4")
                .withComment("Black Comment")
                .withNag(new short[]{1})
                .build();

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
