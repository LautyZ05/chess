package dataaccess;

import dataaccess.sql.SQLUserDao;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLUserDaoTest{
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
}