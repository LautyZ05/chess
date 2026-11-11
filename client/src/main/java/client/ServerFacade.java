package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    String URL;
    HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String url){
        URL = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException{
        String body = new Gson().toJson(new UserData(username, password, email));
        HttpResponse<String> response = sendRequest("POST", "/user", body);
        return new Gson().fromJson(response.body(), AuthData.class);
    }

    private HttpResponse<String> sendRequest(String action, String path, String body) throws ResponseException{
        var request = HttpRequest.newBuilder(URI.create(URL + path))
                .method(action, requestBody(body))
                .build();
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
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
}
