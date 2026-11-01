package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import model.*;

public class SQLGameDao implements GameDao{
    public SQLGameDao() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            String statement = """
                    CREATE TABLE IF NOT EXISTS game (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(255) DEFAULT NULL,
                    blackUsername VARCHAR(255) DEFAULT NULL,
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (gameID))""";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to configure database: " + e.getMessage());
        }
    }

    public GameData createGame(GameData game) throws DataAccessException{
        String json = new Gson().toJson(game.game());
        int id = updateDatabase( "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)", game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")){
                preparedStatement.setInt(1, gameID);
                try (var resSet = preparedStatement.executeQuery()){
                    if (resSet.next()){
                        return readGame(resSet);
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to read data: " + e.getMessage());
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return List.of();
    }

    public void updateGame(GameData game) throws DataAccessException{

    }

    public void clear() throws DataAccessException{
        updateDatabase("TRUNCATE game");
    }

    private GameData readGame(ResultSet resSet) throws SQLException{
        ChessGame chessGame = new Gson().fromJson(resSet.getString("game"), ChessGame.class);
        return new GameData(resSet.getInt("gameID"), resSet.getString("whiteUsername"), resSet.getString("blackUsername"), resSet.getString("gameName"), chessGame);
    }


    private int updateDatabase(String statement, Object... params) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement prepState = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (int i = 0; i < params.length; i++){
                    if (params[i] instanceof String s){
                        prepState.setString(i + 1, s);
                    }
                    else if (params[i] instanceof Integer in){
                        prepState.setInt(i + 1, in);
                    }
                    else if (params[i] == null){
                        prepState.setNull(i + 1, Types.NULL);
                    }
                }
                prepState.executeUpdate();
                try (ResultSet resSet = prepState.getGeneratedKeys()){
                    return resSet.next() ? resSet.getInt(1) : 0;
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to update database: " + e.getMessage());
        }
    }

}