package dataaccess;

import dataaccess.sql.SQLGameDao;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDaoTest {
    private SQLGameDao testDao;

    @BeforeEach
    public void setup() throws Exception {
        testDao = new SQLGameDao();
        testDao.clear();
    }

    @Test
    public void userTableExists() throws Exception{
        new SQLGameDao();
        try (var conn = DatabaseManager.getConnection();){
            try (var ps = conn.prepareStatement("SHOW TABLES LIKE 'game'")){
                try (var rs = ps.executeQuery()){
                    assertTrue(rs.next());
                }
            }
        }
    }

}