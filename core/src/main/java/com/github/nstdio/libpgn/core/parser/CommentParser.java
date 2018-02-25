package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;
import com.github.nstdio.libpgn.core.internal.ArrayUtils;

import java.util.Arrays;

import static com.github.nstdio.libpgn.core.Configuration.COMMENT_LENGTH_UNLIMITED;
import static com.github.nstdio.libpgn.core.TokenTypes.*;

class CommentParser extends AbstractParser implements Parser<byte[]> {

    CommentParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    @Override
    public byte[] parse() {
        if (lexer.next() == COMMENT_END) { // {} empty comment
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        lastNotEqThrow(COMMENT);

        final byte[] comment = config.trimComment() ? read().trim().getBytes() : readBytes();

        nextNotEqThrow(COMMENT_END);

        return comment;
    }

    @Override
    public byte[] tryParse() {
        byte[] comment = null;
        if (lexer.last() == COMMENT_BEGIN) {
            comment = parse();
            if (lexer.next() == COMMENT_BEGIN) {
                comment = ArrayUtils.concat(comment, tryParse());
            }
        }

        if (comment != null && config.commentMaxLength() != COMMENT_LENGTH_UNLIMITED &&
                comment.length > config.commentMaxLength()) {

            return Arrays.copyOf(comment, config.commentMaxLength());
        }

        return config.skipComment() ? null : comment;
    }
}
