package dataaccess;

import model.*;
import java.util.HashMap;

public class MemoryUserDao implements UserDao{
    private HashMap<String, UserData> users = new HashMap<>();

}