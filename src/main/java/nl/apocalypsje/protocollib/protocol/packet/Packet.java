package nl.apocalypsje.protocollib.protocol.packet;

import nl.apocalypsje.protocollib.exceptions.PacketReadException;
import nl.apocalypsje.protocollib.exceptions.PacketWriteException;
import nl.apocalypsje.protocollib.protocol.ProtocolState;
import nl.apocalypsje.protocollib.protocol.ProtocolVersion;
import nl.apocalypsje.protocollib.stream.PacketDecoder;
import nl.apocalypsje.protocollib.stream.PacketEncoder;

import java.util.List;

public abstract class Packet {

    public abstract void write(PacketEncoder packetEncoder) throws PacketWriteException;

    public abstract void read(PacketDecoder packetDecoder) throws PacketReadException;

    public abstract int packetId();

    public abstract ProtocolState protocolState();

    public abstract List<ProtocolVersion> supportedVersions();

    public abstract String toString();
}
