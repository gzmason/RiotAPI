package application;

public class RateLimitException extends Exception { 
    public RateLimitException(String errorMessage) {
        super(errorMessage);
    }
}