package nl.apocalypsje.protocollib.protocol.packet.handshake;

import nl.apocalypsje.protocollib.exceptions.PacketReadException;
import nl.apocalypsje.protocollib.exceptions.PacketWriteException;
import nl.apocalypsje.protocollib.protocol.ProtocolState;
import nl.apocalypsje.protocollib.protocol.ProtocolVersion;
import nl.apocalypsje.protocollib.protocol.packet.Packet;
import nl.apocalypsje.protocollib.stream.PacketDecoder;
import nl.apocalypsje.protocollib.stream.PacketEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Handshake extends Packet {

    private int protocolVersion;
    private String serverHost;
    private int serverPort;
    private int state;

    public Handshake() {
    }

    public Handshake(int protocolVersion, String serverHost, int serverPort, int state) {
        this.protocolVersion = protocolVersion;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.state = state;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void write(PacketEncoder packetEncoder) throws PacketWriteException {
        try {
            packetEncoder.writeVarInt(protocolVersion);
            packetEncoder.writeString(serverHost);
            packetEncoder.writeShort(serverPort);
            packetEncoder.writeVarInt(state);
        } catch (IOException e) {
            throw new PacketWriteException(e);
        }
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        try {
            protocolVersion = packetDecoder.readVarInt();
            serverHost = packetDecoder.readString();
            serverPort = packetDecoder.readShort();
            state = packetDecoder.readVarInt();
        } catch (IOException e) {
            throw new PacketReadException(e);
        }
    }

    @Override
    public int packetId() {
        return 0x00;
    }

    @Override
    public ProtocolState protocolState() {
        return ProtocolState.HANDSHAKE;
    }

    @Override
    public List<ProtocolVersion> supportedVersions() {
        return Arrays.asList(ProtocolVersion.v1_8, ProtocolVersion.v1_9, ProtocolVersion.v1_9_1, ProtocolVersion.v1_9_2, ProtocolVersion.v1_9_3, ProtocolVersion.v1_10, ProtocolVersion.v1_11, ProtocolVersion.v1_11_1, ProtocolVersion.v1_12, ProtocolVersion.v1_12_1, ProtocolVersion.v1_12_2);
    }

    @Override
    public String toString() {
        return "Handshake{" +
                "protocolVersion=" + protocolVersion +
                ", serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                ", state=" + state +
                '}';
    }
}
