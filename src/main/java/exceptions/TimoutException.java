package exceptions;

public class TimoutException extends Exception{
	public TimoutException(String errorMessage) {
        super(errorMessage);
    }
}
