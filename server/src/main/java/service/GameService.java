package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private AuthDao auths;
    private GameDao games;
    public record GameSummary(int gameID, String whiteUsername, String blackUsername, String gameName){}
    public record ListGamesResult(Collection<GameSummary> games){}
    public record CreateGameRequest(String gameName){}
    public record CreateGameResult(int gameID){}

    public GameService(AuthDao auths, GameDao games){
        this.auths = auths;
        this.games = games;
    }

    private AuthData authenticate(String authToken) throws ServiceException, DataAccessException{
        AuthData auth = (authToken == null) ? null : auths.getAuth(authToken);
        if (auth == null){
            throw new ServiceException(401, "Error: unauthorized");
        }
        return auth;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException{
        authenticate(authToken);
        var summaries = new ArrayList<GameSummary>();
        for (GameData g : games.listGames()){
            summaries.add(new GameSummary(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()));
        }
        return new ListGamesResult(summaries);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws ServiceException, DataAccessException{
        authenticate(authToken);
        if (req == null || req.gameName() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        GameData created = games.createGame(new GameData(0, null, null, req.gameName(), new ChessGame()));
        return new CreateGameResult(created.gameID());
    }
}
