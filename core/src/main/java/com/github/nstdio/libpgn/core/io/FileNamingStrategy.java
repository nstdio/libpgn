package com.github.nstdio.libpgn.core.io;

import java.nio.file.Path;

public interface FileNamingStrategy {

    /**
     * Generates the name for file based on source file name and the current chunk number.
     *
     * @param source The source file path.
     * @param chunk  The current chunk number.
     *
     * @return The name.
     */
    Path name(Path source, int chunk);
}
