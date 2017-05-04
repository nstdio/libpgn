package com.asatryan.libpgn.core.parser;

public interface InputParser<T, I> {

    T parse(I input);
}
