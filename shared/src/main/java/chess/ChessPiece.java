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
    private int[][] knight_moves = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};

    private void addPawnMoves(List<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow) {
        if (end.getRow() == promotionRow) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        }
        else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private List<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int oneStepRow = row + direction;
        if (onBoard(oneStepRow, col) && board.getPiece(new ChessPosition(oneStepRow, col)) == null) {
            addPawnMoves(moves, myPosition, new ChessPosition(oneStepRow, col), promotionRow);

            int twoStepRow = row + 2 * direction;
            if (row == startRow && board.getPiece(new ChessPosition(twoStepRow, col)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(twoStepRow, col), null));
            }
        }

        for (int change : new int[]{-1, 1}) {
            int captureCol = col + change;

            if (onBoard(oneStepRow, captureCol)) {
                ChessPosition target = new ChessPosition(oneStepRow, captureCol);
                ChessPiece occupant = board.getPiece(target);

                if (occupant != null && occupant.getTeamColor() != this.pieceColor) {
                    addPawnMoves(moves, myPosition, target, promotionRow);
                }
            }
        }

        return moves;
    }

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

    private List<ChessMove> stepping_moves(ChessBoard board, ChessPosition myPosition, int [][] offsets) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int[] offset : offsets) {
            int row = startRow + offset[0];
            int col = startCol + offset[1];

            if (onBoard(row, col)) {
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(target);

                if (occupant == null || occupant.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, target, null));
                }
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
        if (piece.getPieceType() == PieceType.QUEEN) {
            List<ChessMove> queenMoves = new ArrayList<>(sliding_moves(board, myPosition, diagonol_moves));
            queenMoves.addAll(sliding_moves(board, myPosition, straight_moves));
            return queenMoves;
        }
        if (piece.getPieceType() == PieceType.KING) {
            List<ChessMove> kingMoves = new ArrayList<>(stepping_moves(board, myPosition, diagonol_moves));
            kingMoves.addAll(stepping_moves(board, myPosition, straight_moves));
            return kingMoves;
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            return stepping_moves(board, myPosition, knight_moves);
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
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
