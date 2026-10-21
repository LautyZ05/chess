package dataaccess;

import model.*;

public interface AuthDao {
    void clear() throws DataAccessException;

    void createAuth(AuthData a) throws DataAccessException;

    AuthData getAuth(String token) throws DataAccessException;

    void deleteAuth(String token) throws DataAccessException;
}