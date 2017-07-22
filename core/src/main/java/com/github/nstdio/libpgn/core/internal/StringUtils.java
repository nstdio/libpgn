package com.github.nstdio.libpgn.core.internal;

import javax.annotation.Nullable;

public final class StringUtils {
    private StringUtils() {
    }

    @Nullable
    public static String emptyToNull(final String input) {
        if (input != null && input.trim().length() == 0) {
            return null;
        }

        if ("".equals(input)) {
            return null;
        }

        return input;
    }


}
