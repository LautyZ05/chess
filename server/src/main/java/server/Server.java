package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.sql.SQLUserDao;
import io.javalin.*;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final UserDao userDAO;
    private final AuthDao authDAO;
    private final GameDao gameDAO;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server() {
        try{
            userDAO = new SQLUserDao();
        }
        catch (DataAccessException e){
            throw new RuntimeException("Unable to start server: " + e.getMessage());
        }
        authDAO = new MemoryAuthDao();
        gameDAO = new MemoryGameDao();
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);

        javalin.post("/user", this::register);

        javalin.post("/session", this::login);

        javalin.delete("/session", this::logout);

        javalin.post("/game", this::createGame);

        javalin.put("/game", this::joinGame);

        javalin.get("/game", this::listGames);

        javalin.exception(ServiceException.class, this::serviceExceptionHandler);

        javalin.exception(Exception.class, this::unexpectedExceptionHandler);
    }

    private void clear(io.javalin.http.Context ctx) throws DataAccessException{
        clearService.clear();
        ctx.result("{}");
    }

    private void register(io.javalin.http.Context ctx) throws ServiceException, DataAccessException {
        var req = new Gson().fromJson(ctx.body(), UserData.class);
        var result = userService.register(req);
        ctx.result(new Gson().toJson(result));
    }

    private void login(io.javalin.http.Context ctx) throws ServiceException, DataAccessException{
        var req = new Gson().fromJson(ctx.body(), UserService.LoginRequest.class);
        ctx.result(new Gson().toJson(userService.login(req)));
    }

    private void logout(io.javalin.http.Context ctx) throws ServiceException, DataAccessException{
        userService.logout(ctx.header("authorization"));
        ctx.result("{}");
    }

    private void createGame(io.javalin.http.Context ctx) throws ServiceException, DataAccessException{
        var req = new Gson().fromJson(ctx.body(), GameService.CreateGameRequest.class);
        ctx.result(new Gson().toJson(gameService.createGame(ctx.header("authorization"), req)));
    }

    private void joinGame(io.javalin.http.Context ctx) throws ServiceException, DataAccessException{
        var req = new Gson().fromJson(ctx.body(), GameService.JoinGameRequest.class);
        gameService.joinGame(ctx.header("authorization"), req);
        ctx.result("{}");
    }

    private void listGames(io.javalin.http.Context ctx) throws ServiceException, DataAccessException{
        ctx.result(new Gson().toJson(gameService.listGames(ctx.header("authorization"))));
    }

    private void serviceExceptionHandler(ServiceException ex, io.javalin.http.Context ctx) {
        ctx.status(ex.status());
        ctx.result(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void unexpectedExceptionHandler(Exception ex, io.javalin.http.Context ctx) {
        ctx.status(500);
        ctx.result(new Gson().toJson(Map.of("message", "Error: " + ex.getMessage())));
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
