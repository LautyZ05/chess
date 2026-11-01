package dataaccess;

import chess.ChessGame;
import dataaccess.sql.SQLGameDao;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDaoTest {
    private SQLGameDao testDao;

    @BeforeEach
    public void setup() throws Exception{
        testDao = new SQLGameDao();
        testDao.clear();
    }

    @Test
    public void userTableExists() throws Exception{
        new SQLGameDao();
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement("SHOW TABLES LIKE 'game'")){
                try (var rs = ps.executeQuery()){
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    public void createAndGetGame() throws Exception{
        var created = testDao.createGame(new GameData(0, null, null, "testGame1", new ChessGame()));
        var loaded = testDao.getGame(created.gameID());

        assertNotNull(loaded);
        assertEquals(created.gameID(), loaded.gameID());
        assertEquals("testGame1", loaded.gameName());
        assertNull(loaded.whiteUsername());
        assertNull(loaded.blackUsername());
        assertNotNull(loaded.game());
        assertEquals(new ChessGame(), loaded.game());
    }

    @Test
    public void listGamesTest() throws Exception{
        testDao.createGame(new GameData(0, null, null, "testGame1", new ChessGame()));
        testDao.createGame(new GameData(0, null, null, "testGame2", new ChessGame()));
        assertEquals(2, testDao.listGames().size());
    }

    @Test
    public void updateGameTest() throws Exception{
        var created = testDao.createGame(new GameData(0, null, null, "testGame1", new ChessGame()));
        testDao.updateGame(new GameData(created.gameID(), "whiteTest", "blackTest", "testGame1", created.game()));

        var loaded = testDao.getGame(created.gameID());
        assertEquals("whiteTest", loaded.whiteUsername());
        assertEquals("blackTest", loaded.blackUsername());
    }

    @Test
    public void clearRemovesGames() throws Exception{
        testDao.createGame(new GameData(0, null, null, "testGame1", new ChessGame()));
        testDao.clear();
        assertTrue(testDao.listGames().isEmpty());
    }
}