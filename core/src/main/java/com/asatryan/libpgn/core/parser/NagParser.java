package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.TreeSet;

import static com.asatryan.libpgn.core.TokenTypes.NAG;
import static com.asatryan.libpgn.core.internal.CollectionUtils.toArray;

class NagParser extends AbstractParser implements InputParser<short[], short[]> {

    NagParser(@Nonnull final PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    /**
     * Poll all {@code NAG} tokens until non {@code NAG} token occurrence.
     */
    private void pollExcluded() {
        while (lexer.lastToken() == NAG) {
            lexer.nextAlignedToken();
        }
    }

    /**
     * @param limit        Limit for return array size.
     * @param mergeWithNag In case if not null and not empty this array will be prepended to returned array.
     *
     * @return The parsed NAGs.
     */
    private short[] tryParseNag(final int limit, final short[] mergeWithNag) {
        if (lexer.lastToken() != NAG) {
            return mergeWithNag;
        }

        return parseNag0(limit, mergeWithNag);
    }

    @Nullable
    private short[] parseNag0(final int limit, final short[] mergeWith) {
        if (limit <= 0) {
            return null;
        }

        final int mergeWithLen = mergeWith == null ? 0 : mergeWith.length;

        final Set<Short> nags = toSet(mergeWith);

        final int estimatedSize = limit - mergeWithLen;
        do {
            nags.add(safeParseShort());
        } while (lexer.nextToken() == NAG && nags.size() < estimatedSize);

        pollExcluded();

        return toArray(nags);
    }

    private Set<Short> toSet(final @Nullable short[] src) {
        return src == null ? setImpl() : toSetImpl(src);
    }

    @Nonnull
    private Set<Short> setImpl() {
        return new TreeSet<>();
    }

    private Set<Short> toSetImpl(final @Nonnull short[] src) {
        if (src.length == 0) {
            return setImpl();
        }

        final Set<Short> nags = setImpl();

        for (short i : src) {
            nags.add(i);
        }

        return nags;
    }

    /**
     * Parse short from {@code tokenStream} top element value.
     * In case if exception occurred {@code 0} will be returned.
     *
     * @return The short value of the string.
     */
    private Short safeParseShort() {
        try {

            return Short.valueOf(lexer.extract().substring(1));
        } catch (NumberFormatException e) {
            return (short) 0;
        }
    }

    @Override
    public short[] parse(short[] input) {
        return tryParseNag(config.nagLimit(), input);
    }
}
