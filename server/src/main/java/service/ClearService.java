package service;

import dataaccess.*;

public class ClearService{
    private UserDao users;
    private AuthDao auths;
    private GameDao games;

    public ClearService(UserDao users, AuthDao auths, GameDao games){
        this.users = users;
        this.auths = auths;
        this.games = games;
    }

    public void clear() throws DataAccessException{
        users.clear();
        auths.clear();
        games.clear();
    }
}