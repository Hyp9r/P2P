package mreze.p2p.network;

import mreze.p2p.message.Packet;
import mreze.p2p.message.PacketType;
import mreze.p2p.peer.Peer;
import mreze.p2p.util.AddressPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DataReceiver extends Thread {

    private Peer peer;
    private ServerSocket serverSocket;

    public DataReceiver(Peer peer,  int port) throws IOException {
        this.peer = peer;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        for(;;) {
            try {
                Socket socket = this.serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                while(socket.isConnected()) {
                    Packet packet = (Packet) input.readObject();
					
                    switch(packet.getPacketType()) {
                        case JOIN: {
                            if(!this.peer.isBootPeer()) return;

                            byte guid = (byte) this.peer.getNetworkSize().incrementAndGet();

                            output.writeObject(new Packet(PacketType.GUID, guid));
                            output.flush();

                            AddressPair address = (AddressPair) packet.getMessage();
                            this.peer.getPeerDHT().addPeer(guid, address);
                            this.peer.getController().sendMessageInfo("Peer connected! Sent guid: " + guid);
                            break;
                        }
                        case LEAVE: {
                            byte guid = (byte) packet.getMessage();
                            this.peer.getPeerDHT().removePeer(guid);

                            this.peer.getController().sendMessageInfo("Peer disconnected! Guid: " + guid);
                            break;
                        }
                        case FIND: {
                            byte guid = (byte) packet.getMessage();
                            byte closest = this.peer.getPeerDHT().findClosest(guid);

                            output.writeObject(new Packet(PacketType.FOUND, closest));
                            output.flush();

                            System.out.println("Finding closest guid to: " + guid + "! Found: " + closest);
                            break;
                        }
                        case REQUEST_DHT: {
                            if(!this.peer.isBootPeer()) return;

                            output.writeObject(new Packet(PacketType.DHT, this.peer.getPeerDHT().getReferences()));
                            output.flush();
                            break;
                        }
                        case MESSAGE: {

                        }
                    }
                }

            } catch(IOException | ClassNotFoundException ignored) {

            }
        }
    }

    public void close() {
        try {
            this.serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
