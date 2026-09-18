package chess;

import java.util.Arrays;
import java.util.Objects;
import java.util.HashMap;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8]; //check again URGENT

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //throw new RuntimeException("Not implemented");
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }

        ChessPiece.PieceType[] beginning_row = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, beginning_row[col-1]));
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, beginning_row[col-1]));
        }
    }

    static HashMap<ChessPiece.PieceType, String> symbols = new HashMap<>();
    static {
        symbols.put(ChessPiece.PieceType.KING, "k");
        symbols.put(ChessPiece.PieceType.QUEEN, "q");
        symbols.put(ChessPiece.PieceType.BISHOP, "b");
        symbols.put(ChessPiece.PieceType.KNIGHT, "n");
        symbols.put(ChessPiece.PieceType.ROOK, "r");
        symbols.put(ChessPiece.PieceType.PAWN, "p");
    }

    private String pieceSymbol(ChessPiece piece) {
        String letter = symbols.get(piece.getPieceType());

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return letter.toUpperCase();
        }
        else {
            return letter;
        }
    }

    @Override
    public String toString() {
        String result = "";

        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col ++) {
                ChessPiece piece = board[row-1][col-1];

                String symbol;
                if (piece == null) {
                    symbol = "|.";
                }
                else {
                    symbol = "|" + pieceSymbol(piece);
                }

                result += symbol;
            }
            result += "|\n";
        }
        return result;
    }
}
