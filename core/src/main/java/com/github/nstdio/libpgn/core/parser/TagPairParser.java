package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.GameFilter;
import com.github.nstdio.libpgn.core.TagPair;
import com.github.nstdio.libpgn.core.exception.FilterException;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.nstdio.libpgn.core.TokenTypes.*;
import static com.github.nstdio.libpgn.core.parser.ExceptionBuilder.syntaxException;

class TagPairParser extends AbstractParser implements Parser<List<TagPair>> {
    private final Map<Integer, TagPair> cache;
    private final GameFilter gameFilter;

    TagPairParser(final PgnLexer lexer, final Configuration config) {
        super(lexer, config);
        cache = cacheContainer();
        gameFilter = config.gameFilter();
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

        return config.cacheTagPair() ? cached(tag, value) : TagPair.of(tag, value);
    }

    @Nonnull
    private TagPair cached(final String tag, final String value) {
        final int hashCode = TagPair.hashCode(tag, value);

        return cache.containsKey(hashCode) ?
                cache.get(hashCode) :
                cache.put(hashCode, TagPair.of(tag, value));
    }

    private Map<Integer, TagPair> cacheContainer() {
        if (!config.cacheTagPair()) {
            return null;
        }

        final ConcurrentLinkedHashMap<Integer, TagPair> cache = new ConcurrentLinkedHashMap.Builder<Integer, TagPair>()
                .maximumWeightedCapacity(config.tagPairCacheSize())
                .build();

        Optional.ofNullable(config.predefinedCache())
                .filter(preDefTagPairs -> preDefTagPairs.size() > 0)
                .ifPresent(preDefTagPairs -> preDefTagPairs.forEach(tagPair -> cache.put(tagPair.hashCode(), tagPair)));

        return cache;
    }

    @Override
    public List<TagPair> tryParse() {
        throw new RuntimeException("Not implemented yet.");
    }
}
