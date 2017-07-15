package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public class TagPair implements StringConvertible {
    @Nonnull
    private final String tag;
    @Nonnull
    private final String value;

    private int hasCode;

    public TagPair(@Nonnull String tag, String value) {
        if (tag.length() == 0) {
            throw new IllegalArgumentException("tag cannot be empty");
        }
        this.tag = tag;
        this.value = value == null ? "" : value;
    }

    public static TagPair of(String tag, String value) {
        return new TagPair(tag, value);
    }

    public static TagPair ofEvent(final String value) {
        return of("Event", value);
    }

    public static TagPair ofSite(final String value) {
        return of("Site", value);
    }

    public static TagPair ofDate(final String value) {
        return of("Date", value);
    }

    public static TagPair of(TagPair tagPair) {
        return new TagPair(tagPair.tag, tagPair.value);
    }

    public static int hashCode(@Nonnull final String tag, @Nonnull final String value) {
        return 31 * tag.hashCode() + value.hashCode();
    }

    @Nonnull
    public String getTag() {
        return tag;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("[%s \"%s\"]", tag, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TagPair)) {
            return false;
        }
        TagPair tagPair = (TagPair) obj;

        return tag.equals(tagPair.tag) && value.equals(tagPair.value);
    }

    @Override
    public int hashCode() {
        if (hasCode == 0) {
            hasCode = hashCode(tag, value);
        }

        return hasCode;
    }

    @Override
    public String toPgnString() {
        return toString();
    }
}
