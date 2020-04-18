package exceptions;

/**
 * A custom exception that is thrown whenever an entity is timed out
 *
 */
public class TimeoutException extends Exception{
	public TimeoutException(String errorMessage) {
        super(errorMessage);
    }
}
