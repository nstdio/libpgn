package com.asatryan.libpgn.core.parser;

import com.asatryan.libpgn.core.Configuration;

import javax.annotation.Nonnull;

import static com.asatryan.libpgn.core.Configuration.COMMENT_LENGTH_UNLIMITED;
import static com.asatryan.libpgn.core.TokenTypes.*;

class CommentParser extends AbstractParser implements Parser<String> {

    CommentParser(@Nonnull PgnLexer lexer, @Nonnull Configuration config) {
        super(lexer, config);
    }

    @Override
    public String parse() {
        if (lexer.nextToken() == COMMENT_END) { // {} empty comment
            return "";
        }

        lastNotEqThrow(COMMENT);

        final String comment = config.trimComment() ? lexer.extract().trim() : lexer.extract();

        nextNotEqThrow(COMMENT_END);

        return comment;
    }

    @Override
    public String tryParse() {
        String comment = null;
        if (lexer.lastToken() == COMMENT_BEGIN) {
            comment = parse();
            if (lexer.nextToken() == COMMENT_BEGIN) {
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

        return comment;
    }
}
