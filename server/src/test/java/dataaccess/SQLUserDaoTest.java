package dataaccess;

import dataaccess.sql.SQLUserDao;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLUserDaoTest{
    private SQLUserDao testDao;

    @BeforeEach
    public void setup() throws Exception {
        testDao = new SQLUserDao();
        testDao.clear();
    }

    @Test
    public void userTableExists() throws Exception{
        new SQLUserDao();
        try (var conn = DatabaseManager.getConnection();){
            try (var ps = conn.prepareStatement("SHOW TABLES LIKE 'user'")){
                try (var rs = ps.executeQuery()){
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    public void createAndGetUser() throws Exception {
        testDao.createUser(new UserData("test1", "password1", "test@email.com"));
        var user = testDao.getUser("test1");
        assertEquals("test1", user.username());
        assertEquals("password1", user.password());
        assertEquals("test@email.com", user.email());
    }
}