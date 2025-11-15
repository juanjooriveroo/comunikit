package authservice.exception;

public class PasswordDuplicateException extends RuntimeException {
    public PasswordDuplicateException(String message) {super(message);}
}