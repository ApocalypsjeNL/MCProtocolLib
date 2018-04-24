package nl.apocalypsje.protocollib.exceptions;

public class PacketReadException extends Exception {

    public PacketReadException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PacketReadException(String message) {
        super(message);
    }

    public PacketReadException(Throwable throwable) {
        super(throwable);
    }
}
