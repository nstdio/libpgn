package com.asatryan.libpgn.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused", "SameParameterValue", "JavadocReference"})
public class Configuration {
    public static final int TAG_PAIR_VALUE_MAX_LEN = 255;
    public static final int DEFAULT_NAG_LIMIT = 8;
    public static final int COMMENT_LENGTH_UNLIMITED = -1;
    public static final int DEFAULT_TAG_PAIR_CACHE_SIZE = 512;

    private final Set<TagPair> predefinedCache;

    private final boolean skipTagPairSection;
    private final boolean skipMovetext;
    private final boolean skipComment;
    private final boolean skipVariations;
    private final boolean strict;
    private final boolean stopOnError;
    private final boolean allowDuplicationsInNags;
    private final boolean sortNags;
    private final boolean useNullOnInvalidNag;
    private final boolean trimComment;
    private final boolean extractLiteralNags;
    private final boolean cacheTagPair;
    private final String threatNagAsComment;
    private final int nagLimit;
    private final int tagPairValueMaxLength;
    private final int commentMaxLength;
    private final int tagPairCacheSize;

    private Configuration(Set<TagPair> predefinedCache, boolean skipTagPairSection, boolean skipMovetext, boolean skipComment, boolean skipVariations,
                          boolean stopOnError, String threatNagAsComment, boolean strict, boolean allowDuplicationsInNags,
                          boolean useNullOnInvalidNag, boolean sortNags,
                          boolean trimComment, boolean extractLiteralNags, boolean cacheTagPair, int nagLimit, int tagPairValueMaxLength, int commentMaxLength, int tagPairCacheSize) {
        this.predefinedCache = predefinedCache;
        this.skipTagPairSection = skipTagPairSection;
        this.skipMovetext = skipMovetext;
        this.skipComment = skipComment;
        this.skipVariations = skipVariations;
        this.stopOnError = stopOnError;
        this.threatNagAsComment = threatNagAsComment;
        this.strict = strict;
        this.allowDuplicationsInNags = allowDuplicationsInNags;
        this.useNullOnInvalidNag = useNullOnInvalidNag;
        this.sortNags = sortNags;
        this.trimComment = trimComment;
        this.extractLiteralNags = extractLiteralNags;
        this.cacheTagPair = cacheTagPair;
        this.nagLimit = nagLimit;
        this.tagPairValueMaxLength = tagPairValueMaxLength;
        this.commentMaxLength = commentMaxLength;
        this.tagPairCacheSize = tagPairCacheSize;
    }

    public static Configuration defaultConfiguration() {
        return defaultBuilder()
                .build();
    }

    public static ConfigurationBuilder defaultBuilder() {
        return builder()
                .strict(false)
                .allowDuplicationsInNags(false)
                .useNullOnInvalidNag(true)
                .extractLiteralNags(true)
                .nagLimit(DEFAULT_NAG_LIMIT);
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    @Nullable
    public Set<TagPair> predefinedCache() {
        return predefinedCache;
    }

    /**
     * @see Configuration.ConfigurationBuilder#extractLiteralNags(boolean)
     */
    public boolean extractInlineNags() {
        return extractLiteralNags;
    }

    /**
     * @see Configuration.ConfigurationBuilder#skipTagPairSection(boolean)
     */
    public boolean skipTagPairSection() {
        return skipTagPairSection;
    }

    /**
     * @see Configuration.ConfigurationBuilder#skipMovetext(boolean)
     */
    public boolean skipMovetext() {
        return skipMovetext;
    }

    /**
     * @see Configuration.ConfigurationBuilder#skipComment(boolean)
     */
    public boolean skipComment() {
        return skipComment;
    }

    /**
     * @see Configuration.ConfigurationBuilder#skipVariations(boolean)
     */
    public boolean skipVariations() {
        return skipVariations;
    }

    /**
     * @see Configuration.ConfigurationBuilder#stopOnError(boolean)
     */
    public boolean stopOnError() {
        return stopOnError;
    }

    /**
     * @see Configuration.ConfigurationBuilder#threatNagAsComment(String)
     */
    public String threatNagAsComment() {
        return threatNagAsComment;
    }

    /**
     * @see Configuration.ConfigurationBuilder#nagLimit(int)
     */
    public int nagLimit() {
        return nagLimit;
    }

    /**
     * @see Configuration.ConfigurationBuilder#allowDuplicationsInNags(boolean)
     */
    public boolean allowDuplicationsInNags() {
        return allowDuplicationsInNags;
    }

    /**
     * @see Configuration.ConfigurationBuilder#trimComment(boolean)
     */
    public boolean trimComment() {
        return trimComment;
    }

    /**
     * @see Configuration.ConfigurationBuilder#commentMaxLength(int)
     */
    public int commentMaxLength() {
        return commentMaxLength;
    }

    /**
     * @see Configuration.ConfigurationBuilder#cacheTagPair(boolean)
     */
    public boolean cacheTagPair() {
        return cacheTagPair;
    }

    /**
     * @see Configuration.ConfigurationBuilder#tagPairCacheSize(int)
     */
    public int tagPairCacheSize() {
        return tagPairCacheSize;
    }

    public static final class ConfigurationBuilder {
        private Set<TagPair> predefinedCache;
        private boolean skipTagPairSection;
        private boolean skipMovetext;
        private boolean skipComment;
        private boolean skipVariations;
        private boolean stopOnError;
        private String threatNagAsComment;
        private boolean strict;
        private boolean allowDuplicationsInNags;
        private boolean useNullOnInvalidNag;
        private boolean sortNags;
        private boolean trimComment;
        private boolean extractLiteralNags = false;
        private boolean cacheTagPair = false;
        private int nagLimit = DEFAULT_NAG_LIMIT;
        private int tagPairValueMaxLength = TAG_PAIR_VALUE_MAX_LEN;
        private int commentMaxLength = COMMENT_LENGTH_UNLIMITED;
        private int tagPairCacheSize = DEFAULT_TAG_PAIR_CACHE_SIZE;

        private ConfigurationBuilder() {
        }

        /**
         * @param predefinedCache The item will be added to the tag pair cache. If {@link #cacheTagPair} is {@code
         *                        false} this values will be omitted.
         *
         * @return ConfigurationBuilder itself.
         * @see #predefinedCache(TagPair...)
         */
        public ConfigurationBuilder predefinedCache(final @Nonnull Set<TagPair> predefinedCache) {
            if (this.predefinedCache == null) {
                this.predefinedCache = new HashSet<>(predefinedCache);
            } else {
                this.predefinedCache.addAll(predefinedCache);
            }

            return this;
        }

        /**
         * @param predefinedCache The predefined cache.
         *
         * @return ConfigurationBuilder itself.
         * @see #predefinedCache(Set)
         */
        public ConfigurationBuilder predefinedCache(@Nonnull TagPair... predefinedCache) {
            return predefinedCache(new HashSet<>(Arrays.asList(predefinedCache)));
        }

        /**
         * @param skipTagPairSection Whether parser should parse tag pairs or not. Anyway syntax errors will be
         *                           reported.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder skipTagPairSection(boolean skipTagPairSection) {
            this.skipTagPairSection = skipTagPairSection;
            return this;
        }

        /**
         * @param skipMovetext Whether parser should parse moves or not. Anyway syntax errors will be reported.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder skipMovetext(boolean skipMovetext) {
            this.skipMovetext = skipMovetext;
            return this;
        }

        /**
         * @param skipComment Whether parser should parse comments or not. Anyway syntax errors will be reported.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder skipComment(boolean skipComment) {
            this.skipComment = skipComment;
            return this;
        }

        /**
         * @param skipVariations Whether parser should parse variation or not. Anyway syntax errors will be reported.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder skipVariations(boolean skipVariations) {
            this.skipVariations = skipVariations;
            return this;
        }

        /**
         * @param stopOnError Whether parser should stop execution on first error or collect exception.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder stopOnError(boolean stopOnError) {
            this.stopOnError = stopOnError;
            return this;
        }

        /**
         * <p>Examples:
         * <blockquote><pre>
         * 1. e4 $1 $1 $2 will result in {1, 2}
         * 1. e4 $1 $1 $2 $2 will result in {1, 2}
         * </pre></blockquote>
         *
         * @param allowDuplicationsInNags Whether should allow duplicate nags on same move or not. Default is <code>false</code>
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder allowDuplicationsInNags(boolean allowDuplicationsInNags) {
            this.allowDuplicationsInNags = allowDuplicationsInNags;
            return this;
        }

        /**
         * <p>Examples:
         * <blockquote><pre>
         * 1. e4 $abc $2 will result in {0, 2}
         * 1. e4 $a $b will result in {0} // <code>{@link #allowDuplicationsInNags} == true</code>
         * 1. e4 $a $b will result in {0, 0} // {@link #allowDuplicationsInNags} == false
         * </pre></blockquote>
         *
         * @param useNullOnInvalidNag Whether when parser determine invalid nag value should use <code>0</code> instead or
         *                            throw exception.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder useNullOnInvalidNag(boolean useNullOnInvalidNag) {
            this.useNullOnInvalidNag = useNullOnInvalidNag;
            return this;
        }

        /**
         * <p>Examples:
         * <blockquote><pre>
         * 1.e4 $2 $1 $3 will result in {1, 2, 3} // {@link #sortNags} == true.
         * 1.e4 $2 $2 $3 will result in {2, 3} // {@link #sortNags} == true &amp;&amp; {@link #allowDuplicationsInNags}
         * == false.
         * 1.e4 $a $3 $1 will result in {0, 2, 3} // {@link #sortNags} == true &amp;&amp; {@link
         * #allowDuplicationsInNags} ==
         * false &amp;&amp; {@link #useNullOnInvalidNag} == true.
         * 1.e4 $2 $1 $3 will result in {2, 1, 3} // {@link #sortNags} = false.
         * </pre></blockquote>
         *
         * @param sortNags Whether sort nags on ascending order or parse as is.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder sortNags(boolean sortNags) {
            this.sortNags = sortNags;
            return this;
        }

        /**
         * @param extractLiteralNags Whether should extract literal nags. By default {@c }
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder extractLiteralNags(boolean extractLiteralNags) {
            this.extractLiteralNags = extractLiteralNags;
            return this;
        }

        /**
         * Whether use should use PGN strict specification or not.
         *
         * @param strict The strictness indicator.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * This configuration allows to represent NAG's description in comment. Every NAG has its own textual
         * representations.
         * <p>Examples
         * <blockquote><pre>
         * 1. e4 $1 will result in e4 {Very good move}
         * 1. e4! will result in e4 {Very good move}
         * 1. e4! {First move} will result in e4 {First move. Very good move} // {@code threatNagAsComment == ". "}.
         * 1. e4 $1$7 {First move} will result in e4 {First move. Very good move. Forced move (all others lose quickly)}
         * // {@link #threatNagAsComment == ". "}.
         * </pre></blockquote>
         *
         * @param threatNagAsComment If multiple nags discovered on particular move {@code threatNagAsComment} will be
         *                           used as separator string. If comment already present in string {@code
         *                           threatNagAsComment} will be appended to existing comment.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder threatNagAsComment(String threatNagAsComment) {
            this.threatNagAsComment = threatNagAsComment;
            return this;
        }

        /**
         * By default {@link String#trim()} will be used.
         * <p>Examples:
         * <blockquote><pre>
         * 1. e4 { Comment   } will result in " Comment   " // {@link #trimComment} == false
         * 1. e4 { Comment   } will result in "Comment" // {@link #trimComment} == true
         * </pre></blockquote>
         *
         * @param trimComment Whether should trim whitespaces at the end and start of comments or not.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder trimComment(boolean trimComment) {
            this.trimComment = trimComment;
            return this;
        }

        /**
         * {@code -1} indicates that comment length has not maximum. <p>Examples:
         * <blockquote><pre>
         * 1. e4 {Comment} will result in "C" // {@link #commentMaxLength} == 1
         * 1. e4 {This is comment on white's first move} will result in "This is comment on white's first move" //
         * {@linkplain #commentMaxLength} == -1
         * </pre></blockquote>
         *
         * @param commentMaxLength Whether should trim comments at fixed length if exceeds. Default value is {@code -1}
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder commentMaxLength(int commentMaxLength) {
            if (commentMaxLength < -1) {
                commentMaxLength = COMMENT_LENGTH_UNLIMITED;
            }
            this.commentMaxLength = commentMaxLength;
            return this;
        }

        public ConfigurationBuilder nagLimit(int nagLimit) {
            this.nagLimit = nagLimit;
            return this;
        }

        /**
         * By the PGN Specification tag pair value must not have length greater then 255 characters.
         * You can control that length by setting this property.
         *
         * @param maxLen The maximum length of tag pair value.
         *
         * @return ConfigurationBuilder itself.
         */
        public ConfigurationBuilder tagPairValueMaxLength(int maxLen) {
            tagPairValueMaxLength = maxLen;
            return this;
        }

        /**
         * {@code TagPair} is immutable so we can perform caching on same tag pairs to prevent memory allocation.
         *
         * @param cacheTagPair Whether use cache for {@link TagPair} instances or not.
         *
         * @return ConfigurationBuilder itself.
         * @see #tagPairCacheSize(int)
         */
        public ConfigurationBuilder cacheTagPair(boolean cacheTagPair) {
            this.cacheTagPair = cacheTagPair;
            return this;
        }

        /**
         * The maximum capacity of cache. When parsing a large amount of data its recommended to set this value above
         * default.
         * As a result same tag pairs will be referenced to the same {@link TagPair}
         *
         * @param tagPairCacheSize The maximum size of tag pair cache container. This value will be ignored if {@link
         *                         #cacheTagPair} is {@code false}. Default value is {@link DEFAULT_TAG_PAIR_CACHE_SIZE}.
         *                         Only positive numbers will be accepted otherwise cache size will be set to its
         *                         default value.
         *
         * @return ConfigurationBuilder itself.
         * @see #cacheTagPair(boolean)
         */
        public ConfigurationBuilder tagPairCacheSize(int tagPairCacheSize) {
            if (tagPairCacheSize <= 0) {
                tagPairCacheSize = DEFAULT_TAG_PAIR_CACHE_SIZE;
            }
            this.tagPairCacheSize = tagPairCacheSize;
            return this;
        }

        public Configuration build() {
            return new Configuration(
                    predefinedCache, skipTagPairSection,
                    skipMovetext,
                    skipComment,
                    skipVariations,
                    stopOnError,
                    threatNagAsComment,
                    strict,
                    allowDuplicationsInNags,
                    useNullOnInvalidNag,
                    sortNags,
                    trimComment,
                    extractLiteralNags,
                    cacheTagPair, nagLimit,
                    tagPairValueMaxLength,
                    commentMaxLength,
                    tagPairCacheSize);
        }
    }
}
