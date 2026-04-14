package com.tdt4240Grp04.clashofclaws.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tdt4240Grp04.clashofclaws.network.Network;

import java.io.IOException;

public class GameClient {
    private Client client;
    private String serverIp;
    private int tcpPort;
    private int udpPort;

    public GameClient(String serverIp, int tcpPort, int udpPort) {
        this.serverIp = serverIp;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        client = new Client();

        Network.register(client);
    }

    public void connect() throws IOException {
        client.start();
        client.connect(5000, serverIp, tcpPort, udpPort);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                // Handle received objects
            }

            public void disconnected(Connection connection) {
                // Handle disconnection
            }
        });
    }

    public void sendTCP(Object object) {
        client.sendTCP(object);
    }

    public void sendUDP(Object object) {
        client.sendUDP(object);
    }

    public void disconnect() {
        client.close();
    }

    public Client getClient() {
        return client;
    }
}
