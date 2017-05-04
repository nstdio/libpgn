package com.asatryan.libpgn.core.internal;

public final class CharUtils {
    public static final int NOT_FOUND = -1;
    private static final char COMMENT_BEGIN = '{';
    private static final char VARIATION_BEGIN = '(';
    private static final char VARIATION_END = ')';
    private static final char NAG = '$';
    private static final char ROL_COMMENT = ';';
    private static final char GAMETERM = '*';

    public static int whitespaceOrChar(char[] input, int from, char c1, char c2, char c3, char c4, char c5) {
        for (int i = from, n = input.length; i < n; i++) {
            final char ch = input[i];
            if (Character.isWhitespace(ch) || ch == c1 || ch == c2 || ch == c3 || ch == c4 || ch == c5)
                return i;
        }

        return NOT_FOUND;
    }

    public static int whitespaceOrChar(char[] input, int from, char c1, char c2) {
        return whitespaceOrChar(input, from, c1, c2, '\0');
    }

    public static int whitespaceOrChar(char[] input, int from, char c1, char c2, char c3) {
        return whitespaceOrChar(input, from, c1, c2, c3, '\0');
    }

    public static int newLine(char[] input, int from) {
        for (int i = from, n = input.length; i < n; i++) {
            final char ch = input[i];
            if (ch == '\n' || ch == '\r')
                return i;
        }

        return input.length - 1;
    }

    public static int lookBackForNewLine(char[] input, int from) {
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

    public static String charOrName(char[] input, int atIndex) {
        return charOrName(new String(input), atIndex);
    }

    private static boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != 0xFFFF &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    public static int unescapedChar(char[] input, int from, char ch) {
        for (; from < input.length; from++) {
            final char c = input[from];
            if (c == ch && input[from - 1] != '\\')
                return from;
        }
        return NOT_FOUND;
    }

    public static boolean isDigit(final char ch) {
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }

    public static boolean isLetter(final char ch) {
        return Character.isLetter(ch);
    }

    public static int whitespaceOrChar(char[] data, int from, char c1, char c2, char c3, char c4) {
        return whitespaceOrChar(data, from, c1, c2, c3, c4, '\0');
    }

    public static boolean isDefined(char ch) {
        return Character.isDefined((int) ch);
    }

    public static int moveEnd(char[] data, int from) {
        for (int i = from, n = data.length; i < n; i++) {
            final char ch = data[i];
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
