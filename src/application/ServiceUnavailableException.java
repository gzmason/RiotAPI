package application;

public class ServiceUnavailableException extends Exception{
	public ServiceUnavailableException(String errorMessage) {
        super(errorMessage);
    }
}
