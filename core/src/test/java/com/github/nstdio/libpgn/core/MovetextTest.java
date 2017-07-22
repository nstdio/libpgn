package com.github.nstdio.libpgn.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MovetextTest {
    private Movetext movetext;
    private String moveNumber;
    private String whiteMove;
    private String whiteComment;
    private String blackMove;
    private String blackComment;

    @Before
    public void setUp() throws Exception {
        moveNumber = "1";
        whiteMove = "e4";
        whiteComment = "White Comment";
        blackMove = "e5";
        blackComment = "Black Comment";

    }

    @After
    public void tearDown() throws Exception {
        movetext = null;
    }

    @Test
    public void gettersWithNoVariation() throws Exception {
        createDefault();

        assertEquals(Integer.parseInt(moveNumber), movetext.moveNo());
        assertEquals(whiteMove, movetext.whiteMove());
        assertEquals(whiteComment, movetext.whiteComment());
        assertEquals(blackMove, movetext.blackMove());
        assertEquals(blackComment, movetext.blackComment());
    }

    private void createDefault() {
        Move move = MoveFactory.of(whiteMove, whiteComment);
        Move move1 = MoveFactory.of(blackMove, blackComment);

        movetext = new Movetext(moveNumber, move, move1);
    }

    @Test
    public void equalsSimple() throws Exception {
        Movetext m1 = MovetextFactory.white(1, "d4");
        Movetext m2 = MovetextFactory.white(1, "d4");

        assertEquals(m1, m2);

        m1 = MovetextFactory.of(1, null, "d5");
        m2 = MovetextFactory.of(1, null, "d5");

        assertEquals(m1, m2);

        m1 = MovetextFactory.white(1, "d5");
        m2 = MovetextFactory.white(2, "d5");

        assertNotEquals(m1, m2);

        m1 = MovetextFactory.white(1, "d5");
        m2 = MovetextFactory.white(1, "d4");

        assertNotEquals(m1, m2);

        m1 = MovetextFactory.white(1, "d5");
        m2 = MovetextFactory.white(2, "d4");

        assertNotEquals(m1, m2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullStrings() throws Exception {
        MovetextFactory.of(1, (String) null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMoves() throws Exception {
        MovetextFactory.of(1, (Move) null, null);
    }

    @Test
    public void factory() throws Exception {
        Movetext m = MovetextFactory.white(1, "d4");
        Movetext m2 = MovetextFactory.white(1, "d4");
        Movetext m3 = MovetextFactory.white(1, MoveFactory.of("d4"));

        assertEquals(m, m2);
        assertEquals(m, m3);
        assertEquals(m2, m3);

        assertEquals(1, m.moveNo());
        assertEquals("d4", m.whiteMove());
        assertNull(m.black());

        m = MovetextFactory.black(1, "d4");

        assertEquals(1, m.moveNo());
        assertEquals("d4", m.blackMove());
        assertNull(m.white());
    }

    @Test
    public void listFactory() throws Exception {
        List<Movetext> expected = Arrays.asList(
                MovetextFactory.of(1, "d4", "Nf6"),
                MovetextFactory.of(2, "c4", "e6"),
                MovetextFactory.of(3, "Nc3", "d5"),
                MovetextFactory.of(4, "Bg5", "Nbd7"),
                MovetextFactory.of(5, "e3", "Be7"),
                MovetextFactory.of(6, "Nf3", "O-O"),
                MovetextFactory.of(7, "Qc2", "c5")
        );

        List<Movetext> actual = MovetextFactory.moves("d4", "Nf6", "c4", "e6", "Nc3", "d5", "Bg5", "Nbd7", "e3", "Be7",
                "Nf3", "O-O", "Qc2", "c5");

        assertEquals(expected, actual);

        assertNotNull(MovetextFactory.moves());
        assertEquals(0, MovetextFactory.moves().size());

        final List<Movetext> d4 = Collections.singletonList(MovetextFactory.white(1, "d4"));

        assertEquals(d4, MovetextFactory.moves("d4"));

        expected = Arrays.asList(
                MovetextFactory.of(3, "d4", "Nf6"),
                MovetextFactory.white(4, "c4")
        );

        assertEquals(expected, MovetextFactory.moves(3, "d4", "Nf6", "c4"));

        expected = Arrays.asList(
                MovetextFactory.of(1, "d4", "Nf6"),
                MovetextFactory.of(2, "c4", "e6"),
                MovetextFactory.white(3, "Nc3")
        );

        assertEquals(expected, MovetextFactory.moves(-1, "d4", "Nf6", "c4", "e6", "Nc3"));

        assertNotNull(MovetextFactory.moves((String[]) null));

        expected = Arrays.asList(
                MovetextFactory.of(Integer.MAX_VALUE - 2, "d4", "d5"),
                MovetextFactory.white(Integer.MAX_VALUE - 1, "Nc3")
        );

        assertEquals(expected, MovetextFactory.moves(Integer.MAX_VALUE, "d4", "d5", "Nc3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNull() throws Exception {
        MovetextFactory.moves((String) null);
    }
}