package com.asatryan.libpgn.core.internal;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;

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
}
