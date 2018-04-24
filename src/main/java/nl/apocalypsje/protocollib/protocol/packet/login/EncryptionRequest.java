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

public class EncryptionRequest extends Packet {

    private String serverId;
    private int publicKeyLength;
    private byte[] publicKey;
    private int verifyTokenLength;
    private byte[] verifyToken;

    public EncryptionRequest() {
    }

    public EncryptionRequest(String serverId, int publicKeyLength, byte[] publicKey, int verifyTokenLength, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKeyLength = publicKeyLength;
        this.publicKey = publicKey;
        this.verifyTokenLength = verifyTokenLength;
        this.verifyToken = verifyToken;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getPublicKeyLength() {
        return publicKeyLength;
    }

    public void setPublicKeyLength(int publicKeyLength) {
        this.publicKeyLength = publicKeyLength;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public int getVerifyTokenLength() {
        return verifyTokenLength;
    }

    public void setVerifyTokenLength(int verifyTokenLength) {
        this.verifyTokenLength = verifyTokenLength;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }



    @Override
    public void write(PacketEncoder packetEncoder) throws PacketWriteException {
        try {
            packetEncoder.writeString(serverId);
            packetEncoder.writeVarInt(publicKeyLength);
            packetEncoder.write(publicKey);
            packetEncoder.writeVarInt(verifyTokenLength);
            packetEncoder.write(verifyToken);
        } catch (IOException e) {
            throw new PacketWriteException(e);
        }
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        try {
            serverId = packetDecoder.readString();
            publicKeyLength = packetDecoder.readVarInt();
            publicKey = new byte[publicKeyLength];
            packetDecoder.readFully(publicKey);
            verifyTokenLength = packetDecoder.readVarInt();
            verifyToken = new byte[verifyTokenLength];
            packetDecoder.readFully(verifyToken);
        } catch (IOException e) {
            throw new PacketReadException(e);
        }
    }

    @Override
    public int packetId() {
        return 0x01;
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
        return "EncryptionRequest{" +
                "serverId='" + serverId + '\'' +
                ", publicKeyLength=" + publicKeyLength +
                ", publicKey=" + Arrays.toString(publicKey) +
                ", verifyTokenLength=" + verifyTokenLength +
                ", verifyToken=" + Arrays.toString(verifyToken) +
                '}';
    }
}
