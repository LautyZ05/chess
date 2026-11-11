package client;

public class ResponseException extends Exception{
    int status;

    public ResponseException(int status, String message){
        super(message);
        this.status = status;
    }

    public int status(){
        return status;
    }
}
