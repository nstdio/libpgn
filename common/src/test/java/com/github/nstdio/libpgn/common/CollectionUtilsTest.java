package com.github.nstdio.libpgn.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {
    static Stream<Collection> notEmptyCollections() {
        return Stream.of(
                Collections.singletonList(1),
                Collections.singleton(1)
        );
    }

    @ParameterizedTest
    @MethodSource("notEmptyCollections")
    public void isNotEmptyOrNull(final Collection<?> arg) {
        assertThat(CollectionUtils.isNotEmptyOrNull(arg)).isTrue();
    }
}