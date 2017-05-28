package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;
import com.asatryan.libpgn.core.GameFilter;
import com.asatryan.libpgn.core.TagPair;
import com.asatryan.libpgn.core.exception.FilterException;
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
        if (config.skipTagPairSection()) {
            lexer.poll(MOVE_NUMBER);

            return null;
        }

        final List<TagPair> section = new ArrayList<>();

        while (lexer.lastToken() == TP_BEGIN) {
            section.add(parseTagPair());
        }

        GameFilter gameFilter = config.gameFilter();
        if (gameFilter != null && !gameFilter.test(section)) {
            throw new FilterException("Skip this game.");
        }

        return section;
    }

    private TagPair parseTagPair() {
        final String tag = extractNextIfNotEqThrow(TP_NAME);
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

    @Nonnull
    private TagPair cached(String tag, String value) {
        final int hashCode = TagPair.hashCode(tag, value);

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
