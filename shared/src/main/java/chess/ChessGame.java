package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        List<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move: possibleMoves){
            ChessBoard testBoard = copyBoard(board);
            applyMoveToBoard(move, testBoard);

            ChessBoard originalBoard = board;
            board = testBoard;

            boolean leavesKingInCheck = isInCheck(piece.getTeamColor());
            board = originalBoard;

            if (!leavesKingInCheck){
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private ChessBoard copyBoard(ChessBoard source){
        ChessBoard copy = new ChessBoard();

        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = source.getPiece(position);

                if (piece != null){
                    copy.addPiece(position, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return copy;
    }

    private void applyMoveToBoard(ChessMove move, ChessBoard targetBoard) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece movingPiece = targetBoard.getPiece(start);

        ChessPiece pieceToPlace = movingPiece;
        if (move.getPromotionPiece() != null){
            pieceToPlace = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }

        targetBoard.addPiece(end, pieceToPlace);
        targetBoard.addPiece(start, null);
    }


    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null){
            throw new InvalidMoveException("No piece at start position");
        }
        if (piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("It is not that team's turn");
        }

        Collection<ChessMove> legalMoves = validMoves(start);

        if (legalMoves == null || !legalMoves.contains(move)){
            throw new InvalidMoveException("Move is not legal");
        }

        applyMoveToBoard(move, board);
        teamTurn = (teamTurn == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPiece occupant = board.getPiece(new ChessPosition(row, col));
                if (occupant == null) {
                    continue;
                }
                if (occupant.getPieceType() == ChessPiece.PieceType.KING){
                    if (occupant.getTeamColor() == teamColor) {
                        return new ChessPosition(row, col);
                    }
                }
            }
        }
        return null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opponent = (teamColor == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingPosition = findKing(teamColor);

        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(position);

                if (occupant == null || occupant.getTeamColor() != opponent){
                    continue;
                }

                Collection<ChessMove> moves = occupant.pieceMoves(board, position);
                for (ChessMove move: moves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }

        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() != teamColor){
                    continue;
                }

                Collection<ChessMove> moves = validMoves(position);
                if (moves != null && !moves.isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return 71 * Objects.hash(board, teamTurn);
    }
}