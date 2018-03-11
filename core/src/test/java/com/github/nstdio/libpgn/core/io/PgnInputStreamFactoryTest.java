package com.github.nstdio.libpgn.core.io;

import com.github.nstdio.libpgn.core.parser.InputStreamPgnLexer;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.zip.ZipException;

import static com.github.nstdio.libpgn.core.assertj.Assertions.assertThatLexer;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PgnInputStreamFactoryTest {
    @Nonnull
    private static File resourceFile(final String resourceName) {
        return new File(PgnInputStreamFactoryTest.class.getResource(resourceName).getFile());
    }

    private static InputStream resourceInputStream(final String resourceName) {
        return PgnInputStreamFactoryTest.class.getResourceAsStream(resourceName);
    }

    private static void assertSameTokenStream(final String res1, final String res2) {
        try (final InputStreamPgnLexer actual = InputStreamPgnLexer.of(PgnInputStreamFactory.of(resourceFile(res1)));
             final InputStreamPgnLexer expected = InputStreamPgnLexer.of(resourceInputStream(res2))) {
            assertThatLexer(actual).producesSameTokensAs(expected);
        }
    }

    @Test
    public void singleGame() {
        assertSameTokenStream("/compress/single_game.zip", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void twoGames_ZIP() {
        assertSameTokenStream("/compress/two_games.zip", "/compress/uncompressed_2.pgn");
    }

    @Test
    @Ignore("Not Supported by Commons Compress.")
    public void twoGames_7ZIP() {
        assertSameTokenStream("/compress/two_games.7z", "/compress/uncompressed_2.pgn");
    }

    @Test
    public void twoGames_TAR_XZ() {
        assertSameTokenStream("/compress/two_games.tar.xz", "/compress/uncompressed_2.pgn");
    }

    @Test
    public void singleGame_TAR_XZ() {
        assertSameTokenStream("/compress/single_game.tar.xz", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void singleGame_TAR_GZ() {
        assertSameTokenStream("/compress/single_game.tar.gz", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void singleGame_BZ2() {
        assertSameTokenStream("/compress/single_game.bz2", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void singleGame_7Z() {
        assertSameTokenStream("/compress/single_game.7z", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void singleGame_PGN() {
        assertSameTokenStream("/compress/uncompressed_1.pgn", "/compress/uncompressed_1.pgn");
    }

    @Test
    public void emptyZipFile() {
        assertThatExceptionOfType(UncheckedIOException.class)
                .isThrownBy(() -> PgnInputStreamFactory.of(resourceFile("/compress/empty.zip")))
                .withCauseExactlyInstanceOf(ZipException.class);
    }
}