package dataaccess;

import dataaccess.sql.SQLAuthDao;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDaoTest{
    @Test
    public void userTableExists() throws Exception{
        new SQLAuthDao();
        try (var conn = DatabaseManager.getConnection();){
            try (var ps = conn.prepareStatement("SHOW TABLES LIKE 'auth'")){
                try (var rs = ps.executeQuery()){
                    assertTrue(rs.next());
                }
            }
        }
    }
}