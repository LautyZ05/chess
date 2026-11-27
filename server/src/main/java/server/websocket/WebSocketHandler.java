package server.websocket;

import dataaccess.*;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageHandler;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler{
    private AuthDao authDao;
    private GameDao gameDao;
    private
}
