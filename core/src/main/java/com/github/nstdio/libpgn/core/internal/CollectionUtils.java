package com.github.nstdio.libpgn.core.internal;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * Note that after method return {@code src} will be empty.
     *
     * @param src The source {@code Collection} from witch array will be created.
     *
     * @return The {@code short[]} that contains all elements of {@code src}
     */
    public static short[] toArray(final @Nonnull Collection<Short> src) {
        Iterator<Short> it = src.iterator();
        final short[] ret = new short[src.size()];
        int i = 0;

        while (it.hasNext()) {
            ret[i++] = it.next();
            it.remove();
        }

        return ret;
    }

    public static <T> boolean isEmpty(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmptyOrNull(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> nullToEmpty(final List<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }
}
