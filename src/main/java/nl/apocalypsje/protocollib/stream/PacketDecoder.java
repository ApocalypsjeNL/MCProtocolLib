package nl.apocalypsje.protocollib.stream;

import nl.apocalypsje.protocollib.exceptions.PacketReadException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketDecoder extends DataInputStream {

    public PacketDecoder(InputStream inputStream) {
        super(inputStream);
    }

    public int readVarInt() throws PacketReadException {
        int varInt = 0;
        int byteShift = 0;
        byte readByte;
        try {
            do {
                readByte = readByte();
                varInt |= (readByte & 0b1111111) << (byteShift++ * 7);

                if (byteShift > 5) throw new PacketReadException("Failed to decode VarInt (too big)");

            } while((readByte & 0b10000000) == 0b10000000);
        } catch (IOException e) {
            throw new PacketReadException("Failed to decode VarInt", e);
        }

        return varInt;
    }

    public String readString() throws PacketReadException {
        try {
            int length = readVarInt();
            byte[] stringBytes = new byte[length];
            readFully(stringBytes);
            String s = new String(stringBytes, "UTF-8");
            return s;
        } catch (IOException e) {
            throw new PacketReadException("Failed to decode String", e);
        }
    }
}
