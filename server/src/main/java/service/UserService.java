package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDao users;
    private final AuthDao auths;
    public record LoginRequest(String username, String password){}

    public UserService(UserDao users, AuthDao auths){
        this.users = users;
        this.auths = auths;
    }

    public AuthData register(UserData req) throws ServiceException, DataAccessException{
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        if (users.getUser(req.username()) != null){
            throw new ServiceException(403, "Error: already taken");
        }

        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        users.createUser(new UserData(req.username(), hashedPassword, req.email()));
        var auth = new AuthData(UUID.randomUUID().toString(), req.username());
        auths.createAuth(auth);
        return auth;
    }

    public AuthData login(LoginRequest req) throws ServiceException, DataAccessException{
        if (req == null || req.username() == null || req.password() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        UserData user = users.getUser(req.username());
        if (user == null || !BCrypt.checkpw(req.password(), user.password())){
            throw new ServiceException(401, "Error: unauthorized");
        }
        var auth = new AuthData(UUID.randomUUID().toString(), user.username());
        auths.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws ServiceException, DataAccessException{
        if (authToken == null || auths.getAuth(authToken) == null){
            throw new ServiceException(401, "Error: unauthorized");
        }
        auths.deleteAuth(authToken);
    }
}