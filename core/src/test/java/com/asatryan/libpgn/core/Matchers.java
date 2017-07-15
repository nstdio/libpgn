package com.asatryan.libpgn.core;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;

public class Matchers {

    public static <T extends Collection> Matcher<T> size(final int size) {
        return new CustomTypeSafeMatcher<T>("") {
            @Override
            protected boolean matchesSafely(T item) {
                return item.size() == size;
            }
        };
    }
}
