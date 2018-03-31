package com.github.nstdio.libpgn.core.pgn.builder;

import com.github.nstdio.libpgn.core.internal.ArrayUtils;
import com.github.nstdio.libpgn.core.pgn.Move;
import com.github.nstdio.libpgn.core.pgn.MoveText;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC, staticName = "moveText")
public class MoveTextBuilder {
    private int moveNo;
    private Move white;
    private Move black;

    public MoveTextBuilder withMoveNo(final int moveNo) {
        this.moveNo = moveNo;
        return this;
    }

    public MoveBuilder white() {
        return new WhiteMoveBuilder();
    }

    public MoveBuilder black() {
        return new BlackMoveBuilder();
    }

    public MoveText build() {
        return MoveText.of(moveNo, white, black);
    }

    private class WhiteMoveBuilder extends MoveBuilder {
        @Override
        public MoveTextBuilder build() {
            MoveTextBuilder.this.white = buildMove();
            return MoveTextBuilder.this;
        }
    }

    private class BlackMoveBuilder extends MoveBuilder {
        @Override
        public MoveTextBuilder build() {
            MoveTextBuilder.this.black = buildMove();
            return MoveTextBuilder.this;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract class MoveBuilder {
        private byte[] move;
        private byte[] comment;
        private short[] nag;
        private List<MoveText> variations;

        public MoveBuilder move(final String move) {
            this.move = move.getBytes();
            return this;
        }

        public MoveBuilder comment(final String comment) {
            this.comment = comment.getBytes();
            return this;
        }

        public MoveBuilder nag(final short[] nag) {
            this.nag = nag;
            return this;
        }

        public MoveBuilder variations(final List<MoveText> variations) {
            this.variations = variations;
            return this;
        }


        Move buildMove() {
            if (ArrayUtils.isEmptyOrNull(move)) {
                throw new IllegalStateException("The move cannot be empty or null! Method withMove(String) should be invoked before build!");
            }

            return Move.ofImmutable(move, comment, nag, variations);
        }

        public abstract MoveTextBuilder build();
    }
}