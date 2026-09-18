package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List; //imported following video
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private int[][] diagonol_moves = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
    private int[][] straight_moves = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    private Boolean onBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private List<ChessMove> sliding_moves(ChessBoard board, ChessPosition myPosition, int[][] dirs) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int[] dir : dirs) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            while (onBoard(row, col)) {
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(target);

                if (occupant == null) {
                    moves.add(new ChessMove(myPosition, target, null));
                }
                else {
                    if (occupant.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            return sliding_moves(board, myPosition, diagonol_moves);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            return sliding_moves(board, myPosition, straight_moves);
        }
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor.equals(that.pieceColor) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return 71 * Objects.hashCode(pieceColor) + Objects.hashCode(type);
    }
}
