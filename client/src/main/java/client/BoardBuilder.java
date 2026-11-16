package client;

import chess.*;
import static ui.EscapeSequences.*;

public class BoardBuilder {

    public static String printBoard(ChessBoard board, boolean whitePerspective){
        StringBuilder boardData = new StringBuilder();
        boardData.append(letterRow(whitePerspective));
        int row;
        for (int i = 0; i < 8; i++){
            if (whitePerspective){
                row = 8 - i;
            }
            else {
                row = 1 + i;
            }
            boardData.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE).append(" ")
                    .append(row).append(" ").append(RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int j = 0; j < 8; j++){
                int col;
                if (whitePerspective){
                    col = j + 1;
                }
                else {
                    col = 8 - j;
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                boardData.append(chessPattern(row, col)).append(getChessPiece(piece)).append(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            boardData.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE).append(" ").append(row)
                    .append(" ").append(RESET_BG_COLOR + RESET_TEXT_COLOR).append("\n");
        }
        boardData.append(letterRow(whitePerspective));
        return boardData.toString();
    }

    private static String chessPattern(int row, int col){
        if ((row + col) % 2 == 1){
            return SET_BG_COLOR_WHITE;
        }
        else {
            return SET_BG_COLOR_BLACK;
        }
    }

    private static String getChessPiece(ChessPiece piece){
        if (piece == null){
            return "   ";
        }
        String letter = "";
        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            letter = "K";
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
            letter = "Q";
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            letter = "B";
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
            letter = "N";
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
            letter = "R";
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
            letter = "P";
        }
        String color;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            color = SET_TEXT_COLOR_RED;
        }
        else{
            color = SET_TEXT_COLOR_BLUE;
        }
        return color + " " + letter + " ";
    }

    private static String letterRow(boolean whitePerspective){
        StringBuilder row = new StringBuilder(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE).append("   ");
        int col;
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (int i = 0; i < 8; i++){
            if (whitePerspective){
                col = i + 1;
            }
            else {
                col = 8 - i;
            }
            row.append(" ").append(letters[col-1]).append(" ");
        }
        row.append("   ").append(RESET_BG_COLOR + RESET_TEXT_COLOR).append("\n");
        return row.toString();
    }
}
