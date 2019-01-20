package application;

public class InternalServerException extends Exception{
	public InternalServerException(String errorMessage) {
        super(errorMessage);
    }
}
