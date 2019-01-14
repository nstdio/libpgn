module com.github.nstdio.libpgn.common {
    exports com.github.nstdio.libpgn.common to
            com.github.nstdio.libpgn.entity,
            com.github.nstdio.libpgn.collector,
            com.github.nstdio.libpgn.io,
            com.github.nstdio.libpgn.parser,
            com.github.nstdio.libpgn.fen;

    requires jsr305;
    requires lombok;
}