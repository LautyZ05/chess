package server;

import com.google.gson.Gson;
import io.javalin.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx -> ctx.result("{}")); //just to move onto, not actually clearing stuff

        // Register your endpoints and exception handlers here.
        javalin.post("/user", Server::register);

    }

    private static void register(io.javalin.http.Context ctx) {
        var result = Map.of("username", "", "authToken", "");
        ctx.result(new Gson().toJson(result));
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
