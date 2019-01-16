package mreze.p2p.network;

import mreze.p2p.message.Packet;
import mreze.p2p.message.PacketType;
import mreze.p2p.peer.Peer;
import mreze.p2p.util.AddressPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class DataSender extends Thread {

    private Peer peer;
    private Socket socket;

    public DataSender(Peer peer, int bootPeerPort) throws IOException {
        this.peer = peer;

        if(!this.peer.isBootPeer()) {
            this.socket = new Socket(InetAddress.getLocalHost().getHostAddress(), bootPeerPort);
        }
    }

    @Override
    public void run() {
        if(this.peer.isBootPeer()) return;

        try {
            ObjectOutputStream output = new ObjectOutputStream(this.socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());

            output.writeObject(new Packet(PacketType.JOIN, this.peer.getAddressPair()));
            output.flush();

            this.handleInputMessage(input);

            output.writeObject(new Packet(PacketType.REQUEST_DHT, new Object[0]));
            output.flush();

            this.handleInputMessage(input);

            output.close();
            input.close();

        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleInputMessage(ObjectInputStream input) throws IOException, ClassNotFoundException {
        Packet packet = (Packet) input.readObject();

        switch(packet.getPacketType()) {
            case GUID: {
                Byte guid = (Byte) packet.getMessage();
                this.peer.setGuid(guid);

                this.peer.getController().sendMessageInfo("Guid: " + guid);
                break;
            }
            case DHT: {
                Map references = (Map) packet.getMessage();
                this.peer.getPeerDHT().mapReferences(references);

                System.out.println("References: " + references);
            }
        }
    }

    public void sendFindPacket(AddressPair address, byte guid) {
        try {

            Socket socket = new Socket(address.getIpAddress(), address.getPort());

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            byte current = this.peer.getGuid();

            while(current != guid) {
                output.writeObject(new Packet(PacketType.FIND, guid));
                output.flush();

                Packet packet = (Packet) input.readObject();
                current = (byte) packet.getMessage();

                System.out.println("Peer: " + current + " --> ");
            }

            output.close();

        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void broadcastLeaveMessage() {
        this.peer.getPeerDHT().broadcastLeaveMessage();
    }

    public void close() {
        if(this.peer.isBootPeer()) return;

        try {
            this.socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
