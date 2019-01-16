package mreze.p2p.util;

import java.io.Serializable;
import java.net.InetAddress;

public class AddressPair implements Serializable {

    private InetAddress ipAddress;
    private int port;

    public AddressPair(InetAddress ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "IP: " + this.ipAddress.getHostAddress() + ", Port: " + this.getPort();
    }
}
