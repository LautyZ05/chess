package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.*;

import java.util.UUID;

public class UserService {
    private UserDao users;
    private AuthDao auths;

    public UserService(UserDao users, AuthDao auths){
        this.users = users;
        this.auths = auths;
    }

    public AuthData register(UserData req) throws ServiceException, DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        if (users.getUser(req.username()) != null){
            throw new ServiceException(403, "Error: already taken");
        }

        users.createUser(req);
        var auth = new AuthData(UUID.randomUUID().toString(), req.username());
        auths.createAuth(auth);
        return auth;
    }
}