package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import model.UserData;
import service.ClearService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final UserDao userDAO = new MemoryUserDao();
    private final AuthDao authDAO = new MemoryAuthDao();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear); //just to move onto, not actually clearing stuff

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);

        javalin.post("/session", this::login);

        javalin.delete("/session", this::logout);

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
