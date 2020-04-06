package server.debugger;

public class DebugMessage {
    private String message;

    public DebugMessage(){}
    public DebugMessage(String s){
        message = s;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
