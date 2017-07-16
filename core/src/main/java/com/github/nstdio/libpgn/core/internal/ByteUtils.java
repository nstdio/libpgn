package com.github.nstdio.libpgn.core.internal;

public final class ByteUtils {
    public static final int NOT_FOUND = -1;
    private static final byte COMMENT_BEGIN = '{';
    private static final byte VARIATION_BEGIN = '(';
    private static final byte VARIATION_END = ')';
    private static final byte NAG = '$';
    private static final byte ROL_COMMENT = ';';
    private static final byte GAMETERM = '*';

    private ByteUtils() {
    }

    public static int whitespaceOrChar(byte[] input, int from, char c1, char c2, char c3, char c4, char c5) {
        for (int i = from, n = input.length; i < n; i++) {
            final byte ch = input[i];
            if (Character.isWhitespace(ch) || ch == c1 || ch == c2 || ch == c3 || ch == c4 || ch == c5)
                return i;
        }

        return NOT_FOUND;
    }

    public static int whitespaceOrChar(byte[] input, int from, char c1, char c2) {
        return whitespaceOrChar(input, from, c1, c2, '\0');
    }

    public static int whitespaceOrChar(byte[] input, int from, char c1, char c2, char c3) {
        return whitespaceOrChar(input, from, c1, c2, c3, '\0');
    }

    public static int newLine(final byte[] input, int from) {
        for (int i = from, n = input.length; i < n; i++) {
            final byte ch = input[i];
            if (ch == '\n' || ch == '\r')
                return i;
        }

        return input.length - 1;
    }

    public static int lookBackForNewLine(byte[] input, int from) {
        for (int i = from; i > 1; --i) {
            if (input[i] == '\n') {
                return i;
            }
        }

        return 0;
    }

    public static String charOrName(String input, int atIndex) {
        String charName = null;
        char invalidChar = input.charAt(atIndex);
        if (!isPrintableChar(invalidChar)) {
            charName = Character.getName(input.codePointAt(atIndex));
        }

        if (charName == null) {
            return new String(new char[]{invalidChar});
        }

        return charName;
    }

    public static String charOrName(byte[] input, int atIndex) {
        return charOrName(new String(input), atIndex);
    }

    private static boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != 0xFFFF &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    public static int unescapedChar(byte[] input, int from, char ch) {
        for (; from < input.length; from++) {
            final byte c = input[from];
            if (c == ch && input[from - 1] != '\\')
                return from;
        }
        return NOT_FOUND;
    }

    public static boolean isLetter(final byte ch) {
        return Character.isLetter(ch);
    }

    public static int whitespaceOrChar(byte[] data, int from, char c1, char c2, char c3, char c4) {
        return whitespaceOrChar(data, from, c1, c2, c3, c4, '\0');
    }

    public static boolean isDefined(final byte b) {
        return Character.isDefined((int) b);
    }

    public static int moveEnd(byte[] data, int from) {
        for (int i = from, n = data.length; i < n; i++) {
            final byte ch = data[i];
            if (Character.isWhitespace(ch) || ch == COMMENT_BEGIN
                    || ch == VARIATION_BEGIN
                    || ch == VARIATION_END
                    || ch == NAG
                    || ch == ROL_COMMENT
                    || ch == GAMETERM)
                return i;
        }

        return NOT_FOUND;
    }
}
