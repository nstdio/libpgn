package com.github.nstdio.libpgn.entity;

import com.github.nstdio.libpgn.common.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.nstdio.libpgn.entity.MoveFactory.Holder.IMMUTABLE;
import static com.github.nstdio.libpgn.entity.MoveFactory.Holder.MUTABLE;

/**
 * Represents a color agnostic move definition. Provides some convenient factory method for various use cases.
 */
public interface Move {

    /**
     * Creates the new {@code Move} instance with all possible components. Most comprehensive factory method.
     *
     * @param move       The actual move.
     * @param comment    The commentary.
     * @param nag        The Numeric Annotation Glyphs.
     * @param variations The alternative variations.
     *
     * @return Newly created {@code Move} instance.
     *
     * @throws IllegalArgumentException When {@code move} is {@code null} or size is less then {@code 2}.
     */
    static Move of(byte[] move, byte[] comment, short[] nag, List<MoveText> variations) {
        return MUTABLE.create(move, comment, nag, variations);
    }

    /**
     * Creates the new {@code Move} instance without variations.
     *
     * @param move    The actual move.
     * @param comment The commentary.
     * @param nag     The Numeric Annotation Glyphs.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(byte[] move, byte[] comment, short[] nag) {
        return of(move, comment, nag, Collections.emptyList());
    }

    /**
     * Creates the new {@code Move} instance with move and comment.
     *
     * @param move    The actual move.
     * @param comment The commentary.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[], short[])
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(byte[] move, byte[] comment) {
        return of(move, comment, ArrayUtils.EMPTY_SHORT_ARRAY);
    }

    /**
     * Creates the new {@code Move} instance with move.
     *
     * @param move The actual move.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[])
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(byte[] move) {
        return of(move, ArrayUtils.EMPTY_BYTE_ARRAY);
    }

    /**
     * Creates the new {@code Move} instance with move.
     *
     * @param move The actual move string representation.
     *
     * @return Newly created {@code Move} instance.
     *
     * @throws NullPointerException When {@code move} is {@literal null}.
     * @see Move#of(byte[])
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(String move) {
        return of(Objects.requireNonNull(move, "move cannot be null").getBytes());
    }

    /**
     * Creates the new {@code Move} instance with actual move and comment.
     *
     * @param move    The actual move string representation.
     * @param comment The commentary string representation.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[])
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(String move, String comment) {
        return of(move.getBytes(), Objects.requireNonNull(comment).getBytes());
    }

    /**
     * Creates the new {@code Move} instance with actual move and variations.
     *
     * @param move       The actual move string representation.
     * @param variations The alternative variations.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(String move, List<MoveText> variations) {
        return of(move.getBytes(), ArrayUtils.EMPTY_BYTE_ARRAY, ArrayUtils.EMPTY_SHORT_ARRAY, variations);
    }

    /**
     * Creates the new {@code Move} instance with actual move and nag.
     *
     * @param move The actual move string representation.
     * @param nag  The Numeric Annotation Glyphs.
     *
     * @return Newly created {@code Move} instance.
     *
     * @see Move#of(byte[], byte[], short[], List)
     */
    static Move of(String move, short[] nag) {
        return of(move.getBytes(), ArrayUtils.EMPTY_BYTE_ARRAY, nag, Collections.emptyList());
    }

    /**
     * Creates the new {@code Move} instance with actual move and comment, variations.
     *
     * @param move       The actual move string representation.
     * @param comment    The commentary string representation.
     * @param variations The alternative variations.
     *
     * @return Move#of(byte[], byte[], short[], List)
     */
    static Move of(String move, String comment, List<MoveText> variations) {
        return of(move.getBytes(), comment.getBytes(), ArrayUtils.EMPTY_SHORT_ARRAY, variations);
    }

    /**
     * Creates the new {@code Move} immutable instance with move.
     *
     * @param move The actual move.
     *
     * @return Newly created {@code Move} immutable instance.
     */
    static Move ofImmutable(byte[] move, byte[] comments, short[] nag, List<MoveText> variations) {
        return IMMUTABLE.create(move, comments, nag, variations);
    }

    /**
     * Creates the new {@code Move} immutable instance without variations.
     *
     * @param move    The actual move.
     * @param comment The commentary.
     * @param nag     The Numeric Annotation Glyphs.
     *
     * @return Newly created {@code Move} immutable instance.
     *
     * @see Move#ofImmutable(byte[], byte[], short[], List)
     */
    static Move ofImmutable(byte[] move, byte[] comment, short nag[]) {
        return ofImmutable(move, comment, nag, Collections.emptyList());
    }

    /**
     * Creates the new {@code Move} instance with move and comment.
     *
     * @param move    The actual move.
     * @param comment The commentary.
     *
     * @return Newly created {@code Move} immutable instance.
     *
     * @see Move#ofImmutable(byte[], byte[], short[])
     * @see Move#ofImmutable(byte[], byte[], short[], List)
     */
    static Move ofImmutable(byte[] move, byte[] comment) {
        return ofImmutable(move, comment, ArrayUtils.EMPTY_SHORT_ARRAY);
    }

    /**
     * Creates the new {@code Move} instance with move.
     *
     * @param move The actual move.
     *
     * @return Newly created {@code Move} immutable instance.
     *
     * @see Move#ofImmutable(byte[], byte[])
     * @see Move#ofImmutable(byte[], byte[], short[], List)
     */
    static Move ofImmutable(byte[] move) {
        return ofImmutable(move, ArrayUtils.EMPTY_BYTE_ARRAY);
    }

    /**
     * Creates the new {@code Move} immutable instance with move.
     *
     * @param move The actual move string representation.
     *
     * @return Newly created {@code Move} immutable instance.
     *
     * @throws NullPointerException When {@code move} is {@literal null}.
     * @see Move#ofImmutable(byte[])
     */
    static Move ofImmutable(final String move) {
        return ofImmutable(Objects.requireNonNull(move, "move cannot be null").getBytes());
    }

    /**
     * The move bytes. The underlying implementation may make defensive copies to achieve immutability.
     *
     * @return The move bytes.
     */
    @Nonnull
    byte[] move();

    /**
     * The comment bytes. The underlying implementation may make defensive copies to achieve immutability.
     *
     * @return The comment bytes.
     */
    @Nonnull
    byte[] comment();

    /**
     * The NAGs as shorts. The underlying implementation may make defensive copies to achieve immutability.
     *
     * @return The NAGs as shorts.
     */
    @Nonnull
    short[] nag();

    /**
     * The NAGs as shorts. The underlying implementation may make defensive copies to achieve immutability.
     *
     * @return The NAGs as shorts.
     */
    @Nonnull
    List<MoveText> variations();
}
