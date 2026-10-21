package dataaccess;

import model.*;
import java.util.HashMap;

public class MemoryUserDao implements UserDao{
    private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}