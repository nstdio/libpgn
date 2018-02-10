package com.github.nstdio.libpgn.core.fen;

import java.util.List;
import java.util.Objects;

import static com.github.nstdio.libpgn.core.fen.PieceSignature.*;

public class FEN {
    /**
     * Castling availabilities.
     */
    public static final int C_NONE = 0;
    public static final int C_WHITE_KING = 1;
    public static final int C_WHITE_QUEEN = 1 << 1;
    public static final int C_BLACK_KING = 1 << 2;
    public static final int C_BLACK_QUEEN = 1 << 3;
    public static final int C_ALL = C_WHITE_KING | C_WHITE_QUEEN | C_BLACK_KING | C_BLACK_QUEEN;

    private final Board board;
    private final StringBuilder sb = new StringBuilder(64);

    private boolean whiteMove;
    private int castle;
    private int inPassingRank;
    private int inPassingFile;
    private int halfMoveClock;
    private int captureIdx;
    private int rankToMove;
    private int fileToMove;
    private int fromFile;
    private int move;

    FEN() {
        /*
         * 7 8 r n b q k b n r
         * 6 7 p p p p p p p p
         * 5 6 . . . . . . . .
         * 4 5 . . . . . . . .
         * 3 4 . . . . . . . .
         * 2 3 . . . . . . . .
         * 1 2 P P P P P P P P
         * 0 1 R N B Q K B N R
         *     a b c d e f g h
         *     0 1 2 3 4 5 6 7
         */
        this.board = new Board(this::rookMoveHandler);
        this.whiteMove = true;
        this.castle = C_ALL;
    }

    FEN(final byte[][] board,
        final boolean whiteMove,
        final int castle,
        final int inPassingFile,
        final int inPassingRank,
        final int halfMoveClock,
        final int move) {

        this.board = new Board(this::rookMoveHandler, Objects.requireNonNull(board));
        this.whiteMove = whiteMove;

        // TODO: 2/4/18 validate
        this.castle = castle;
        this.inPassingFile = inPassingFile;
        this.inPassingRank = inPassingRank;
        this.halfMoveClock = halfMoveClock;
        this.move = move == 1 ? 0 : move;
    }

    private static boolean isPawnMove(final String move) {
        return between(move.charAt(0), 97, 104) &&
                (between(move.charAt(1), 49, 56) || move.charAt(1) == 'x');
    }

    private static boolean between(final int check, final int low, final int high) {
        return check >= low && check <= high;
    }

    public static int fileToIdx(final int f) {
        return (f - 104) + 7;
    }

    private static char idxToFile(final int idx) {
        return (char) ((idx + 104) - 7);
    }

    private static boolean isKingSideCastle(final String move) {
        return "O-O".equals(move);
    }

    private static boolean isQueenSideCastle(final String move) {
        return "O-O-O".equals(move);
    }

    private static int captureFileIdx(final String move, final int captureIdx) {
        return fileToIdx(move.charAt(captureIdx + 1));
    }

    public static int rankIdx(final int r) {
        return Character.getNumericValue(r) - 1;
    }

    private void rookMoveHandler(final int rank, final int file) {
        // TODO: 2/4/18 rewrite
        if (rank == 0) {
            if (file == 7) {
                disqualifyCasting(C_WHITE_KING);
            } else if (file == 0) {
                disqualifyCasting(C_WHITE_QUEEN);
            }
        } else if (rank == 7) {
            if (file == 7) {
                disqualifyCasting(C_BLACK_KING);
            } else if (file == 0) {
                disqualifyCasting(C_BLACK_QUEEN);
            }
        }
    }

    public Board board() {
        return board;
    }

    public String move(final List<String> moves) {
        return move(moves, moves.size());
    }

    public String move(final String move) {
        moveInternal(move, whiteMove);

        return buildString(++this.move, whiteMove = !whiteMove);
    }

    public String move(final List<String> moves, final int size) {
        boolean whiteMoveFlag = whiteMove;

        for (int i = move; i < size; i++, move++) {
            whiteMoveFlag = i % 2 == 1;
            moveInternal(moves.get(i), !whiteMoveFlag);
        }

        return buildString(size, whiteMoveFlag);
    }

    private String buildString(final int move, final boolean whiteMoveFlag) {
        // resetting internal buffer.
        sb.setLength(0);

        appendRanks();

        sb.append(' ')
                .append(whiteMoveFlag ? 'w' : 'b')
                .append(' ');

        appendCastling();

        sb.append(' ');

        if (isInPassing()) {
            sb.append(idxToFile(inPassingFile)).append(inPassingRank + 1);
        } else {
            sb.append('-');
        }

        sb.append(' ')
                .append(halfMoveClock)
                .append(' ')
                .append(move == 0 ? 1 : (move / 2) + 1);

        return sb.toString();
    }

    private void moveInternal(final String move, final boolean whiteMoveNow) {
        if (move == null || move.length() == 0) {
            throw new IllegalArgumentException();
        }

        captureIdx = move.indexOf('x');

        if (isPawnMove(move)) {
            pawn(move, whiteMoveNow);
            halfMoveClock = 0;
        } else if (isKingSideCastle(move)) {
            halfMoveClock++;
            disqualifyCasting(whiteMoveNow);
            board.castleKingSide(whiteMoveNow);
            resetInPassing();
        } else if (isQueenSideCastle(move)) {
            halfMoveClock++;
            disqualifyCasting(whiteMoveNow);
            board.castleQueenSide(whiteMoveNow);
            resetInPassing();
        } else {
            resetInPassing();
            switch (move.charAt(0)) {
                case KING:
                    king(move, signature(KING, whiteMoveNow));
                    disqualifyCasting(whiteMoveNow);
                    break;
                case KNIGHT:
                    knight(move, signature(KNIGHT, whiteMoveNow));
                    break;
                case BISHOP:
                    bishop(move, signature(BISHOP, whiteMoveNow));
                    break;
                case QUEEN:
                    queen(move, signature(QUEEN, whiteMoveNow));
                    break;
                case ROOK:
                    rook(move, signature(ROOK, whiteMoveNow));
                    break;
                default:
                    throw new IllegalStateException(String.format("Cannot determine the move: %s", move));
            }
        }
    }

    private void appendCastling() {
        if (castle == C_NONE) {
            sb.append('-');

            return;
        }

        if ((castle & C_WHITE_KING) == C_WHITE_KING)
            sb.append('K');

        if ((castle & C_WHITE_QUEEN) == C_WHITE_QUEEN)
            sb.append('Q');

        if ((castle & C_BLACK_KING) == C_BLACK_KING)
            sb.append('k');

        if ((castle & C_BLACK_QUEEN) == C_BLACK_QUEEN)
            sb.append('q');
    }

    private void queen(final String move, final byte search) {
        assignIndices(move);
        board.moveQueen(rankToMove, fileToMove, search);
    }

    private void bishop(final String move, final byte search) {
        assignIndices(move);
        board.moveBishop(rankToMove, fileToMove, search);
    }

    private void knight(final String move, final byte search) {
        assignIndices(move);
        board.moveKnight(rankToMove, fileToMove, fromFile, search);
    }

    private void rook(final String move, final byte search) {
        assignIndices(move);
        board.moveRook(rankToMove, fileToMove, fromFile, search);
    }

    private void king(final String move, final byte search) {
        assignIndices(move);
        board.moveKing(rankToMove, fileToMove, search);
    }

    private void assignIndices(final String move) {
        int offset = captureIdx == -1 ? 0 : 1;
        boolean ff = false;
        if ((between(move.charAt(1), 'a', 'h') && between(move.charAt(2), 'a', 'h')) || move.charAt(2) == 'x') {
            offset++;
            ff = true;
        }

        rankToMove = rankIdx(move.charAt(2 + offset));
        fileToMove = fileToIdx(move.charAt(1 + offset));
        fromFile = ff ? fileToIdx(move.charAt(1)) : -1;

        if (captureIdx == -1) {
            halfMoveClock++;
        } else {
            halfMoveClock = 0;
        }
    }

    private void disqualifyCasting(final boolean white) {
        disqualifyCasting(white ? C_WHITE_KING | C_WHITE_QUEEN : C_BLACK_KING | C_BLACK_QUEEN);
    }

    private void disqualifyCasting(final int flag) {
        castle &= ~flag;
    }

    private void pawn(final String move, final boolean whiteMoveNow) {
        int search = signature(PAWN, whiteMoveNow);
        final int file = fileToIdx(move.charAt(0));
        final int rank;

        if (captureIdx == -1) {
            // was not capture move.
            rankToMove = Character.getNumericValue(move.charAt(1)) - 1;
            fileToMove = file;
            rank = board.rankOf(search, file, rankToMove, whiteMoveNow ? -1 : 1);
        } else {
            fileToMove = captureFileIdx(move, captureIdx);
            rankToMove = Character.getNumericValue(move.charAt(captureIdx + 2)) - 1;
            rank = whiteMoveNow ? rankToMove - 1 : rankToMove + 1;
        }

        final int promotionIdx = move.indexOf('=');

        if (promotionIdx != -1) {
            final char piece = move.charAt(promotionIdx + 1);
            search = whiteMoveNow ? Character.toUpperCase(piece) : Character.toLowerCase(piece);
        }

        if (inPassingRank == rankToMove && inPassingFile == fileToMove) {
            board.remove(whiteMoveNow ? inPassingRank - 1 : inPassingRank + 1, inPassingFile);
            resetInPassing();
        }

        // remove pawn from old square.
        board.remove(rank, file);
        // setting the moved pawn on its new place.
        board.set(rankToMove, fileToMove, (byte) search);

        if (Math.abs(rank - rankToMove) == 2) {
            inPassingFile = file;
            inPassingRank = whiteMoveNow ?
                    rankToMove - 1 :
                    // we flip the board to view from the blacks perspective
                    rankToMove + 1;
        } else {
            resetInPassing();
        }
    }

    private void resetInPassing() {
        inPassingFile = 0;
        inPassingRank = 0;
    }

    private boolean isInPassing() {
        return inPassingFile != 0 && inPassingRank != 0;
    }

    private void appendRanks() {
        for (int i = 7; i >= 0; i--) {
            int empty = 0;

            for (final byte rank : board.rank(i)) {
                if (rank == 0) {
                    empty++;
                } else {
                    if (empty != 0) {
                        sb.append(empty);
                    }

                    sb.append((char) rank);

                    empty = 0;
                }
            }

            if (empty != 0) {
                sb.append(empty);
            }

            if (i > 0) {
                sb.append('/');
            }
        }
    }

}
