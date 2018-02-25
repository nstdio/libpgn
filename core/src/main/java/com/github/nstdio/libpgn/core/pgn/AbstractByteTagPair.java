package com.github.nstdio.libpgn.core.pgn;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
abstract class AbstractByteTagPair implements TagPair {
    @NonNull
    final byte[] tag;

    @NonNull
    final byte[] value;
}
