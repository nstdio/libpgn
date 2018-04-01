package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.common.ArrayUtils;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;
import com.github.nstdio.libpgn.entity.Result;
import com.github.nstdio.libpgn.entity.TagPair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


public class PgnParserTest {
    private PgnParser parser;

    @Test
    @Disabled
    public void emptyTagPair() {
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

        parser = new PgnParser(createLexer(input));

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

        final List<Game> games = parser.parse();
        final Game game = games.get(0);
        final List<MoveText> moves = new ArrayList<>();

        assertThat(game).isNotNull();
        assertThat(game.tagPairSection()).isEqualTo(tagPairs);

        moves.add(MoveText.of(1, Move.of("d4", "Better was e4."), Move.of("f5")));
        moves.add(MoveText.of(2, "c4", "c5"));
        moves.add(MoveText.of(3, "dxc5", "Qa5+"));
        moves.add(MoveText.of(4, "Nc3", "Qxc5"));
        moves.add(MoveText.of(5, "e4", "fxe4"));

        assertThat(game.moves()).isEqualTo(moves);
        assertThat(game.gameResult()).isEqualTo(Result.WHITE);
    }

    private InputStreamPgnLexer createLexer(final String input) {
        return InputStreamPgnLexer.of(input.getBytes());
    }

    @Test
    public void movetext() {
        String input = "1.d4 {White Comment} f5 {Black Comment} 2.c4 c5 3.dxc5 Qa5+ 4.Nc3 Qxc5 *";

        final List<Game> games = new PgnParser(createLexer(input)).parse();

        List<MoveText> moves = new ArrayList<>();

        moves.add(MoveText.of(1,
                Move.of("d4", "White Comment"),
                Move.of("f5", "Black Comment"))
        );
        moves.add(MoveText.of(2, "c4", "c5"));
        moves.add(MoveText.of(3, "dxc5", "Qa5+"));
        moves.add(MoveText.of(4, "Nc3", "Qxc5"));

        assertThat(games.get(0).moves()).isEqualTo(moves);
    }

    @Test
    public void moveWithVariation() {
        String[] input = {
                "1.d4(1.d5 d6)1...f5{Black Comment}*",
                "1.d4 (1. d5 d6) 1... f5 {Black Comment}*",
                "1.d4(1.d5 d6) 1... f5{Black Comment} *",
                "1.d4(1.d5 d6)1... f5 {Black Comment} *",
        };

        List<MoveText> moves = new ArrayList<>();

        final MoveText movetext = MoveText.of(1,
                Move.of("d4", singletonList(MoveText.of(1, "d5", "d6"))),
                Move.of("f5", "Black Comment")
        );

        moves.add(movetext);

        assertMovesEquals(input, moves);
    }

    @Test
    public void blackVariations() {
        String[] inputs = {
                "1. e4 (1. d4 Nf6) c5 (1... e5 2. Nf3) 2. Nf3 d6 *",
                "1.e4(1.d4 Nf6)c5(1...e5 2.Nf3)2.Nf3 d6*"
        };

        List<MoveText> moves = new ArrayList<>();

        final MoveText movetext_1 = MoveText.of(1,
                Move.of("e4", singletonList(MoveText.of(1, "d4", "Nf6"))),
                Move.of("c5", Arrays.asList(
                        MoveText.ofBlack(1, "e5"),
                        MoveText.ofWhite(2, "Nf3")
                ))
        );
        final MoveText movetext_2 = MoveText.of(2, "Nf3", "d6");

        moves.add(movetext_1);
        moves.add(movetext_2);

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void nestedSimpleVariation() {
        String[] inputs = {
                "1. e4 (1. d4 (1. d3 (1. c5 c6)) 1... d5 (1... d6)) 1... c5 2. Nf3 c6 *",
        };


        final List<MoveText> d3Var = singletonList(MoveText.of(1, "c5", "c6"));
        final List<MoveText> d5Var = singletonList(MoveText.ofBlack(1, "d6"));
        final List<MoveText> d4Var = singletonList(MoveText.ofWhite(1, Move.of("d3", d3Var)));
        final List<MoveText> e4Var = singletonList(
                MoveText.of(1, Move.of("d4", d4Var), Move.of("d5", d5Var))
        );

        final MoveText e4c5 = MoveText.of(1, Move.of("e4", e4Var), Move.of("c5"));

        List<MoveText> moves = new ArrayList<>();
        moves.add(e4c5);
        moves.add(MoveText.of(2, "Nf3", "c6"));

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void variationWithComment() {
        String[] inputs = {
                "1.e4 (1. d4 (1. d3 {3d .1}) {Comment})*",
                "1.e4 (1. d4 {Comment} (1. d3 {3d .1}))*",
                "1.e4(1.d4{Comment}(1.d3{3d .1}))*",
                "  1  . e4 (    1.d4  {Comment}  (  1.  d3  {3d .1}  )  )*",
        };

        final List<MoveText> d4Var = singletonList(MoveText.ofWhite(1, Move.of("d3", "3d .1")));
        final List<MoveText> e4Var = singletonList(
                MoveText.ofWhite(1, Move.of("d4", "Comment", d4Var))
        );

        final MoveText e4 = MoveText.ofWhite(1, Move.of("e4", e4Var));

        assertMovesEquals(inputs, singletonList(e4));
    }

    @Test
    public void nestedVariations() {
        String[] inputs = {
                "1. e4 (1. d4 (1. d3 {3e Nested comment whit} (1. c5 {3 Nested comment white} c6 {3 Nested comment black}) 1... d5)) 1... c5 (1... e5 2. Nf3) 2. Nf3 *"
        };

        List<MoveText> moves = new ArrayList<>();
        List<MoveText> d3Variation = singletonList(
                MoveText.of(1, Move.of("c5", "3 Nested comment white"), Move.of("c6", "3 Nested comment black"))
        );
        final List<MoveText> d4Variation = singletonList(
                MoveText.of(1,
                        Move.of("d3", "3e Nested comment whit", d3Variation),
                        Move.of("d5")
                )
        );
        final List<MoveText> e4Variation = singletonList(
                MoveText.ofWhite(1, Move.of("d4", d4Variation))
        );

        final List<MoveText> c5Variation = Arrays.asList(
                MoveText.ofBlack(1, "e5"),
                MoveText.ofWhite(2, "Nf3")
        );

        moves.add(MoveText.of(1, Move.of("e4", e4Variation), Move.of("c5", c5Variation)));
        moves.add(MoveText.ofWhite(2, "Nf3"));

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void moveNumber() {
        String[] inputs = {
                "100. e4 d5   101  . Nc3 e5 *",
                "100 . e4 d5 101 . Nc3 e5 *",
                "  100   .   e4   d5    101  .   Nc3    e5    *  "
        };

        final List<MoveText> moves = MoveText.moves(100, "e4", "d5", "Nc3", "e5");

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void nag() {
        String[] inputs = {
                "1. e4 $1$2$3 d5 *",
                "1. e4 $3$1$2 d5 *",
                "1. e4 $1 $3$2 d5 *",
                "1. e4 $3$1$2$2 d5 *",
                "1. e4 $3 $1 $2 $2 d5 *",
        };

        final List<MoveText> moves = singletonList(
                MoveText.of(1, Move.of("e4", new short[]{1, 2, 3}), Move.of("d5"))
        );

        assertMovesEquals(inputs, moves);
    }

    @Test
    public void sequentialComments() {
        final String[] inputs = {
                "1. e4 {Comment} {Comment} {Comment} e5 *",
                "1. e4 {Comment}{Comment}{Comment} e5 *",
                "1. e4{Comment}{Comment}{Comment} e5 *",
                "1.e4{Comment}{Comment}{Comment} e5 *",
                "1.e4{Comment}{Comment}{Comment}e5*",
        };

        final List<MoveText> moves = singletonList(
                MoveText.of(1, Move.of("e4", "CommentCommentComment"), Move.of("e5"))
        );

        assertMovesEquals(inputs, moves);
    }

    @Test
    @Disabled
    public void predefinedTagPairCache() {
        final TagPair tagPair = TagPair.of("White", "Kasparov, Garry");
        final TagPair tagPair2 = TagPair.of("Black", "Karpov, Anatoly");

        final Set<TagPair> predefinedCache = new HashSet<>();
        predefinedCache.add(tagPair);
        predefinedCache.add(tagPair2);

        Configuration config = Configuration.defaultBuilder()
                .cacheTagPair(true)
                .predefinedCache(predefinedCache)
                .build();

        String input = "[White \"Kasparov, Garry\"]\n" +
                "[Black \"Karpov, Anatoly\"]\n" +
                "1. e4 *";

        final List<Game> parse = new PgnParser(createLexer(input), config).parse();

        final List<TagPair> tagPairs = parse.get(0).tagPairSection();

        assertThat(tagPair).isSameAs(tagPairs.get(0));
        assertThat(tagPair2).isSameAs(tagPairs.get(1));
    }

    @Test
    public void skipComment() {
        final String[] inputs = {
                "1. e4 {Comment} {Other Comment} d5 {Comment} *",
        };

        final Configuration config = Configuration.defaultBuilder()
                .skipComment(true)
                .build();

        for (String input : inputs) {
            final List<Game> games = new PgnParser(createLexer(input), config).parse();
            for (MoveText movetext : games.get(0).moves()) {
                assertThat(movetext.white().map(Move::comment))
                        .hasValue(ArrayUtils.EMPTY_BYTE_ARRAY);

                assertThat(movetext.black().map(Move::comment))
                        .hasValue(ArrayUtils.EMPTY_BYTE_ARRAY);
            }
        }
    }

    @Test
    public void skipTagPair() {
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
                "1.d4 *";

        final Configuration config = Configuration.defaultBuilder()
                .skipTagPairSection(true)
                .build();

        List<Game> games = new PgnParser(createLexer(input), config).parse();

        final Game firstGame = games.get(0);
        assertThat(firstGame.tagPairSection()).isNull();

        assertThat(firstGame.moves().get(0).white().map(Move::move).orElse(null)).containsExactly("d4".getBytes());
        assertThat(firstGame.gameResult()).isEqualTo(Result.UNKNOWN);
    }

    private void assertMovesEquals(String[] inputs, List<MoveText> moves) {
        for (String pgn : inputs) {
            List<Game> games = new PgnParser(createLexer(pgn)).parse();
            final Game game = games.get(0);

            assertThat(moves).isEqualTo(game.moves());
            assertThat(Result.UNKNOWN).isEqualTo(game.gameResult());
        }
    }
}