package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void clearDatabase() throws Exception{
        facade.clear();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerTest() throws Exception{
        var authData = facade.register("testPlayer1", "testPassword", "p1@test.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void doubleRegisterFailTest() throws Exception{
        facade.register("testPlayer1", "testPassword", "p1@test.com");
        assertThrows(ResponseException.class, () -> facade.register("testPlayer1", "testPassword", "p1@test.com"));
    }

    @Test void clearTest() throws Exception{
        facade.register("testPlayer1", "testPassword", "p1@test.com");
        facade.clear();

        var authData = facade.register("testPlayer1", "testPassword", "p1@test.com");
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginTest() throws Exception{
        facade.register("testPlayer1", "testPassword", "p1@test.com");
        var authData = facade.login("testPlayer1", "testPassword");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void logoutTest() throws Exception{
        var authData = facade.register("testPlayer1", "testPassword", "p1@test.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void createGameTest() throws Exception{
        var authData = facade.register("testPlayer1", "testPassword", "p1@test.com");
        int gameID = facade.createGame(authData.authToken(), "testGame1");
        assertTrue(gameID > 0);
    }

    @Test
    public void listGamesTest() throws Exception{
        var authData = facade.register("testPlayer1", "testPassword", "p1@test.com");
        facade.createGame(authData.authToken(), "testGame1");
        facade.createGame(authData.authToken(), "testGame2");

        var games = facade.listGames(authData.authToken());
        assertEquals(2, games.size());
        assertEquals("testGame1", games.get(0).gameName());
    }
}
