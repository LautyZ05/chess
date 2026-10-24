package service;

import dataaccess.*;

public class ClearService{
    private UserDao users;
    private AuthDao auths;

    public ClearService(UserDao users, AuthDao auths){
        this.users = users;
        this.auths = auths;
    }

    public void clear() throws DataAccessException{
        users.clear();
        auths.clear();
    }
}