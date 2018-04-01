package com.github.nstdio.libpgn.common;

/**
 * Specialized version for {@link java.util.function.BiConsumer} to accept two primitive integers and avoid boxing.
 *
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface BiIntConsumer {
    /**
     * Performs this operation on the given arguments.
     *
     * @param first  the first input argument
     * @param second the second input argument
     */
    void accept(int first, int second);
}
