package nl.apocalypsje.protocollib.exceptions;

public class LoginException extends Exception {

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LoginException(Throwable throwable) {
        super(throwable);
    }
}
