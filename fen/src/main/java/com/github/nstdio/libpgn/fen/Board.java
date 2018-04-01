package com.github.nstdio.libpgn.fen;

import com.github.nstdio.libpgn.common.BiIntConsumer;

import java.io.PrintStream;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

class Board {
    static final int SIZE = 8;
    private static final int FIRST = 0;
    private static final int LAST = SIZE - 1;
    private static final int[][] BISHOP_STEPS = new int[][]{
            {1, 1},
            {-1, 1},
            {1, -1},
            {-1, -1},
    };
    private static final int[][] ROOK_STEPS = new int[][]{
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
    };
    // @formatter:off
    /**
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
    // @formatter:on

    private final byte[][] board;
    private final BiIntConsumer rookListener;

    Board(final BiIntConsumer rookMoveOrCaptureListener) {
        rookListener = Objects.requireNonNull(rookMoveOrCaptureListener);

        board = new byte[SIZE][SIZE];
        board[0] = new byte[]{'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'};
        board[1] = new byte[]{'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'};
        board[2] = new byte[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        board[3] = new byte[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        board[4] = new byte[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        board[5] = new byte[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        board[6] = new byte[]{'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'};
        board[7] = new byte[]{'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};
    }

    Board(final BiIntConsumer rookMoveOrCaptureListener, final byte[][] board) {
        rookListener = Objects.requireNonNull(rookMoveOrCaptureListener);
        this.board = board;
    }

    private static boolean isInBoard(int rank, int file) {
        return rank >= FIRST && rank <= LAST && file >= FIRST && file <= LAST;
    }

    public void castleQueenSide(final boolean whiteMoveNow) {
        castle(2, 3, 0, whiteMoveNow);
    }

    public void castleKingSide(final boolean whiteMoveNow) {
        castle(6, 5, 7, whiteMoveNow);
    }

    private void castle(final int fileMoveKing, final int fileMoveRook, final int fileRemoveRook, final boolean whiteMoveNow) {
        final byte kingSig = (byte) (whiteMoveNow ? 'K' : 'k');
        final byte rookSig = (byte) (whiteMoveNow ? 'R' : 'r');
        final int rank = whiteMoveNow ? 0 : 7;

        if (get(rank, 4) != kingSig) {
            throw new IllegalStateException("Cannot castle. King is moved.");
        }

        if (get(rank, 7) != rookSig) {
            throw new IllegalStateException("Cannot castle. Rook is moved.");
        }

        remove(rank, 4);
        set(rank, fileMoveKing, kingSig);

        remove(rank, fileRemoveRook);
        set(rank, fileMoveRook, rookSig);
    }

    public byte[] rank(final int rank) {
        return board[rank];
    }

    public int rankOf(final int search, final int file, final int startRank, final int direction) {
        for (int i = startRank; isInBoard(i, file); i += direction) {
            if (get(i, file) == search) {
                return i;
            }
        }

        throw new IllegalStateException();
    }

    public int search(final byte[] rank, final byte search, final int from, final int to) {
        for (int i = from; i <= to; i++) {
            if (rank[i] == search) {
                return i;
            }
        }

        return -1;
    }

    public void moveKing(final int rank, final int file, final byte kingSig) {
        final int fromRank;
        final int toRank;

        if (rank == LAST) {
            fromRank = rank;
            toRank = rank - 1;
        } else if (rank == FIRST) {
            fromRank = 0;
            toRank = rank + 1;
        } else {
            fromRank = rank - 1;
            toRank = rank + 1;
        }

        final int startRank = min(fromRank, toRank);
        final int endRank = max(fromRank, toRank);
        final int fromFile = max(FIRST, file - 1);
        final int toFile = min(LAST, file + 1);

        for (int i = startRank; i <= endRank; i++) {
            final int searchedFile = search(board[i], kingSig, fromFile, toFile);
            if (searchedFile != -1) {
                remove(i, searchedFile);
                set(rank, file, kingSig);
                break;
            }
        }
    }

    public void moveKnight(final int rank, final int file, final int fromFile, final byte knightSig) {
        // adjustment
        final int adj = 2;

        for (int r = -adj; r <= adj; r++) {
            for (int f = -adj; f <= adj; f++) {
                if (Math.abs(r * f) == adj && isInBoard(rank + r, file + f)) {
                    final int adjRank = rank + r;
                    final int adjFile = file + f;
                    if (fromFile != -1 && fromFile != adjFile) {
                        continue;
                    }

                    if (get(adjRank, adjFile) == knightSig) {
                        remove(adjRank, adjFile);
                        set(rank, file, knightSig);
                        return;
                    }
                }
            }
        }

        throw cannotMove("Knight", rank, file);
    }

    public void moveBishop(final int rank, final int file, final byte bishopSig) {
        if (!findAndSet(rank, file, -1, bishopSig, BISHOP_STEPS)) {
            throw cannotMove("Bishop", rank, file);
        }
    }

    private boolean findAndSet(final int rank, final int file, final int fromFile, final byte pieceSig, final int[][] steps) {
        for (final int[] step : steps) {
            for (int i = rank + step[0], j = file + step[1]; isInBoard(i, j); i += step[0], j += step[1]) {
                if (fromFile != -1 && fromFile != j) {
                    continue;
                }

                if (get(i, j) == pieceSig) {
                    remove(i, j);
                    set(rank, file, pieceSig);
                    return true;
                }

                if (get(i, j) != '\0') {
                    break;
                }
            }
        }

        return false;
    }

    public void remove(final int rank, final int file) {
        set(rank, file, (byte) '\0');
    }

    public void set(final int rank, final int file, final byte value) {
        if (PieceSignature.isRook(get(rank, file))) {
            rookListener.accept(rank, file);
        }

        board[rank][file] = value;
    }

    public byte get(final int rank, final int file) {
        return board[rank][file];
    }

    public void print(final PrintStream stream) {
        for (int i = SIZE - 1; i >= 0; i--) {
            // the rank number.
            stream.print(i + 1);
            stream.print(' ');

            for (int j = 0; j < SIZE; j++) {
                final byte b = board[i][j];
                stream.append(b == '\0' ? '.' : (char) b)
                        .append(' ');
            }

            stream.println();
        }

        stream.append("  ");

        for (int i = 0; i < SIZE; i++) {
            stream.print((char) (i + 97));
            stream.append(' ');
        }

        stream.println();
    }

    private IllegalStateException cannotMove(final String piece, final int rank, final int file) {
        return new IllegalStateException(String.format("Cannot find %s that can move to [%d, %d]", piece, rank, file));
    }

    public void moveQueen(final int rank, final int file, final byte queenSig) {
        final boolean find1 = findAndSet(rank, file, -1, queenSig, BISHOP_STEPS);
        final boolean find2 = findAndSet(rank, file, -1, queenSig, ROOK_STEPS);
        if (!(find1 || find2)) {
            throw cannotMove("Queen", rank, file);
        }
    }

    public void moveRook(final int rank, final int file, final int fromFile, final byte rookSig) {
        if (!(findAndSet(rank, file, fromFile, rookSig, ROOK_STEPS))) {
            throw cannotMove("Rook", rank, file);
        }
    }
}
