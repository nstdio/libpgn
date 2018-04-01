package com.github.nstdio.libpgn.io;

import com.github.nstdio.libpgn.entity.Game;
import com.github.nstdio.libpgn.entity.Move;
import com.github.nstdio.libpgn.entity.MoveText;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.nstdio.libpgn.common.ArrayUtils.isEmptyOrNull;
import static com.github.nstdio.libpgn.common.CollectionUtils.isNotEmptyOrNull;
import static com.github.nstdio.libpgn.common.ExceptionUtils.wrapChecked;

public class PgnOutputStreamImpl extends FilterOutputStream implements PgnOutputStream {
    public PgnOutputStreamImpl(final OutputStream out) {
        super(out);
    }

    @Override
    public void write(final Game game) {
        Optional.ofNullable(game.tagPairSection())
                .map(Collection::stream)
                .ifPresent(stream ->
                        stream.forEach(tagPair -> wrapChecked(() -> {
                            write('[');
                            write(tagPair.getTag());
                            writeSpace();
                            write('"');
                            write(tagPair.getValue());
                            write('"');
                            write(']');
                            write('\n');
                        }))
                );

        writeMoves(game.moves());

        wrapChecked(() -> write(game.gameResult().getTerm().getBytes()));
    }

    private void writeMoves(final List<MoveText> moves) {
        moves.forEach(moveText -> wrapChecked(() -> {
            write(String.valueOf(moveText.moveNo()).getBytes());
            write('.');
            writeSpace();

            moveText.white().ifPresent(this::writeMove);
            moveText.black().ifPresent(this::writeMove);
        }));
    }

    private void writeMove(final Move move) {
        wrapChecked(() -> {
            write(move.move());
            writeComment(move.comment());

            if (isNotEmptyOrNull(move.variations())) {
                writeMoves(move.variations());
            }

            writeSpace();
        });
    }

    private void writeSpace() throws IOException {
        write(' ');
    }

    private void writeComment(final byte[] comment) throws IOException {
        if (isEmptyOrNull(comment)) {
            return;
        }

        write('{');
        write(comment);
        write('}');
    }
}