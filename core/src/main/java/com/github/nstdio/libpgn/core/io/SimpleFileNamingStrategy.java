package com.github.nstdio.libpgn.core.io;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleFileNamingStrategy implements FileNamingStrategy {
    @Override
    public Path name(final Path source, final int chunk) {
        final String stringValue = source.getFileName().toString();

        final int dotIdx = stringValue.indexOf('.');

        final String concat = stringValue.substring(0, dotIdx)
                .concat(String.format("_%02d", chunk))
                .concat(stringValue.substring(dotIdx));

        return Paths.get(source.getParent().toString(), concat);
    }
}
