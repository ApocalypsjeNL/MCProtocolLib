package nl.apocalypsje.protocollib.packet;

import nl.apocalypsje.protocollib.protocol.packet.Packet;

public interface IPacketHandler {

    void handle(Packet packet);
}
