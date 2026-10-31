package dataaccess;

import model.*;

public interface AuthDao {
    void clear() throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}