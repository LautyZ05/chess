package dataaccess.sql;

import dataaccess.*;
import java.sql.*;

public class SQLUpdateDatabase {

    static int updateDatabase(String statement, Object... params) throws DataAccessException{
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
                try (ResultSet resSet = prepState.getGeneratedKeys()) {
                    return resSet.next() ? resSet.getInt(1) : 0;
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException("unable to update database: " + e.getMessage());
        }
    }
}
