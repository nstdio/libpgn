package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.GameFilter.GameFilterBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;

public class GameFilterTest {
    private static InputStream res;
    private GameFilterBuilder builder;

    @BeforeClass
    public static void setUpClass() {
        res = GameFilterTest.class.getResourceAsStream("/Aronian.pgn");
    }

    @Before
    public void setUp() {
        builder = GameFilter.builder();
    }

    @Test
    public void res() throws Exception {
        Assert.assertNotNull(res);
    }
}