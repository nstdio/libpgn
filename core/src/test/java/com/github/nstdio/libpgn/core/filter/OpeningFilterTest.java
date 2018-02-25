package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.pgn.MoveText;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class OpeningFilterTest {
    private static List<MoveText> moves(final String... moves) {
        return new ArrayList<>(MoveText.moves(moves));
    }

    private static StartsWithMovesFilter openingFilter(final List<MoveText> moves) {
        return new StartsWithMovesFilter(moves);
    }

    @Test
    public void successful() {
        final List<MoveText> moves = moves("e4", "e5", "Nf3", "d6");

        assertThat(openingFilter(moves).test(moves)).isTrue();
    }

    @Test
    public void nullList() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new StartsWithMovesFilter(null))
                .withMessageMatching("\\w+ == null");
    }

    @Test
    public void searchingNotPerformedWhenInputSizeLessThenExpectedSize() {
        final List<MoveText> movesExpected = moves("e4", "e5", "Nf3", "d6");
        final List<MoveText> movesInput = moves("e4");

        final List<MoveText> spyExpected = spy(movesExpected);
        final List<MoveText> spyInput = spy(movesInput);

        assertThat(openingFilter(spyExpected).test(spyInput)).isFalse();

        verify(spyExpected).size();
        verify(spyInput).size();

        verifyNoMoreInteractions(spyExpected, spyInput);
    }

    @Test
    @Ignore("FIX")
    public void takingMinimalSize() {
        final List<MoveText> movesExpected = moves("e4", "e5", "Nf3");
        final List<MoveText> movesInput = moves("e4", "e5", "Nf3", "d6", "Nc3", "g6");

        final List<MoveText> spyExpected = spy(movesExpected);
        final List<MoveText> spyInput = spy(movesInput);

        assertThat(openingFilter(spyExpected)).accepts(spyInput);

        verify(spyExpected).size();
        verify(spyInput).size();

        verify(spyExpected, times(2)).get(anyInt());
        verify(spyInput, times(2)).get(anyInt());

        verifyNoMoreInteractions(spyExpected, spyInput);
    }
}