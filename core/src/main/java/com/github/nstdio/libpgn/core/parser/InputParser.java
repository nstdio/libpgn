package com.github.nstdio.libpgn.core.parser;

public interface InputParser<T, I> {

    T parse(I input);
}
