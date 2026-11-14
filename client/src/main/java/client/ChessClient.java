package client;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private enum State{
        LOGGED_OUT,
        LOGGED_IN
    }
    private State state = State.LOGGED_OUT;
    ServerFacade server;

    public ChessClient(String url) throws ResponseException{
        server = new ServerFacade(url);
    }

    public void run(){
        System.out.println("♕ Welcome to the 240 Chess! Type 'Help' to get started! ♕");
        Scanner scanned = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            System.out.print("\n[" + state + "] >>> ");
            String userInput = scanned.nextLine();
            result = evaluateUserInput(userInput);
            System.out.print(result);
        }
    }

    private String evaluateUserInput(String userInput){
        String[] inputs = userInput.split(" ");
        String command = inputs[0].toLowerCase();
        String[] params = Arrays.copyOfRange(inputs, 1, inputs.length);
        try{
            if (command.equals("help")){
                return help();
            }
            else if (command.equals("register")){
                return register(params);
            }
            else if (command.equals("login")){
                return login(params);
            }
            else if (command.equals("quit")){
                return "quit";
            }
            else{
                return "not valid command\n";
            }
        }
        catch (ResponseException e){
            System.out.print("Something went wrong lol");
            return command;
        }
    }

    private String register(String... params) throws ResponseException{
        if (params.length == 3){
            return "Entered register with good params\n";
        }
        else {
            return "Entered register with bad params\n";
        }
    }

    private String login(String... params) throws ResponseException{
        if (params.length == 2){
            return "Entered login with good params\n";
        }
        else {
            return "Entered login with bad params\n";
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
