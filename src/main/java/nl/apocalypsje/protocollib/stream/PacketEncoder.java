package nl.apocalypsje.protocollib.stream;

import nl.apocalypsje.protocollib.exceptions.PacketWriteException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class PacketEncoder extends DataOutputStream {

    public PacketEncoder(OutputStream outputStream) {
        super(outputStream);
    }

    public void writeVarInt(int value) throws PacketWriteException {
        final int varInt = value;
        try {
            do {
                int writeByte = value & 0b1111111;
                value >>>= 7;

                if(value != 0) writeByte |= 0b10000000;
                writeByte(writeByte);
            } while (value != 0);
        } catch (IOException e) {
            throw new PacketWriteException("Failed to encode VarInt '" + varInt + "'", e);
        }
    }

    public void writeString(String value) throws PacketWriteException {
        try {
            byte[] stringBytes = value.getBytes("UTF-8");
            writeVarInt(stringBytes.length);
            write(stringBytes);
        } catch (IOException e) {
            throw new PacketWriteException("Failed to encode String '" + value + "'", e);
        }
    }
}
