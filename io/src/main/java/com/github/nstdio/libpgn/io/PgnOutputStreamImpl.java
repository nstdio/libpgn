package com.github.nstdio.libpgn.io;

import com.github.nstdio.libpgn.common.ThrowingRunnable;
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

    private final ThrowingRunnable noOpMoveFinisher = ThrowingRunnable.empty();

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

        writeMoves(game.moves(), this::writeSpace);

        wrapChecked(() -> write(game.gameResult().getTerm().getBytes()));
    }

    private void writeMoves(final List<MoveText> moves, final ThrowingRunnable finisher) {
        moves.forEach(moveText -> wrapChecked(() -> {
            write(String.valueOf(moveText.moveNo()).getBytes());

            if (moves.get(0).white().isPresent()) {
                write('.');
            } else {
                write('.');
                write('.');
                write('.');
            }

            writeSpace();

            moveText.white().ifPresent(white -> writeMove(white, finisher));
            moveText.black().ifPresent(black -> writeMove(black, finisher));
        }));
    }

    private void writeMove(final Move move, final ThrowingRunnable finisher) {
        wrapChecked(() -> {
            write(move.move());
            writeComment(move.comment());

            if (isNotEmptyOrNull(move.variations())) {
                writeSpace();
                write('(');
                writeMoves(move.variations(), noOpMoveFinisher);
                write(')');
            }

            finisher.run();
        });
    }

    private void writeSpace() throws IOException {
        write(' ');
    }

    private void writeComment(final byte[] comment) throws IOException {
        if (isEmptyOrNull(comment)) {
            return;
        }

        write(' ');
        write('{');
        write(comment);
        write('}');
    }
}