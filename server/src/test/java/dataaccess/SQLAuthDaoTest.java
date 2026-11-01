package dataaccess;

import dataaccess.sql.SQLAuthDao;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDaoTest{
    private SQLAuthDao testDao;

    @BeforeEach
    public void setup() throws Exception {
        testDao = new SQLAuthDao();
        testDao.clear();
    }

    @Test
    public void userTableExists() throws Exception{
        new SQLAuthDao();
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement("SHOW TABLES LIKE 'auth'")){
                try (var rs = ps.executeQuery()){
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    public void createAndGetAuth() throws Exception{
        testDao.createAuth(new AuthData("testToken1", "test1"));
        var auth = testDao.getAuth("testToken1");
        assertEquals("testToken1", auth.authToken());
        assertEquals("test1", auth.username());
    }

    @Test
    public void deleteAuth() throws Exception{
        testDao.createAuth(new AuthData("testToken1", "test1"));
        testDao.deleteAuth("testToken1");
        assertNull(testDao.getAuth("testToken1"));
    }

    @Test
    public void clearAuths() throws Exception{
        testDao.createAuth(new AuthData("testToken1", "test1"));
        testDao.createAuth(new AuthData("testToken2", "test2"));
        testDao.clear();
        assertNull(testDao.getAuth("testToken1"));
        assertNull(testDao.getAuth("testToken2"));
    }
}