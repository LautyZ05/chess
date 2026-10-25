package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private MemoryUserDao userDAO;
    private MemoryAuthDao authDAO;
    private MemoryGameDao gameDAO;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @BeforeEach
    public void setup(){
        userDAO = new MemoryUserDao();
        authDAO = new MemoryAuthDao();
        gameDAO = new MemoryGameDao();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void clearTest() throws Exception{
        var auth = userService.register(new UserData("test1", "password1", "test@mail.com"));
        gameService.createGame(auth.authToken(), new GameService.CreateGameRequest("game1"));

        clearService.clear();
        assertNull(userDAO.getUser("test1"));
        assertNull(authDAO.getAuth(auth.authToken()));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}