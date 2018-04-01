package com.github.nstdio.libpgn.fen;

public final class PieceSignature {
    public static final byte PAWN = 'P';
    public static final byte KING = 'K';
    public static final byte QUEEN = 'Q';
    public static final byte ROOK = 'R';
    public static final byte BISHOP = 'B';
    public static final byte KNIGHT = 'N';

    public static boolean isRook(final byte sig) {
        return sig == PieceSignature.ROOK || sig == PieceSignature.ROOK + 32;
    }

    public static byte signature(final byte sig, final boolean white) {
        return white ? sig : (byte) (sig + 32);
    }
}
