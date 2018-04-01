package com.github.nstdio.libpgn.entity;

import javax.annotation.Nonnull;

import static com.github.nstdio.libpgn.common.ArrayUtils.copy;

/**
 * The PGN metadata.
 */
public interface TagPair {
    /**
     * Creates the new {@code TagPair} instance backed by raw bytes. The provided byte arrays will be stored without any
     * copy and any change will affect stored arrays. Also the accessor methods will return non-copied arrays. This
     * implementation is intended to deal with reasonably large PGN collection.
     *
     * @param tag   The tag pair tag represented in bytes.
     * @param value The tag pair value represented in bytes.
     *
     * @return The newly created tag pair object.
     */
    static TagPair of(final byte[] tag, final byte[] value) {
        return new RawByteTagPair(tag, value);
    }

    /**
     * Creates the new {@code TagPair} instance backed by raw bytes.
     *
     * @param tag   The tag pair tag represented in bytes.
     * @param value The tag pair value represented in bytes.
     *
     * @return The newly created tag pair object.
     */
    static TagPair ofCopied(final byte[] tag, final byte[] value) {
        return new CopyByteTagPair(copy(tag), copy(value));
    }

    static TagPair of(final String tag, final String value) {
        return new StringTagPair(tag, value);
    }

    static TagPair ofLazy(final String tag, final String value) {
        return new LazyByteTagPair(tag, value);
    }

    /**
     * @return The tag pair tag represented in bytes.
     */
    @Nonnull
    byte[] getTag();

    /**
     * @return The tag pair value represented in bytes.
     */
    @Nonnull
    byte[] getValue();

    default String getTagAsString() {
        return new String(getTag());
    }

    default String getValueAsString() {
        return new String(getValue());
    }
}
