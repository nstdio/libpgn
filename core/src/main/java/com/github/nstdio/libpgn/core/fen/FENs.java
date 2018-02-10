package com.github.nstdio.libpgn.core.fen;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.github.nstdio.libpgn.core.fen.Board.SIZE;
import static java.lang.Character.getNumericValue;

public final class FENs {
    private static final int STATE_BOARD = 0;
    private static final int STATE_MOVE_TURN = 1;
    private static final int STATE_CASTLING = 2;
    private static final int STATE_EN_PASSANT = 3;
    private static final int STATE_HALF_MOVE_CLOCK = 4;
    private static final int STATE_MOVE = 5;
    private static final int STATE_FINISHED = 6;

    private static final Pattern MOVE_NUMBER = Pattern.compile("\\d+\\.\\s?");

    private FENs() {
    }

    public static String ofPlainPgn(final String pgnStr, final int limit) {
        return new FEN().move(getMoves(pgnStr), limit);
    }

    public static String ofPlainPgn(final String pgnStr) {
        final List<String> moves = getMoves(pgnStr);
        return new FEN().move(moves, moves.size());
    }

    public static FEN initial() {
        return of("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public static FEN of(final String fen) {
        Objects.requireNonNull(fen, "Input fen string cannot be null");

        final byte[][] board = new byte[SIZE][SIZE];
        int castle = FEN.C_NONE, inPassingRank = 0, inPassingFile = 0, halfMoveClock = 0, move = 0;
        boolean white = true;
        int state = STATE_BOARD;
        int len = fen.length();
        int rank = 7, i = 0;

        try {
            while (i < len) {
                switch (state) {
                    case STATE_BOARD: {
                        int idx = 0;
                        char c;
                        while ((c = fen.charAt(i++)) != '/') {
                            if (rank == -1) {
                                throw new IllegalArgumentException("Attempt to parse more than 8 rows.");
                            }

                            if (c == ' ') {
                                state = STATE_MOVE_TURN;
                                break;
                            }

                            if (idx >= 8) {
                                throw illegalCharacter(c, i - 1);
                            }

                            if (Character.isDigit(c)) {
                                // shifting array index position.
                                idx += getNumericValue(c);
                            } else {
                                switch (c) {
                                    case 'P':
                                    case 'p':
                                    case 'k':
                                    case 'K':
                                    case 'q':
                                    case 'Q':
                                    case 'r':
                                    case 'R':
                                    case 'b':
                                    case 'B':
                                    case 'n':
                                    case 'N':
                                        board[rank][idx++] = (byte) c;
                                        break;
                                    default:
                                        throw illegalCharacter(c, i - 1);
                                }
                            }
                        }
                        rank--;
                    }
                    break;
                    case STATE_MOVE_TURN: {
                        // checking that we read 8 rows
                        if (rank != -1) {
                            throw new IllegalArgumentException(String.format("8 rows were expected but actual parsed %d", 7 - rank));
                        }

                        char c = fen.charAt(i++);
                        if (c != 'w' && c != 'b') {
                            throw illegalCharacter(c, i - 1);
                        }

                        white = c == 'w';
                        state = STATE_CASTLING;
                    }
                    break;
                    case STATE_CASTLING: {
                        if (fen.charAt(++i) == '-') {
                            ++i;
                            castle = FEN.C_NONE;
                            state = STATE_EN_PASSANT;
                        } else {
                            char c;
                            while ((c = fen.charAt(i++)) != ' ' && i < len) {
                                switch (c) {
                                    case 'K':
                                        castle |= FEN.C_WHITE_KING;
                                        break;
                                    case 'Q':
                                        castle |= FEN.C_WHITE_QUEEN;
                                        break;
                                    case 'k':
                                        castle |= FEN.C_BLACK_KING;
                                        break;
                                    case 'q':
                                        castle |= FEN.C_BLACK_QUEEN;
                                        break;
                                    default:
                                        throw illegalCharacter(c, i - 1);
                                }
                            }
                            state = STATE_EN_PASSANT;
                        }
                    }
                    break;
                    case STATE_EN_PASSANT:
                        if (castle == FEN.C_NONE) {
                            // skipping whitespace
                            ++i;
                        }

                        if (fen.charAt(i) == '-') {
                            ++i;
                        } else {
                            inPassingFile = FEN.fileToIdx(fen.charAt(i));
                            if (inPassingFile < 0 || inPassingFile > 7) {
                                throw illegalCharacter(fen.charAt(i), i);
                            }

                            inPassingRank = FEN.rankIdx(fen.charAt(++i));

                            if (inPassingRank != 2 && inPassingRank != 5) {
                                throw illegalCharacter(fen.charAt(i), i);
                            }

                            ++i;
                        }
                        state = STATE_HALF_MOVE_CLOCK;
                        break;
                    case STATE_HALF_MOVE_CLOCK:
                        if (!Character.isDigit(fen.charAt(++i))) {
                            throw illegalCharacter(fen.charAt(i), i);
                        }

                        halfMoveClock = getNumericValue(fen.charAt(i));
                        state = STATE_MOVE;
                        break;
                    case STATE_MOVE:
                        ++i;
                        if (!Character.isDigit(fen.charAt(++i))) {
                            throw illegalCharacter(fen.charAt(i), i);
                        }

                        move = getNumericValue(fen.charAt(i));
                        state = STATE_FINISHED;
                        break;
                    case STATE_FINISHED:
                        i = len;
                        break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unexpected end of input", e);
        }

        return new FEN(board, white, castle, inPassingFile, inPassingRank, halfMoveClock, move);
    }

    private static IllegalArgumentException illegalCharacter(final char c, final int index) {
        return new IllegalArgumentException(String.format("Unexpected character %s[%c] at index %d", Character.getName(c), c, index));
    }

    @Nonnull
    private static List<String> getMoves(final String str) {
        return Arrays.asList(MOVE_NUMBER.matcher(str).replaceAll("").split("\\s+"));
    }
}
