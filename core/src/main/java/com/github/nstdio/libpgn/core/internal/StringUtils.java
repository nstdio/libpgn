package com.github.nstdio.libpgn.core.internal;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class StringUtils {
    private final static char[] GROUPS = {'A', 'B', 'C', 'D', 'E'};

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


    public static boolean isEco(final String eco) {
        if (eco == null || eco.length() != 3) {
            return false;
        }

        if (Arrays.binarySearch(GROUPS, eco.charAt(0)) == -1) {
            return false;
        }

        return Character.isDigit(eco.codePointAt(1)) && Character.isDigit(eco.codePointAt(2));
    }

    public static String nullTo(final String input, final String defaultValue) {
        return input == null ? defaultValue : input;
    }
}
