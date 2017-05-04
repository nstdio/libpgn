package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class PgnParserTest {

    private PgnParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new PgnParser();
    }

    @Test
    public void emptyTagPair() throws Exception {
        String input = "[Event \"Leipzig8990 m\"]\n" +
                "[Site \"Leipzig\"]\n" +
                "[Date \"1889.??.??\"]\n" +
                "[Round \"1\"]\n" +
                "[White \"Lasker, Emanuel\"]\n" +
                "[Black \"Mieses, Jacques\"]\n" +
                "[Result \"1-0\"]\n" +
                "[WhiteElo \"\"]\n" +
                "[BlackElo \"\"]\n" +
                "[ECO \"A84\"]\n" +
                "\n" +
                "1.d4 {Better was e4.} f5 2.c4 c5 3.dxc5 Qa5+ 4.Nc3 Qxc5 5.e4 fxe4   1-0";

        parser = new PgnParser();

        final List<TagPair> tagPairs = new ArrayList<>();

        tagPairs.add(TagPair.of("Event", "Leipzig8990 m"));
        tagPairs.add(TagPair.of("Site", "Leipzig"));
        tagPairs.add(TagPair.of("Date", "1889.??.??"));
        tagPairs.add(TagPair.of("Round", "1"));
        tagPairs.add(TagPair.of("White", "Lasker, Emanuel"));
        tagPairs.add(TagPair.of("Black", "Mieses, Jacques"));
        tagPairs.add(TagPair.of("Result", "1-0"));
        tagPairs.add(TagPair.of("WhiteElo", ""));
        tagPairs.add(TagPair.of("BlackElo", ""));
        tagPairs.add(TagPair.of("ECO", "A84"));

        final List<Game> games = parser.parse(input);
        final Game game = games.get(0);
        final List<Movetext> moves = new ArrayList<>();

        assertNotNull(game);
        assertEquals(game.tagPairSection(), tagPairs);

        moves.add(Movetext.of(1, MoveFactory.of("d4", "Better was e4."), MoveFactory.of("f5")));
        moves.add(Movetext.of(2, "c4", "c5"));
        moves.add(Movetext.of(3, "dxc5", "Qa5+"));
        moves.add(Movetext.of(4, "Nc3", "Qxc5"));
        moves.add(Movetext.of(5, "e4", "fxe4"));

        assertEquals(game.moves(), moves);
        assertEquals(game.gameResult(), Game.Result.WHITE);
    }

    @Test
    public void movetext() throws Exception {
        String input = "1.d4 {White Comment} f5 {Black Comment} 2.c4 c5 3.dxc5 Qa5+ 4.Nc3 Qxc5 *";

        parser = new PgnParser();
        final List<Game> games = parser.parse(input);

        List<Movetext> moves = new ArrayList<>();

        moves.add(new Movetext(1,
                MoveFactory.of("d4", "White Comment"),
                MoveFactory.of("f5", "Black Comment"))
        );
        moves.add(Movetext.of(2, "c4", "c5"));
        moves.add(Movetext.of(3, "dxc5", "Qa5+"));
        moves.add(Movetext.of(4, "Nc3", "Qxc5"));

        assertThat(games.get(0).moves(), is(moves));
    }

    @Test
    public void moveWithVariation() throws Exception {
        String[] input = {
                "1.d4(1.d5 d6)1...f5{Black Comment}*",
                "1.d4 (1. d5 d6) 1... f5 {Black Comment}*",
                "1.d4(1.d5 d6) 1... f5{Black Comment} *",
                "1.d4(1.d5 d6)1... f5 {Black Comment} *",
        };

        List<Movetext> moves = new ArrayList<>();

        final Movetext movetext = Movetext.of(1,
                MoveFactory.of("d4", singletonList(Movetext.of(1, "d5", "d6"))),
                MoveFactory.of("f5", "Black Comment")
        );

        moves.add(movetext);

        assertMovesEquals(input, moves);

    }

    @Test
    public void blackVariations() throws Exception {
        String[] inputs = {
                "1. e4 (1. d4 Nf6) c5 (1... e5 2. Nf3) 2. Nf3 d6 *",
                "1.e4(1.d4 Nf6)c5(1...e5 2.Nf3)2.Nf3 d6*"
        };

        List<Movetext> moves = new ArrayList<>();

        final Movetext movetext_1 = Movetext.of(1,
                MoveFactory.of("e4", singletonList(Movetext.of(1, "d4", "Nf6"))),
                MoveFactory.of("c5", Arrays.asList(
                        Movetext.of(1, null, "e5"),
                        Movetext.of(2, "Nf3")
                ))
        );
        final Movetext movetext_2 = Movetext.of(2, "Nf3", "d6");

        moves.add(movetext_1);
        moves.add(movetext_2);

        assertMovesEquals(inputs, moves);

    }

    @Test
    public void nestedSimpleVariation() throws Exception {
        String[] inputs = {
                "1. e4 (1. d4 (1. d3 (1. c5 c6)) 1... d5 (1... d6)) 1... c5 2. Nf3 c6 *",
        };


        final List<Movetext> d3Var = singletonList(Movetext.of(1, "c5", "c6"));
        final List<Movetext> d5Var = singletonList(Movetext.black(1, "d6"));
        final List<Movetext> d4Var = singletonList(Movetext.of(1, MoveFactory.of("d3", d3Var)));
        final List<Movetext> e4Var = singletonList(
                Movetext.of(1, MoveFactory.of("d4", d4Var), MoveFactory.of("d5", d5Var))
        );

        final Movetext e4c5 = Movetext.of(1, MoveFactory.of("e4", e4Var), MoveFactory.of("c5"));

        List<Movetext> moves = new ArrayList<>();
        moves.add(e4c5);
        moves.add(Movetext.of(2, "Nf3", "c6"));

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void variationWithComment() throws Exception {
        String[] inputs = {
                "1.e4 (1. d4 (1. d3 {3d .1}) {Comment})*",
                "1.e4 (1. d4 {Comment} (1. d3 {3d .1}))*",
                "1.e4(1.d4{Comment}(1.d3{3d .1}))*",
                "  1  . e4 (    1.d4  {Comment}  (  1.  d3  {3d .1}  )  )*",
        };

        final List<Movetext> d4Var = singletonList(Movetext.of(1, MoveFactory.of("d3", "3d .1")));
        final List<Movetext> e4Var = singletonList(
                Movetext.of(1, MoveFactory.of("d4", "Comment", d4Var))
        );

        final Movetext e4 = Movetext.of(1, MoveFactory.of("e4", e4Var));

        assertMovesEquals(inputs, singletonList(e4));
    }

    @Test
    public void nestedVariations() throws Exception {
        String[] inputs = {
                "1. e4 (1. d4 (1. d3 {3e Nested comment whit} (1. c5 {3 Nested comment white} c6 {3 Nested comment black}) 1... d5)) 1... c5 (1... e5 2. Nf3) 2. Nf3 *"
        };

        List<Movetext> moves = new ArrayList<>();
        List<Movetext> d3Variation = singletonList(
                Movetext.of(1, MoveFactory.of("c5", "3 Nested comment white"), MoveFactory.of("c6", "3 Nested comment black"))
        );
        final List<Movetext> d4Variation = singletonList(
                Movetext.of(1,
                        MoveFactory.of("d3", "3e Nested comment whit", d3Variation),
                        MoveFactory.of("d5")
                )
        );
        final List<Movetext> e4Variation = singletonList(
                Movetext.of(1, MoveFactory.of("d4", d4Variation))
        );

        final List<Movetext> c5Variation = Arrays.asList(
                Movetext.black(1, "e5"),
                Movetext.white(2, "Nf3")
        );

        moves.add(Movetext.of(1, MoveFactory.of("e4", e4Variation), MoveFactory.of("c5", c5Variation)));
        moves.add(Movetext.white(2, "Nf3"));

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void moveNumber() throws Exception {
        String[] inputs = {
                "100. e4 d5   101  . Nc3 e5 *",
                "100 . e4 d5 101 . Nc3 e5 *",
                "  100   .   e4   d5    101  .   Nc3    e5    *  "
        };

        final List<Movetext> moves = Movetext.moves(100, "e4", "d5", "Nc3", "e5");

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void nag() throws Exception {
        String[] inputs = {
                "1. e4 $1$2$3 d5 *",
                "1. e4 $3$1$2 d5 *",
                "1. e4 $1 $3$2 d5 *",
                "1. e4 $3$1$2$2 d5 *",
                "1. e4 $3 $1 $2 $2 d5 *",
        };

        final List<Movetext> moves = singletonList(
                Movetext.of(1, MoveFactory.of("e4", new short[]{1, 2, 3}), MoveFactory.of("d5"))
        );

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void sequentialComments() throws Exception {
        final String[] inputs = {
                "1. e4 {Comment} {Comment} {Comment} e5 *",
                "1. e4 {Comment}{Comment}{Comment} e5 *",
                "1. e4{Comment}{Comment}{Comment} e5 *",
                "1.e4{Comment}{Comment}{Comment} e5 *",
                "1.e4{Comment}{Comment}{Comment}e5*",
        };

        final List<Movetext> moves = singletonList(
                Movetext.of(1, MoveFactory.of("e4", "CommentCommentComment"), MoveFactory.of("e5"))
        );

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void predefinedTagPairCache() throws Exception {
        final TagPair tagPair = TagPair.of("White", "Kasparov, Garry");
        final TagPair tagPair2 = TagPair.of("Black", "Karpov, Anatoly");

        final Set<TagPair> predefinedCache = new HashSet<>();
        predefinedCache.add(tagPair);
        predefinedCache.add(tagPair2);

        Configuration config = Configuration.defaultBuilder()
                .cacheTagPair(true)
                .predefinedCache(predefinedCache)
                .build();

        parser = new PgnParser(config);

        String input = "[White \"Kasparov, Garry\"]\n" +
                "[Black \"Karpov, Anatoly\"]\n" +
                "1. e4 *";

        final List<Game> parse = parser.parse(input);

        final List<TagPair> tagPairs = parse.get(0).tagPairSection();

        assertSame(tagPair, tagPairs.get(0));
        assertSame(tagPair2, tagPairs.get(1));
    }

    private void assertMovesEquals(String[] inputs, List<Movetext> moves) {
        for (String pgn : inputs) {
            List<Game> games = parser.parse(pgn);
            final Game game = games.get(0);

            assertEquals(moves, game.moves());
            assertEquals(Game.Result.UNKNOWN, game.gameResult());
        }
    }
}