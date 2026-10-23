package dataaccess;


import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDao{
    private int nextId = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(GameData game){
        game = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(game.gameID(), game);
        return game;
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public Collection<GameData> listGames(){
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException{
        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("game does not exist");
        }
        games.put(game.gameID(), game);
    }

    public void clear(){
        games.clear();
    }
}