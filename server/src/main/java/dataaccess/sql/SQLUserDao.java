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

    public void createUser(UserData user) throws DataAccessException{
        updateDatabase("INSERT INTO user (username, password, email) VALUES (?, ?, ?)", user.username(), user.password(), user.email());
    }

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")){
                preparedStatement.setString(1, username);
                try (var resSet = preparedStatement.executeQuery()){
                    if (resSet.next()){
                        return new UserData(resSet.getString("username"), resSet.getString("password"), resSet.getString("email"));
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to read data: " + e.getMessage());
        }
        return null;
    }

    public void clear() throws DataAccessException {

    }

    private void updateDatabase(String statement, Object... params) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement prepState = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (int i = 0; i < params.length; i++){
                    if (params[i] instanceof String s) {
                        prepState.setString(i + 1, s);
                    }
                    else if (params[i] instanceof Integer in){
                        prepState.setInt(i + 1, in);
                    }
                    else if (params[i] == null) {
                        prepState.setNull(i + 1, Types.NULL);
                    }
                }
                prepState.executeUpdate();
                try (ResultSet resSet = prepState.getGeneratedKeys()){
                    if (resSet.next()) {
                        resSet.getInt(1);
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to update database: " + e.getMessage());
        }
    }
}