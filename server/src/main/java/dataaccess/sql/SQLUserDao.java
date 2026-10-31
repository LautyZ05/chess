package dataaccess.sql;

import dataaccess.*;
import java.sql.*;
import model.*;

public class SQLUserDao implements UserDao{
    public SQLUserDao() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            String statement = """
                    CREATE TABLE IF NOT EXISTS user (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username))""";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to configure database: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {

    }

    public void createUser(UserData u) throws DataAccessException {

    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}