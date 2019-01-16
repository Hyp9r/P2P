package mreze.p2p.message;

import java.io.Serializable;

public class Packet implements Serializable {

    private PacketType packetType;
    private Object message;

    public Packet(PacketType packetType, Object message) {
        this.packetType = packetType;
        this.message = message;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public Object getMessage() {
        return message;
    }
}
