package mreze.p2p.shutdown;

import mreze.p2p.peer.Peer;

public class ShutdownHandler extends Thread {

    private Peer peer;

    public ShutdownHandler(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {
        System.out.println("Peer shutting down!");

        this.peer.getDataSender().broadcastLeaveMessage();
        this.peer.getDataSender().close();
        this.peer.getDataReceiver().close();
    }
}
