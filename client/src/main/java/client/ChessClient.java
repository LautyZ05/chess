package client;

import java.util.Scanner;

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
        Scanner scanned = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            System.out.print("\n[" + state + "] >>> ");
            String userInput = scanned.nextLine();
            result = userInput;
            if (result.equals("Help")){
                System.out.print(help());
            }
        }
    }

    private String help(){
        if (state == State.LOGGED_OUT){
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to login to an account
                    quit - exit
                    help - to bring out commands
                    """;
        }
        else{
            return """
                    create <NAME> - create a game
                    list - list available games
                    join <ID> <WHITE|BLACK> - join a game
                    observe <ID> - watch a chess match
                    logout - logout of account
                    quit - stop playing chess
                    help - to bring out commands
                    """;
        }
    }
}
