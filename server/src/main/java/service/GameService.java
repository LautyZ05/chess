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
    private final AuthDao auths;
    private final GameDao games;
    public record GameSummary(int gameID, String whiteUsername, String blackUsername, String gameName){}
    public record ListGamesResult(Collection<GameSummary> games){}
    public record CreateGameRequest(String gameName){}
    public record CreateGameResult(int gameID){}
    public record JoinGameRequest(String playerColor, int gameID){}

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

    public void joinGame(String authToken, JoinGameRequest req) throws ServiceException, DataAccessException{
        AuthData auth = authenticate(authToken);
        if (req == null || req.playerColor() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        GameData game = games.getGame(req.gameID());
        if (game == null){
            throw new ServiceException(400, "Error: bad request");
        }

        String white = game.whiteUsername();
        String black = game.blackUsername();

        if (req.playerColor().equals("WHITE")){
            if (white != null) {
                throw new ServiceException(403, "Error: already taken");
            }
            white = auth.username();
        }
        else if (req.playerColor().equals("BLACK")){
            if (black != null) {
                throw new ServiceException(403, "Error: already taken");
            }
            black = auth.username();
        }
        else{
            throw new ServiceException(400, "Error: bad request");
        }
        games.updateGame(new GameData(game.gameID(), white, black, game.gameName(), game.game()));
    }
}
