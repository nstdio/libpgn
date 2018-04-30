package com.github.nstdio.libpgn.io;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

public class PgnInputStreamTest {
    private PgnInputStream stream;

    private static PgnInputStream ofString(final String input) {
        return PgnInputStreamFactory.of(new ByteArrayInputStream(input.getBytes()));
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Test
    public void read() throws Exception {
        stream = ofString("abc abc");

        assertThat(stream.read()).isEqualTo('a');
        assertThat(stream.read()).isEqualTo('b');
        assertThat(stream.read()).isEqualTo('c');

        stream.close();

        stream = ofString("a");

        assertThat(stream.read()).isEqualTo('a');


        assertThatExceptionOfType(EOFException.class).isThrownBy(stream::read);
    }

    @Test
    public void readUntilWhenReadingSequentially() throws IOException {
        final String input = "abc";
        stream = ofString(input);

        assertThat(stream.until('a')).isEqualTo(1);
        assertThat(stream.until('b')).isEqualTo(2);
        assertThat(stream.until('c')).isEqualTo(3);

        assertThat(stream.read()).isEqualTo('a');
        assertThat(stream.until('b')).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void readUntilExisting() throws IOException {
        stream = ofString("abcdefg");

        stream.skip(1);
        assertThat(stream.until('f')).isEqualTo(5);
        assertThat(stream.read()).isEqualTo('b');
    }

    @Test
    public void readUntilNotFound() {
        stream = ofString("abc");

        assertThatExceptionOfType(EOFException.class).isThrownBy(() -> stream.until('d'));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void readUntilWhenAlreadyAtTheEnd() throws Exception {
        final String input = "abc";
        stream = ofString(input);

        stream.skip(input.length());

        assertThatExceptionOfType(EOFException.class)
                .isThrownBy(() -> stream.until('c'));
    }

    @Test
    public void readAheadSuccessful() throws IOException {
        stream = ofString("abc");

        Assertions.assertThat(stream.readAhead(2)).isEqualTo('b');
        Assertions.assertThat(stream.read()).isEqualTo('a');
    }

    @Test
    public void readAheadOutOfBound() throws IOException {
        stream = ofString("abc");

        assertThatExceptionOfType(EOFException.class).isThrownBy(() -> stream.readAhead(4));

        assertThat(stream.read()).isEqualTo('a');
    }

    @Test
    public void readAheadOffsetIsNegativeOrZero() throws IOException {
        stream = ofString("abc");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> stream.readAhead(0));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> stream.readAhead(-1));
    }

    @Test
    public void skipWhiteSpaceAtTheBeginning() throws IOException {
        stream = ofString("     abc");

        stream.skipWhiteSpace();

        assertThat(stream.read()).isEqualTo('a');
    }

    @Test
    public void skipWhiteSpaceAtTheEnd() throws IOException {
        stream = ofString("abc    ");

        stream.skipWhiteSpace();

        assertThat(stream.read()).isEqualTo('a');
        assertThat(stream.read()).isEqualTo('b');
        assertThat(stream.read()).isEqualTo('c');

        stream.skipWhiteSpace();

        assertThatExceptionOfType(EOFException.class).isThrownBy(stream::read);
    }

    @Test
    public void skipWhiteSpaceInTheMiddle() throws IOException {
        stream = ofString("\n\r\t \t a b      c    ");

        stream.skipWhiteSpace();
        assertThat(stream.read()).isEqualTo('a');

        stream.skipWhiteSpace();
        assertThat(stream.read()).isEqualTo('b');

        stream.skipWhiteSpace();
        assertThat(stream.read()).isEqualTo('c');

        stream.skipWhiteSpace();

        assertThatExceptionOfType(EOFException.class).isThrownBy(stream::read);
    }
}