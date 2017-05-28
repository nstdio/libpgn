package com.asatryan.libpgn.core;

import com.asatryan.libpgn.core.GameFilter.GameFilterBuilder;
import org.junit.Before;

public class GameFilterTest {
    private GameFilterBuilder builder;

    @Before
    public void setUp() {
        builder = GameFilter.builder();
    }
}