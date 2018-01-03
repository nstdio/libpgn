package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.Configuration;

import static com.github.nstdio.libpgn.core.Configuration.COMMENT_LENGTH_UNLIMITED;
import static com.github.nstdio.libpgn.core.TokenTypes.*;

class CommentParser extends AbstractParser implements Parser<String> {

    CommentParser(PgnLexer lexer, Configuration config) {
        super(lexer, config);
    }

    @Override
    public String parse() {
        if (lexer.next() == COMMENT_END) { // {} empty comment
            return "";
        }

        lastNotEqThrow(COMMENT);

        final String comment = config.trimComment() ? read().trim() : read();

        nextNotEqThrow(COMMENT_END);

        return comment;
    }

    @Override
    public String tryParse() {
        String comment = null;
        if (lexer.last() == COMMENT_BEGIN) {
            comment = parse();
            if (lexer.next() == COMMENT_BEGIN) {
                final String nextComment = tryParse();
                if (nextComment != null) {
                    comment += nextComment;
                }
            }
        }

        if (comment != null && config.commentMaxLength() != COMMENT_LENGTH_UNLIMITED &&
                comment.length() > config.commentMaxLength()) {

            return comment.substring(0, config.commentMaxLength());
        }

        return config.skipComment() ? null : comment;
    }
}
