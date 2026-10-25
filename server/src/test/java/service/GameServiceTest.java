package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private UserService userService;
    private GameService gameService;
    private String testToken;

    @BeforeEach
    public void setup() throws Exception {
        var userDAO = new MemoryUserDao();
        var authDAO = new MemoryAuthDao();
        var gameDAO = new MemoryGameDao();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        testToken = userService.register(new UserData("test1", "password1", "test@mail.com")).authToken();
    }

    @Test
    void listGamesSuccess() throws Exception{
        gameService.createGame(testToken, new GameService.CreateGameRequest("game1"));
        gameService.createGame(testToken, new GameService.CreateGameRequest("game2"));

        var result = gameService.listGames(testToken);
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesEmpty() throws Exception{
        var result = gameService.listGames(testToken);
        assertTrue(result.games().isEmpty());
    }

    @Test
    void createGameSuccess() throws Exception{
        var result = gameService.createGame(testToken, new GameService.CreateGameRequest("game1"));
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameUniqueIDs() throws Exception{
        var first = gameService.createGame(testToken, new GameService.CreateGameRequest("game1"));
        var second = gameService.createGame(testToken, new GameService.CreateGameRequest("game2"));
        assertNotEquals(first.gameID(), second.gameID());
    }

    @Test
    void createGameMissingName(){
        var e = assertThrows(ServiceException.class, () -> gameService.createGame(testToken, new GameService.CreateGameRequest(null)));
        assertEquals(400, e.status());
    }

    @Test
    void joinGameSuccess() throws Exception{
        int id = gameService.createGame(testToken, new GameService.CreateGameRequest("game1")).gameID();
        gameService.joinGame(testToken, new GameService.JoinGameRequest("WHITE", id));

        var games = gameService.listGames(testToken).games();
        var game = games.iterator().next();
        assertEquals("test1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameBothColors() throws Exception{
        var testToken2 = userService.register(new UserData("test2", "password2", "test2@mail.com")).authToken();
        int id = gameService.createGame(testToken, new GameService.CreateGameRequest("game1")).gameID();

        gameService.joinGame(testToken, new GameService.JoinGameRequest("WHITE", id));
        gameService.joinGame(testToken2, new GameService.JoinGameRequest("BLACK", id));

        var game = gameService.listGames(testToken).games().iterator().next();
        assertEquals("test1", game.whiteUsername());
        assertEquals("test2", game.blackUsername());
    }
}