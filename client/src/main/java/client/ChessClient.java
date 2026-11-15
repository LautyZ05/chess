package client;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    private enum State{
        LOGGED_OUT,
        LOGGED_IN
    }
    private State state = State.LOGGED_OUT;
    private final ServerFacade server;
    private String authToken;
    private List<GameData> games = new ArrayList<>();

    public ChessClient(String url) throws ResponseException{
        server = new ServerFacade(url);
    }

    public void run(){
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + "♕ Welcome to the 240 Chess! Type " + SET_TEXT_COLOR_GREEN + "'Help'" + SET_TEXT_COLOR_WHITE + " to get started! ♕" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
        Scanner scanned = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            System.out.print("\n[" + state + "] >>> ");
            String userInput = scanned.nextLine();
            result = evaluateUserInput(userInput);
            if (result.equals("quit")){
                System.out.print("Exiting program. Come again!");
            }
            else {
                System.out.print(result);
            }
        }
    }

    private String evaluateUserInput(String userInput){
        String[] inputs = userInput.split(" ");
        String command = inputs[0].toLowerCase();
        String[] params = Arrays.copyOfRange(inputs, 1, inputs.length);
        try{
            if (state == State.LOGGED_OUT){
                return evaluateLoggedOut(command, params);
            }
            else {
                return evaluateLoggedIn(command, params);
            }
        }
        catch (ResponseException e){
            return "Something went wrong, please try again.";
        }
    }

    private String evaluateLoggedOut(String command, String[] params) throws ResponseException {
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

    private String evaluateLoggedIn(String command, String[] params) throws ResponseException{
        if (command.equals("help")){
            return help();
        }
        else if (command.equals("create")){
            return create(params);
        }
        else if (command.equals("list")){
            return list();
        }
        else if (command.equals("join")){
            return join(params);
        }
        else if (command.equals("observe")){
            return observe(params);
        }
        else if (command.equals("logout")){
            return logout();
        }
        else if (command.equals("quit")){
            return "quit";
        }
        else{
            return "not valid command\n";
        }
    }

    private String register(String... params) throws ResponseException{
        if (params.length != 3){
            return "Please enter the following: <USERNAME> <PASSWORD> <EMAIL>\n";
        }
        try{
            AuthData auth = server.register(params[0], params[1], params[2]);
            authToken = auth.authToken();
            state = State.LOGGED_IN;
            return String.format("You're logged in as %s.\n", auth.username());
        }
        catch (ResponseException e){
            if (e.status() == 403){
                return "Username is already take, try another.\n";
            }
            return "Unable to register user. Please try again.\n";
        }
    }

    private String login(String... params) throws ResponseException{
        if (params.length != 2){
            return "Please enter the following: <USERNAME> <PASSWORD>\n";
        }
        try{
            AuthData auth = server.login(params[0], params[1]);
            authToken = auth.authToken();
            state = State.LOGGED_IN;
            return String.format("You're logged in as %s.\n", auth.username());
        }
        catch (ResponseException e){
            if (e.status() == 401){
                return "Wrong username or password.\n";
            }
            return "Unable to log in user. Please try again.\n";
        }
    }

    private String create(String... params) throws ResponseException{
        if (params.length != 1){
            return "Please enter the following: <NAME>\n";
        }
        try{
            server.createGame(authToken, params[0]);
            return String.format("""
                    Created game '%s'.
                    Use 'list' to see all games available.
                    Use 'join' to join a game.
                    """, params[0]);
        }
        catch (ResponseException e){
            return "Unable to create game. Please try again.\n";
        }
    }

    private String checkPlayerName(String userName){
        if (userName == null){
            return "empty";
        }
        else{
            return userName;
        }
    }

    private String list() throws ResponseException{
        try {
            games = server.listGames(authToken);
            if (games.isEmpty()){
                return "There are no games created. Use 'create' to make a new game\n";
            }
            StringBuilder gamesList = new StringBuilder();
            for (int i = 0; i < games.size(); i++){
                GameData game = games.get(i);
                gamesList.append(String.format("%d. GAME: %s  WHITE: %s  BLACK: %s\n", i+1, game.gameName(), checkPlayerName(game.whiteUsername()), checkPlayerName(game.blackUsername())));
            }
            return gamesList.toString();
        }
        catch (ResponseException e){
            return "Unable to list game(s). Please try again.\n";
        }
    }

    private GameData findGame(String gameNum) throws ResponseException{
        int number;
        try{
            number = Integer.parseInt(gameNum);
            if (number < 1 || number > games.size()){
                return null;
            }
            return games.get(number-1);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    private String join(String... params) throws ResponseException{
        if (params.length != 2){
            return "Please enter the following: <NUMBER> <WHITE|BLACK>\n";
        }
        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")){
            return "Color must be either 'WHITE' or 'BLACK'.\n";
        }
        GameData game = findGame(params[0]);
        if (game == null){
            return "That game number doesn't exist. Use 'list' to see games.\n";
        }
        try{
            server.joinGame(authToken, color, game.gameID());
            return "Joined '" + game.gameName() + "' as '" + color + "'.\n";
        }
        catch (ResponseException e){
            if (e.status == 403){
                return "That color is already taken.\n";
            }
            return "Unable to join game. Please try again.\n";
        }
    }

    private String observe(String... params) throws ResponseException{
        if (params.length != 1){
            return "Please enter the following: <NUMBER>\n.";
        }
        GameData game = findGame(params[0]);
        if (game == null){
            return "That game number doesn't exist. Use 'list' to see games.\n";
        }
        return "Observing '" + game.gameName() + "'.\n";
    }

    private String logout() throws ResponseException{
        try{
            server.logout(authToken);
            authToken = null;
            state = State.LOGGED_OUT;
            return "You have been logged out.\n";
        }
        catch (ResponseException e){
            return "An error has occurred.\n";
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
