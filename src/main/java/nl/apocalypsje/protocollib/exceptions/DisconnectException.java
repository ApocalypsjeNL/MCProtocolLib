package nl.apocalypsje.protocollib.exceptions;

public class DisconnectException extends Exception {

    public DisconnectException(String message) {
        super(message);
    }

    public DisconnectException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DisconnectException(Throwable throwable) {
        super(throwable);
    }
}
