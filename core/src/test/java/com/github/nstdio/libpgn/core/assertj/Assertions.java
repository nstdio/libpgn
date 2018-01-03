package com.github.nstdio.libpgn.core.assertj;

import com.github.nstdio.libpgn.core.Game;
import com.github.nstdio.libpgn.core.io.PgnInputStream;
import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import org.assertj.core.api.ThrowableTypeAssert;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Assertions {
    public static ThrowableTypeAssert<EOFException> assertThatEOFException() {
        return assertThatExceptionOfType(EOFException.class);
    }

    public static PgnLexerAssert assertThatLexer(final String input) {
        return new PgnLexerAssert(new InputStreamPgnLexer(new PgnInputStream(new ByteArrayInputStream(input.getBytes()))));
    }

    public static GameAssert assertThat(final Game game) {
        return new GameAssert(game);
    }

    public static ResultAssert assertThat(final Game.Result result) {
        return new ResultAssert(result);
    }
}
