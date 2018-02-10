package com.github.nstdio.libpgn.core.fen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FenTestCaseReader {

    public void forEachTestCaseIn(final String dir, final Consumer<FenTestCase> consumer) {
        Optional.ofNullable(getClass().getClassLoader().getResource(Paths.get("fen", dir).toString()))
                .map(URL::getPath)
                .map(Paths::get)
                .map(path -> path.toFile().listFiles())
                .ifPresent(files -> Stream.of(files)
                        .filter(File::isFile)
                        .map(this::file2TestCase)
                        .forEach(consumer)
                );
    }

    private FenTestCase file2TestCase(final File file) {
        final Path path = file.toPath();
        try (final BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String moves = null;
            final List<String> fens = new ArrayList<>();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("--PGN--")) {
                    moves = readUntilFENLabel(bufferedReader);
                } else {
                    fens.add(line);
                }

            }

            return new FenTestCase(path.getFileName().toString(), moves, Collections.unmodifiableList(fens));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readUntilFENLabel(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while (!Objects.equals(line = reader.readLine(), "--FEN--")) {

            lines.add(line);
        }

        return String.join(" ", lines);
    }

    public static class FenTestCase {
        private final String filename;
        private final String moves;
        private final List<String> fens;

        public FenTestCase(final String filename, String moves, final List<String> fens) {
            this.filename = filename;
            this.moves = moves;
            this.fens = fens;
        }

        public String getMoves() {
            return moves;
        }

        public List<String> getFens() {
            return fens;
        }

        public String getFilename() {
            return filename;
        }
    }
}
