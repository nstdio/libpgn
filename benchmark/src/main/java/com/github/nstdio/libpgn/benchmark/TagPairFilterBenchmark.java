package com.github.nstdio.libpgn.benchmark;

import com.github.nstdio.libpgn.core.TagPair;
import com.github.nstdio.libpgn.core.filter.Filters;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Thread)
public class TagPairFilterBenchmark {
    private List<TagPair> tagPairs;

    /**
     * To run this benchmark class:
     * <p>
     * java -cp benchmark/target/benchmarks.jar com.asatryan.libpgn.benchmark.TagPairFilterBenchmark
     *
     * @param args
     *
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(TagPairFilterBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .forks(2)
                .build();

        new Runner(options).run();
    }

    @Setup(Level.Iteration)
    public void setUp() throws IOException {
        tagPairs = new ArrayList<>();
        tagPairs.add(TagPair.of("Event", "Wch U16"));
        tagPairs.add(TagPair.of("Site", "Wattignies"));
        tagPairs.add(TagPair.of("Date", "1976.??.??"));
        tagPairs.add(TagPair.of("Round", "?"));
        tagPairs.add(TagPair.of("White", "Dunne, David Joseph"));
        tagPairs.add(TagPair.of("Black", "Kasparov, Gary"));
        tagPairs.add(TagPair.of("Result", "0-1"));
        tagPairs.add(TagPair.of("WhiteElo", ""));
        tagPairs.add(TagPair.of("BlackElo", ""));
        tagPairs.add(TagPair.of("ECO", "B51"));
    }

    @Benchmark
    public void whiteLastNameSplitImplSuccess() {
        Filters.whiteLastNameEquals("Dunne").test(tagPairs);
    }

    @Benchmark
    public void whiteLastNameSplitImplFail() {
        Filters.whiteLastNameEquals("Kaspa").test(tagPairs);
    }
}
