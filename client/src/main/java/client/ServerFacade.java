package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    String URL;
    HttpClient client = HttpClient.newHttpClient();
    private record CreateGameResult(int gameID) {}
    private record ListGamesResult(List<GameData> games) {}

    public ServerFacade(String url){
        URL = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException{
        String body = new Gson().toJson(new UserData(username, password, email));
        HttpResponse<String> response = sendRequest("POST", "/user", body, null);
        checkResponse(response);
        return new Gson().fromJson(response.body(), AuthData.class);
    }

    public void clear() throws ResponseException{
        HttpResponse<String> response = sendRequest("DELETE", "/db", null, null);
        checkResponse(response);
    }

    public AuthData login(String username, String password) throws ResponseException{
        String body = new Gson().toJson(Map.of("username", username, "password", password));
        HttpResponse<String> response = sendRequest("POST", "/session", body, null);
        checkResponse(response);
        return new Gson().fromJson(response.body(), AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        HttpResponse<String> response = sendRequest("DELETE", "/session", null, authToken);
        checkResponse(response);
    }

    public int createGame(String authToken, String gameName) throws ResponseException{
        String body = new Gson().toJson(Map.of("gameName", gameName));
        HttpResponse<String> response = sendRequest("POST", "/game", body, authToken);
        checkResponse(response);
        return new Gson().fromJson(response.body(), CreateGameResult.class).gameID();
    }

    public List<GameData> listGames(String authToken) throws ResponseException{
        HttpResponse<String> response = sendRequest("GET", "/game", null, authToken);
        checkResponse(response);
        return new Gson().fromJson(response.body(), ListGamesResult.class).games();
    }

    private HttpResponse<String> sendRequest(String action, String path, String body, String authToken) throws ResponseException{
        var request = HttpRequest.newBuilder(URI.create(URL + path))
                .method(action, requestBody(body));
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.header("authorization", authToken);
        }
        try{
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e){
            throw new ResponseException(500, "Unable to reach the server");
        }
    }

    private HttpRequest.BodyPublisher requestBody(String body){
        if (body != null){
            return HttpRequest.BodyPublishers.ofString(body);
        }
        else{
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private void checkResponse(HttpResponse<String> response) throws ResponseException{
        int status = response.statusCode();
        if (status / 100 != 2){
            throw new ResponseException(status, errorMessage(response.body()));
        }
    }

    private String errorMessage(String body){
        try {
            return new Gson().fromJson(body, java.util.Map.class).get("message").toString();
        }
        catch (Exception e){
            return "Something went wrong";
        }
    }
}
