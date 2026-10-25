package service;

import dataaccess.*;

public class ClearService{
    private final UserDao users;
    private final AuthDao auths;
    private final GameDao games;

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