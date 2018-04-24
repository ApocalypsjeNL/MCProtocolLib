package nl.apocalypsje.protocollib.protocol.packet.login;

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

public class LoginStart extends Packet {

    private String username;

    public LoginStart() {
    }

    public LoginStart(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Override
    public void write(PacketEncoder packetEncoder) throws PacketWriteException {
        packetEncoder.writeString(username);
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        username = packetDecoder.readString();
    }

    @Override
    public int packetId() {
        return 0x00;
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
        return "LoginStart{" +
                "username='" + username + '\'' +
                '}';
    }
}
