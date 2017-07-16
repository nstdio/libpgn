package com.github.nstdio.libpgn.benchmark;

import com.github.nstdio.libpgn.core.TokenTypes;
import com.github.nstdio.libpgn.core.parser.PgnLexer;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.InputStream;

@State(Scope.Thread)
public class PgnLexerBenchmark {
    private byte[] data;

    /**
     * To run this benchmark class:
     * <p>
     * java -cp benchmark/target/benchmarks.jar com.asatryan.libpgn.benchmark.PgnLexerBenchmark
     *
     * @param args
     *
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(PgnLexerBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .forks(1)
                .build();

        new Runner(options).run();
    }

    @Setup(Level.Iteration)
    public void setUp() throws IOException {
        final InputStream file = getClass().getClassLoader().getResourceAsStream("Aronian.pgn");

        data = IOUtils.toByteArray(file);
    }

    @Benchmark
    public void lexerDataWithoutCopy() {
        final PgnLexer pgnLexer = new PgnLexer(data);

        pgnLexer.queue(TokenTypes.UNDEFINED);
    }
}
