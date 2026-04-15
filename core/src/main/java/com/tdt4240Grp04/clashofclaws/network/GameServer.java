package com.tdt4240Grp04.clashofclaws.network;

import com.badlogic.gdx.Net;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    // thread-safe map to store all currently connected players and their latest state
    private static ConcurrentHashMap<Integer, Network.PlayerConnected> players = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Network.KibbleData> kibbles = new ConcurrentHashMap<>();
    private static int kibbleIdCounter = 0;
    private static int playersInLobby = 0;

    public static void main(String[] args) {

        for(int i = 0; i < 200; i++) {
            Network.KibbleData k = new Network.KibbleData();
            k.id = kibbleIdCounter++;
            k.x = (float) (Math.random() * 200f); // MAP_WIDTH
            k.y = (float) (Math.random() * 200f); // MAP_HEIGHT
            kibbles.put(k.id, k);
        }

        try {
            Server server = new Server(16384, 16384);
            Network.register(server);
            server.start();

            int tcpPort = 54555;
            int udpPort = 54777;
            server.bind(tcpPort, udpPort);
            System.out.println("Game Server is successfully running on TCP port: " + tcpPort);

            server.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    System.out.println("A new client connected! ID: " + connection.getID());
                    Network.PlayerConnected newPlayer = new Network.PlayerConnected();
                    newPlayer.id = connection.getID();
                    newPlayer.x = 100f;
                    newPlayer.y = 100f;

                    players.put(newPlayer.id, newPlayer);
                }

                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Network.JoinLobby) {
                        Network.JoinLobby join = (Network.JoinLobby) object;
                        playersInLobby++;

                        Network.PlayerConnected knownPlayer = players.get(connection.getID());
                        if (knownPlayer != null) {
                            knownPlayer.name = join.name;
                            knownPlayer.catIndex = join.catIndex;
                        }

                        Network.LobbyUpdate update = new Network.LobbyUpdate();
                        update.currentPlayers = playersInLobby;
                        server.sendToAllTCP(update);

                        // if 2 players are in, start the game
                        if (playersInLobby >= 2) {
                            System.out.println("2 Players connected! Starting game.");
                            server.sendToAllTCP(new Network.GameStart());
                        }
                    }
                    else if (object instanceof Network.ClientReady) {
                        // send all the kibbles to the newly ready player
                        Network.KibbleInitialSync syncMsg = new Network.KibbleInitialSync();
                        syncMsg.kibbles = new ArrayList<>(kibbles.values());
                        server.sendToTCP(connection.getID(), syncMsg);

                        // tell the newly ready player about everyone else
                        for (Network.PlayerConnected existingPlayer: players.values()) {
                            if (existingPlayer.id != connection.getID()) {
                                server.sendToTCP(connection.getID(), existingPlayer);
                            }
                        }

                        // tell everyone else that this player has entered the game
                        Network.PlayerConnected thisPlayer = players.get(connection.getID());
                        if (thisPlayer != null) {
                            server.sendToAllExceptTCP(connection.getID(), thisPlayer);
                        }
                    }
                    else if (object instanceof Network.PlayerMoved) {
                        Network.PlayerMoved moveEvent = (Network.PlayerMoved) object;

                        moveEvent.id = connection.getID();

                        // update server's record of where player is
                        Network.PlayerConnected knownPlayer = players.get(connection.getID());
                        if (knownPlayer != null) {
                            knownPlayer.x = moveEvent.x;
                            knownPlayer.y = moveEvent.y;
                        }

                        // send the movement to all other connected clients
                        server.sendToAllExceptTCP(connection.getID(), moveEvent);
                    }
                    else if (object instanceof Network.KibbleEaten) {
                        Network.KibbleEaten eatenEvent = (Network.KibbleEaten) object;
                        // Check if kibble still exists on server to prevent double-eating
                        if (kibbles.containsKey(eatenEvent.kibbleId)) {
                            kibbles.remove(eatenEvent.kibbleId);
                            // Broadcast to everyone else that this kibble is gone
                            server.sendToAllExceptTCP(connection.getID(), eatenEvent);
                        }
                    }
                    else if (object instanceof Network.CatDefeated) {
                        System.out.println("A cat died");
                        Network.CatDefeated defeatEvent = (Network.CatDefeated) object;
                        server.sendToAllTCP(defeatEvent);
                    }

                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Client disconnected. ID: " + connection.getID());

                    if (players.containsKey(connection.getID())) {
                        players.remove(connection.getID());
                        playersInLobby--;

                        Network.LobbyUpdate update = new Network.LobbyUpdate();
                        update.currentPlayers = playersInLobby;
                        server.sendToAllTCP(update);

                        Network.PlayerDisconnected disconnectedMsg = new Network.PlayerDisconnected();
                        disconnectedMsg.id = connection.getID();
                        server.sendToAllTCP(disconnectedMsg);
                    }
                }
            });

        } catch (IOException e) {
            System.err.println("Server failed to start. Is the port already in use?");
            e.printStackTrace();
        }
    }
}
