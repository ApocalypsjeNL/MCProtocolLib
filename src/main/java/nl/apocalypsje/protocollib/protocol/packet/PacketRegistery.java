package nl.apocalypsje.protocollib.protocol.packet;

import nl.apocalypsje.protocollib.protocol.ProtocolState;
import nl.apocalypsje.protocollib.protocol.packet.handshake.Handshake;
import nl.apocalypsje.protocollib.protocol.packet.login.*;

import java.util.HashMap;
import java.util.Map;

//TODO Add multi version support when we're going to use the game
public class PacketRegistery {

    private static Map<Integer, Class> handShakeServer = new HashMap<>();

    private static Map<Integer, Class> loginServer = new HashMap<>();
    private static Map<Integer, Class> loginClient = new HashMap<>();

    static {
        handShakeServer.put(0x00, Handshake.class);

        loginServer.put(0x00, LoginStart.class);
        //TODO Add kick packet
        loginClient.put(0x01, EncryptionRequest.class);
        loginServer.put(0x01, EncryptionResponse.class);
        loginClient.put(0x03, SetCompression.class);
        loginClient.put(0x02, LoginSuccess.class);
    }

    public static Packet getPacket(int id, ProtocolState state, boolean server) {
        try {
            if(state.equals(ProtocolState.HANDSHAKE)) {
                return (Packet) handShakeServer.get(id).newInstance();
            } else if(state.equals(ProtocolState.LOGIN)) {
                if(server) {
                    return (Packet) loginServer.get(id).newInstance();
                } else {
                    return (Packet) loginClient.get(id).newInstance();
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
