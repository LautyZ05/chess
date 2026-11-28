package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.*;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler{
    private AuthDao authDAO;
    private GameDao gameDAO;
    private final ConnectionManager connections;

    public WebSocketHandler(AuthDao authDAO, GameDao gameDAO, ConnectionManager connections){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.connections = connections;
    }

    @Override
    public void handleConnect(WsConnectContext ctx){
        System.out.println("WS is connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx){
        var session = ctx.session;
        try{
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            if (command == null || command.getCommandType() == null || command.getGameID() == null){
                sendError(session, "Error: invalid command");
                return;
            }
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null){
                sendError(session, "Error: unauthorized");
                return;
            }
            GameData game = gameDAO.getGame(command.getGameID());
            if (game == null){
                sendError(session, "Error: game not found");
                return;
            }
            switch (command.getCommandType()){
                case CONNECT -> connect(session, auth, game);
            }
        }
        catch (Exception e){
            sendError(session, "Error: something went wrong");
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx){
        connections.remove(ctx.session);
    }

    private void connect(Session session, AuthData auth, GameData game){
        connections.add(game.gameID(), session, auth.username());
        connections.send(session, new LoadGameMessage(game.game()));

        ChessGame.TeamColor color = colorOf(auth.username(), game);
        String text = color == null ? auth.username() + " is observing the game" : auth.username() + " joined the game as " + color.toString().toLowerCase();
        connections.broadcastToOthers(game.gameID(), session, new NotificationMessage(text));

    }

    private ChessGame.TeamColor colorOf(String username, GameData game){
        if (username.equals(game.whiteUsername())){
            return ChessGame.TeamColor.WHITE;
        }
        if (username.equals(game.blackUsername())){
            return ChessGame.TeamColor.BLACK;
        }
        else{
            return null;
        }
    }

    private void sendError(Session session, String message){
        connections.send(session, new ErrorMessage(message));
    }
}
