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

public class EncryptionResponse extends Packet {

    private int sharedSecretLength;
    private byte[] sharedSecret;
    private int verifyTokenLength;
    private byte[] verifyToken;

    public EncryptionResponse() {
    }

    public EncryptionResponse(int sharedSecretLength, byte[] sharedSecret, int verifyTokenLength, byte[] verifyToken) {
        this.sharedSecretLength = sharedSecretLength;
        this.sharedSecret = sharedSecret;
        this.verifyTokenLength = verifyTokenLength;
        this.verifyToken = verifyToken;
    }

    public int getSharedSecretLength() {
        return sharedSecretLength;
    }

    public void setSharedSecretLength(int sharedSecretLength) {
        this.sharedSecretLength = sharedSecretLength;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
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
            packetEncoder.writeVarInt(sharedSecretLength);
            packetEncoder.write(sharedSecret);
            packetEncoder.writeVarInt(verifyTokenLength);
            packetEncoder.write(verifyToken);
        } catch (IOException e) {
            throw new PacketWriteException(e);
        }
    }

    @Override
    public void read(PacketDecoder packetDecoder) throws PacketReadException {
        try {
            sharedSecretLength = packetDecoder.readVarInt();
            sharedSecret = new byte[sharedSecretLength];
            packetDecoder.readFully(sharedSecret);
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
        return "EncryptionResponse{" +
                "sharedSecretLength=" + sharedSecretLength +
                ", sharedSecret=" + Arrays.toString(sharedSecret) +
                ", verifyTokenLength=" + verifyTokenLength +
                ", verifyToken=" + Arrays.toString(verifyToken) +
                '}';
    }
}
