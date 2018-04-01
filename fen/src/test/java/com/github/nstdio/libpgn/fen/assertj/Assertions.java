package com.github.nstdio.libpgn.fen.assertj;

import com.github.nstdio.libpgn.fen.FENs;
import org.assertj.core.api.ThrowableAssertAlternative;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class Assertions {
    public static ThrowableAssertAlternative<IllegalArgumentException> assertThatUnexpectedCharacter(final String fen, final char c, final int index) {
        return assertThatIllegalArgumentException()
                .isThrownBy(() -> FENs.of(fen))
                .withMessage("Unexpected character %s[%c] at index %d", Character.getName(c), c, index);
    }
}
