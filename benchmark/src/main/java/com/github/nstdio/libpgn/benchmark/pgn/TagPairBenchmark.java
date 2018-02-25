package com.github.nstdio.libpgn.benchmark.pgn;

import com.github.nstdio.libpgn.core.pgn.TagPair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5)
public class TagPairBenchmark {

    private TagPair rawByte;
    private TagPair stringBased;
    private TagPair lazyString;

    /**
     * To run this benchmark class:
     * <p>
     * java -cp benchmark/target/benchmarks.jar com.github.nstdio.libpgn.benchmark.pgn.TagPairBenchmark
     *
     * @param args
     *
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(TagPairBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.CSV)
                .result("result-tag-pair.csv")
                .jvmArgs("-server")
                .forks(1)
                .build();

        new Runner(options).run();
    }

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        final String tag = "Tag";
        final String value = "Value";

        final byte[] tagBytes = tag.getBytes();
        final byte[] valueBytes = value.getBytes();

        rawByte = TagPair.of(tagBytes, valueBytes);
        stringBased = TagPair.of(tag, value);
        lazyString = TagPair.ofLazy(tag, value);
    }

    @Benchmark
    public byte[] rawBytes() {
        return rawByte.getTag();
    }

    @Benchmark
    public byte[] stringBased() {
        return stringBased.getTag();
    }

    @Benchmark
    public byte[] lazyStringBased() {
        return lazyString.getTag();
    }
}
