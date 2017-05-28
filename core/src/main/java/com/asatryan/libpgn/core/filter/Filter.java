package com.asatryan.libpgn.core.filter;

public interface Filter<T> {
    boolean test(T input);
}
