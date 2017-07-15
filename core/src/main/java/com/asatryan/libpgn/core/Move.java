package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.asatryan.libpgn.core.internal.EmptyArrays.EMPTY_SHORT_ARRAY;

@SuppressWarnings("WeakerAccess")
public class Move implements StringConvertible {
    @Nonnull
    private final String move;
    @Nullable
    private final String comment;
    @Nonnull
    private final short[] nag;
    @Nonnull
    private final List<Movetext> variations;

    public Move(@Nonnull String move, @Nullable String comment, short[] nag, List<Movetext> variations) {
        this.move = move;
        this.comment = comment;
        this.variations = variations == null ? Collections.emptyList() : variations;
        this.nag = nag == null ? EMPTY_SHORT_ARRAY : nag;
    }

    public String move() {
        return move;
    }

    public String comment() {
        return comment;
    }

    public short[] nag() {
        if (nag == EMPTY_SHORT_ARRAY) {
            return nag;
        }

        return nag.clone();
    }

    public List<Movetext> variations() {
        return variations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        Move moveObj = (Move) obj;
        if (!move.equals(moveObj.move)) {
            return false;
        }

        if (comment != null && !comment.equals(moveObj.comment)) {
            return false;
        }

        if (!Arrays.equals(nag, moveObj.nag)) {
            return false;
        }

        return variations.containsAll(moveObj.variations) && moveObj.variations.containsAll(variations);
    }

    @Override
    public String toPgnString() {
        StringBuilder sb = new StringBuilder();
        sb.append(move);
        if (nag.length > 0) {
            sb.append(NAG.glyphAtIndices(nag));
        }

        if (comment != null && comment.length() > 1) {
            sb.append(" {").append(comment).append('}');
        }

        if (variations.size() > 0) {
            sb.append(" (");
            for (Movetext var : variations) {
                sb.append(var.toPgnString()).append(' ');
            }
            sb.append(") ");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toPgnString();
    }
}
