package dataaccess;

import model.*;

public interface UserDao {
    void clear() throws DataAccessException;

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}