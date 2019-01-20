package application;

public class ForbiddenException extends Exception{
	public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}
