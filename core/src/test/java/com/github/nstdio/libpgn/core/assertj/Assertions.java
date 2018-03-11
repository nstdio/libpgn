package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.fen.FENs;
import com.github.nstdio.libpgn.core.io.PgnInputStreamFactory;
import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import com.github.nstdio.libpgn.core.pgn.Move;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.assertj.core.api.ThrowableTypeAssert;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class Assertions {
    public static ThrowableTypeAssert<EOFException> assertThatEOFException() {
        return assertThatExceptionOfType(EOFException.class);
    }

    public static PgnLexerAssert assertThatLexer(final String input) {
        return assertThatLexer(new InputStreamPgnLexer(PgnInputStreamFactory.of(new ByteArrayInputStream(input.getBytes()))));
    }

    public static PgnLexerAssert assertThatLexer(final InputStreamPgnLexer lexer) {
        return new PgnLexerAssert(lexer);
    }

    public static GameAssert assertThat(final Game game) {
        return new GameAssert(game);
    }

    public static ResultAssert assertThat(final Game.Result result) {
        return new ResultAssert(result);
    }

    public static MoveAssert assertThat(final Move move) {
        return new MoveAssert(move);
    }

    public static ThrowableAssertAlternative<IllegalArgumentException> assertThatUnexpectedCharacter(final String fen, final char c, final int index) {
        return assertThatIllegalArgumentException()
                .isThrownBy(() -> FENs.of(fen))
                .withMessage("Unexpected character %s[%c] at index %d", Character.getName(c), c, index);
    }
}
