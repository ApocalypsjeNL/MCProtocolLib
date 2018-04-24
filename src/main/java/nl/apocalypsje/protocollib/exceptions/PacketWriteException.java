package nl.apocalypsje.protocollib.exceptions;

public class PacketWriteException extends Exception {

    public PacketWriteException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PacketWriteException(String message) {
        super(message);
    }

    public PacketWriteException(Throwable throwable) {
        super(throwable);
    }
}