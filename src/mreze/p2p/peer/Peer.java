package mreze.p2p.peer;

import mreze.p2p.controller.Controller;
import mreze.p2p.dht.PeerDHT;
import mreze.p2p.network.DataReceiver;
import mreze.p2p.network.DataSender;
import mreze.p2p.util.AddressPair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class Peer {

    private static final int BOOT_PEER_PORT = 5000;

    private byte guid;
    private int port;
    private String file;//file that peer shares

    private AtomicInteger networkSize;

    private PeerDHT peerDHT;
    private DataReceiver dataReceiver;
    private DataSender dataSender;

    private Controller controller;

    public Peer(int port, Controller controller, String file) throws IOException {
        this.port = port;
        this.file = file;
        this.guid = (byte) (this.isBootPeer() ? 0 : -1);

        this.networkSize = new AtomicInteger(0);
        this.peerDHT = new PeerDHT(this);

        this.joinNetwork();
        this.controller = controller;
        this.controller.sendMessageInfo("New peer joined! Port: " + port);
    }

    public byte getGuid() {
        return guid;
    }

    public void setGuid(byte guid) {
        this.guid = guid;
    }

    public String getFileName(){
        return this.file;
    }

    public PeerDHT getPeerDHT() {
        return peerDHT;
    }

    public AtomicInteger getNetworkSize() {
        return networkSize;
    }

    public boolean isBootPeer() {
        return this.port == BOOT_PEER_PORT;
    }

    public DataReceiver getDataReceiver() {
        return this.dataReceiver;
    }

    public DataSender getDataSender() {
        return this.dataSender;
    }

    public Controller getController() {
        return this.controller;
    }

    public AddressPair getAddressPair() {
        try {
            return new AddressPair(InetAddress.getLocalHost(), this.port);
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void joinNetwork() throws IOException {
        this.dataReceiver = new DataReceiver(this, this.port);
        this.dataSender = new DataSender(this, BOOT_PEER_PORT);

        this.dataReceiver.start();
        this.dataSender.start();
    }
}
