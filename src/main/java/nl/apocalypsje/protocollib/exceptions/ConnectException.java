package nl.apocalypsje.protocollib.exceptions;

public class ConnectException extends Exception {

    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConnectException(Throwable throwable) {
        super(throwable);
    }
}
