package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.TagPair;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import javax.annotation.Nonnull;
import java.util.*;

import static com.asatryan.libpgn.core.TokenTypes.*;
import static com.asatryan.libpgn.core.parser.ExceptionBuilder.syntaxException;

class TagPairParser extends AbstractParser implements Parser<List<TagPair>> {
    private final Map<Integer, TagPair> cache;

    TagPairParser(final @Nonnull PgnLexer lexer, Configuration config) {
        super(lexer, config);
        cache = cacheContainer();
    }

    @Override
    public List<TagPair> parse() {
        final List<TagPair> section = new ArrayList<>();

        while (lexer.lastToken() == TP_BEGIN) {
            final TagPair tagPair = parseTagPair();
            if (!config.skipTagPairSection()) {
                section.add(tagPair);
            }
        }

        return section;
    }

    private TagPair parseTagPair() {
        nextNotEqThrow(TP_NAME);

        final String tag = lexer.extract();
        final String value;

        nextNotEqThrow(TP_NAME_VALUE_SEP);
        nextNotEqThrow(TP_VALUE_BEGIN);

        if (lexer.nextToken() == TP_VALUE) {
            value = lexer.extract();

            nextNotEqThrow(TP_VALUE_END);
            nextNotEqThrow(TP_END);
        } else if (lexer.lastToken() == TP_VALUE_END) {
            value = "";
            nextNotEqThrow(TP_END);
        } else {
            throw syntaxException(lexer, TP_VALUE, TP_VALUE_END);
        }

        lexer.nextToken();

        if (!config.cacheTagPair()) {
            return TagPair.of(tag, value);
        }

        return cached(tag, value);
    }

    private int tagPairHashCode(String tag, String value) {
        int result = tag.hashCode();

        return 31 * result + value.hashCode();
    }


    @Nonnull
    private TagPair cached(String tag, String value) {
        final int hashCode = tagPairHashCode(tag, value);

        if (cache.containsKey(hashCode)) {
            return cache.get(hashCode);
        }

        final TagPair tagPair = TagPair.of(tag, value);

        cache.put(hashCode, tagPair);

        return tagPair;
    }

    private Map<Integer, TagPair> cacheContainer() {
        if (!config.cacheTagPair()) {
            return null;
        }

        final ConcurrentLinkedHashMap<Integer, TagPair> cache = new ConcurrentLinkedHashMap.Builder<Integer, TagPair>()
                .maximumWeightedCapacity(config.tagPairCacheSize())
                .build();

        final Set<TagPair> tagPairsCache = config.predefinedCache();
        if (tagPairsCache != null) {
            for (TagPair tagPair : tagPairsCache) {
                cache.put(tagPair.hashCode(), tagPair);
            }
        }

        return cache;
    }

    @Override
    public List<TagPair> tryParse() {
        return Collections.emptyList();
    }
}
