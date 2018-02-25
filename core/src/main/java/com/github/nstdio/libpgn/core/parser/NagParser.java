package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.exception.InvalidNagException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import static com.github.nstdio.libpgn.core.TokenTypes.NAG;
import static com.github.nstdio.libpgn.core.internal.CollectionUtils.toArray;

class NagParser extends AbstractParser implements InputParser<short[], short[]> {

    NagParser(final PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    /**
     * @param limit        Limit for return array size.
     * @param mergeWithNag In case if not null and not empty this array will be prepended to returned array.
     *
     * @return The parsed NAGs.
     */
    private short[] tryParseNag(final int limit, final short[] mergeWithNag) {
        if (lexer.last() != NAG) {
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

        final Collection<Short> nags = toCollection(mergeWith);

        final int estimatedSize = limit - mergeWithLen;
        do {
            nags.add(safeParseShort());
        } while (lexer.next() == NAG && nags.size() < estimatedSize);

        if (lexer.last() == NAG) {
            lexer.poll(NAG);
        }

        return toArray(nags);
    }

    private Collection<Short> toCollection(final @Nullable short[] src) {
        if (src == null || src.length == 0) {
            return collectionImpl();
        }

        final Collection<Short> nags = collectionImpl();

        for (short i : src) {
            nags.add(i);
        }

        return nags;
    }

    @Nonnull
    private Collection<Short> collectionImpl() {
        return config.allowDuplicationsInNags() ? new ArrayList<>() : new TreeSet<>();
    }

    /**
     * Parse short from {@code tokenStream} top element value. In case if exception occurred {@code 0} will be
     * returned.
     *
     * @return The short value of the string.
     */
    private Short safeParseShort() {
        try {

            return Short.valueOf(read().substring(1));
        } catch (NumberFormatException e) {
            if (config.useNullOnInvalidNag()) {
                return (short) 0;
            }

            throw new InvalidNagException("Invalid NAG.", e);
        }
    }

    @Override
    public short[] parse(short[] input) {
        return tryParseNag(config.nagLimit(), input);
    }
}
