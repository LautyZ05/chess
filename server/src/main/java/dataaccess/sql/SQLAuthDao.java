package dataaccess.sql;

import dataaccess.*;
import java.sql.*;
import model.*;
import static dataaccess.sql.SQLUpdateDatabase.updateDatabase;

public class SQLAuthDao implements AuthDao{
    public SQLAuthDao() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            String statement = """
                    CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(255) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    PRIMARY KEY (authToken))""";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to configure database: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        updateDatabase("TRUNCATE auth");
    }

    public void createAuth(AuthData auth) throws DataAccessException{
        updateDatabase("INSERT INTO auth (authToken, username) VALUES (?, ?)", auth.authToken(), auth.username());
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")){
                preparedStatement.setString(1, authToken);
                try (var resSet = preparedStatement.executeQuery()){
                    if (resSet.next()){
                        return new AuthData(resSet.getString("authToken"), resSet.getString("username"));
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to read data: " + e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        updateDatabase("DELETE FROM auth WHERE authToken=?", authToken);
    }

}