package exception;

/**
 * This exception is thrown when a user failed to login.
 * 
 * @author Frederic, Mathias, Pieter-Jan
 */
public class NoAccessException extends RuntimeException{
    
    private static final long serialVersionUID = -6476237094197413847L;

    
    /**
     * @see RuntimeException
     * @param message @see IllegalArgumentException
     */
    public NoAccessException(String message) {
        super(message);
    }
        
}