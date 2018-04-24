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
import java.util.UUID;

public class LoginSuccess  extends Packet {

    private UUID uuid;
    private String username;

    public LoginSuccess() {
    }

    public LoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void write(PacketEncoder packetEncoder) throws PacketWriteException {
        packetEncoder.writeString(uuid.toString());
        packetEncoder.writeString(username);
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        uuid = UUID.fromString(packetDecoder.readString());
        username = packetDecoder.readString();
    }

    @Override
    public int packetId() {
        return 0x02;
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
        return "LoginSuccess{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                '}';
    }
}
