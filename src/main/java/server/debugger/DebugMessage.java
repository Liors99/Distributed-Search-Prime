package server.debugger;

public class DebugMessage {
    private String message;

    /**
     * empty constructor for debug message used in debug mode
     */
    public DebugMessage(){}
    /**
     * constructor for debug message used in debug mode
     * @param s string to be displayed
     */
    public DebugMessage(String s){
        message = s;
    }

    /**
     * get message to be displayed
     * @return string message which should be displayed in debug mode
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * set message to be contained in the class
     * @param message a string to be stored
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
