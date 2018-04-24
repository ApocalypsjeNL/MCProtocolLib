package nl.apocalypsje.protocollib.protocol.packet.login;

import nl.apocalypsje.protocollib.exceptions.PacketReadException;
import nl.apocalypsje.protocollib.exceptions.PacketWriteException;
import nl.apocalypsje.protocollib.protocol.ProtocolState;
import nl.apocalypsje.protocollib.protocol.ProtocolVersion;
import nl.apocalypsje.protocollib.protocol.packet.Packet;
import nl.apocalypsje.protocollib.stream.PacketDecoder;
import nl.apocalypsje.protocollib.stream.PacketEncoder;

import java.util.Arrays;
import java.util.List;

public class SetCompression extends Packet {

    private int threshold;

    public SetCompression() {
    }

    public SetCompression(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void write(PacketEncoder packetEncoder) throws PacketWriteException {
        packetEncoder.writeVarInt(threshold);
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        threshold = packetDecoder.readVarInt();
    }

    @Override
    public int packetId() {
        return 0x03;
    }

    @Override
    public ProtocolState protocolState() {
        return ProtocolState.LOGIN;
    }

    @Override
    public List<ProtocolVersion> supportedVersions() {
        return Arrays.asList(ProtocolVersion.v1_8, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_3, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_11_1, ProtocolVersion.v1_12, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2);
    }

    @Override
    public String toString() {
        return "SetCompression{" +
                "threshold=" + threshold +
                '}';
    }
}
