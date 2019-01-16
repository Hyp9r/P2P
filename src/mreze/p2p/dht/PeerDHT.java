package mreze.p2p.dht;

import mreze.p2p.message.Packet;
import mreze.p2p.message.PacketType;
import mreze.p2p.peer.Peer;
import mreze.p2p.util.AddressPair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PeerDHT {

    private static final int MAX_REFERENCES = 8;

    private Peer peer;
    private Map<Byte, AddressPair> references;

    public PeerDHT(Peer peer) {
        this.peer = peer;

        this.references = new HashMap<>(MAX_REFERENCES);

        if(this.peer.isBootPeer()) this.references.put(this.peer.getGuid(), this.peer.getAddressPair());
    }

    public void mapReferences(Map<Byte, AddressPair> bootPeerReferences) {
        if(bootPeerReferences.size() < MAX_REFERENCES) {
            this.references = bootPeerReferences;
            return;
        }

        for(int i = 0; i < MAX_REFERENCES; i++) {
            byte power = (byte) Math.pow(2, i);


        }
    }

    public void find(byte guid) {
        if(this.peer.getGuid() == guid) {
            System.out.println("Peer: " + this.peer.getGuid() + " --> " + "Peer: " + guid);
            return;
        }

        int closest = this.findClosest(guid);

        if(closest == guid) {
            System.out.println("Peer: " + this.peer.getGuid() + " --> " + "Peer: " + closest);
            return;
        }

        System.out.println("Peer: " + this.peer.getGuid() + " --> ");

        AddressPair address = this.references.get((byte) closest);
        this.peer.getDataSender().sendFindPacket(address, guid);
    }

    public byte findClosest(byte guid) {
        byte closest = this.peer.getGuid();

        for(Byte id : this.references.keySet()) {
            int distance = this.distance(guid, id);
            if(distance < this.distance(closest, guid)) closest = id;
        }

        return closest;
    }

    public void addPeer(byte guid, AddressPair addressPair) {
        this.references.put(guid, addressPair);
    }

    public void removePeer(byte guid) {
        this.references.remove(guid);
    }

    // Kademlia distance algorithm
    private int distance(int A, int B) {
        return A ^ B;
    }

    public void broadcastLeaveMessage() {
        this.references.values().forEach(address -> {
            if(address.getPort() == this.peer.getAddressPair().getPort()) return;

            try {

                Socket socket = new Socket(address.getIpAddress(), address.getPort());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                output.writeObject(new Packet(PacketType.LEAVE, this.peer.getGuid()));
                output.flush();

                output.close();

            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<Byte, AddressPair> getReferences() {
        return references;
    }
}
