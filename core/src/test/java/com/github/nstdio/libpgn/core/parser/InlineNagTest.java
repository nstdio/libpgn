package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.internal.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class InlineNagTest {
    private InlineNag inlineNag;

    @Before
    public void setUp() throws Exception {
        inlineNag = new InlineNag();
    }

    @Test
    public void simple() throws Exception {
        Map<String, Pair<String, short[]>> map = new HashMap<>();

        map.put("e4", Pair.of("e4", new short[0]));
        map.put("e4!", Pair.of("e4", new short[]{1}));
        map.put("e4?", Pair.of("e4", new short[]{2}));
        map.put("e4‼", Pair.of("e4", new short[]{3}));
        map.put("e4!!", Pair.of("e4", new short[]{3}));
        map.put("e4!!!", Pair.of("e4", new short[]{1, 3}));
        map.put("e4!‼", Pair.of("e4", new short[]{1, 3}));
        map.put("e4⁇", Pair.of("e4", new short[]{4}));
        map.put("e4??", Pair.of("e4", new short[]{4}));
        map.put("e4???", Pair.of("e4", new short[]{2, 4}));
        map.put("e4!?", Pair.of("e4", new short[]{5}));
        map.put("e4⁉", Pair.of("e4", new short[]{5}));
        map.put("e4⁈", Pair.of("e4", new short[]{6}));
        map.put("e4?!", Pair.of("e4", new short[]{6}));
        map.put("e4?!!", Pair.of("e4", new short[]{1, 6}));
        map.put("e4+-", Pair.of("e4", new short[]{18}));
        map.put("e4-+", Pair.of("e4", new short[]{19}));
        map.put("e4RR", Pair.of("e4", new short[]{145}));
        map.put("e4NRR", Pair.of("e4", new short[]{145, 146}));
        map.put("e4RRN", Pair.of("e4", new short[]{145, 146}));
        map.put("e4!RRN", Pair.of("e4", new short[]{1, 145, 146}));
        map.put("e4!!RRN", Pair.of("e4", new short[]{3, 145, 146}));
        map.put("e4!!!RRN", Pair.of("e4", new short[]{1, 3, 145, 146}));
        map.put("e4NRR!!!", Pair.of("e4", new short[]{1, 3, 145, 146}));
        map.put("e4!!⁇⇔", Pair.of("e4", new short[]{3, 4, 239}));
        map.put("Nf3!", Pair.of("Nf3", new short[]{1}));

        for (Map.Entry<String, Pair<String, short[]>> entry : map.entrySet()) {
            final Pair<byte[], short[]> actual = inlineNag.split(entry.getKey().getBytes());

            final Pair<String, short[]> value = entry.getValue();
            final Pair<byte[], short[]> expected = Pair.of(value.first.getBytes(), value.second);

            assertThat(actual.first).containsExactly(expected.first);
            assertThat(actual.second).containsExactly(expected.second);
        }
    }
}