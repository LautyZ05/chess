package dataaccess.sql;

import dataaccess.*;
import java.sql.*;
import model.*;

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

    }

    public void createAuth(AuthData a) throws DataAccessException{

    }

    public AuthData getAuth(String token) throws DataAccessException{
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException{

    }
}