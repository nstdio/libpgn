package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Move implements StringConvertible {
    private final static short[] EMPTY = {};
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
        this.variations = variations == null ? Collections.<Movetext>emptyList() : variations;
        this.nag = nag == null ? EMPTY : nag;
    }

    public static Builder builder() {
        return Builder.builder();
    }

    private void appendList(StringBuilder sb) {
        sb.append(" (");

        for (Movetext var : variations)
            sb.append(var.toPgnString()).append(' ');

        sb.append(") ");
    }

    public String move() {
        return move;
    }

    public String comment() {
        return comment;
    }

    public short[] nag() {
        if (nag == EMPTY) {
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
            sb.append(" {").append(comment).append("}");
        }

        if (variations.size() > 0) {
            appendList(sb);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toPgnString();
    }

    public static final class Builder {
        private String move;
        private String comment;
        private short[] nag;
        private List<Movetext> variations;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withMove(String move) {
            this.move = move;
            return this;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder withNag(short[] nag) {
            this.nag = nag;
            return this;
        }

        public Builder withVariations(List<Movetext> variations) {
            this.variations = variations;
            return this;
        }

        public Move build() {
            return new Move(move, comment, nag, variations);
        }
    }
}
