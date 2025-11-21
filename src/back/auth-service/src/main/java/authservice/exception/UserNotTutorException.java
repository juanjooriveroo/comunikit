package authservice.exception;

public class UserNotTutorException extends RuntimeException {
    public UserNotTutorException(String message) {
        super(message);
    }
}
