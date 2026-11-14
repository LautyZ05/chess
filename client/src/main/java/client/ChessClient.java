package client;

public class ChessClient {
    private enum State{
        LOGGED_OUT,
        LOGGED_IN
    }
    private State state = State.LOGGED_OUT;

    public ChessClient(String url) throws ResponseException{
        ServerFacade server = new ServerFacade(url);
    }

    public void run(){
        System.out.println("♕ Welcome to the 240 Chess! Type 'Help' to get started! ♕");
    }
}
