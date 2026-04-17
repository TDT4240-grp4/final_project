package com.tdt4240Grp04.clashofclaws.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private static final int MAX_ROOM_PLAYERS = 2;

    // All connected players and their state
    private static ConcurrentHashMap<Integer, Network.PlayerConnected> players = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Network.KibbleData> kibbles = new ConcurrentHashMap<>();
    private static int kibbleIdCounter = 0;

    // Room management
    private static ConcurrentHashMap<String, List<Connection>> rooms = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, String> playerRooms = new ConcurrentHashMap<>();

    // Public matchmaking queue
    private static List<Connection> publicQueue = new ArrayList<>();

    private static Server server;

    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            Network.KibbleData k = new Network.KibbleData();
            k.id = kibbleIdCounter++;
            k.x = (float)(Math.random() * 200f);
            k.y = (float)(Math.random() * 200f);
            kibbles.put(k.id, k);
        }

        try {
            server = new Server(16384, 16384);
            Network.register(server);
            server.start();
            server.bind(54555, 54777);
            System.out.println("Server running on TCP 54555 / UDP 54777");

            server.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    System.out.println("Client connected: " + connection.getID());
                    Network.PlayerConnected p = new Network.PlayerConnected();
                    p.id = connection.getID();
                    p.x = (float)(Math.random() * 180f) + 10f;
                    p.y = (float)(Math.random() * 180f) + 10f;
                    players.put(p.id, p);
                }

                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Network.CreateRoom) {
                        Network.CreateRoom msg = (Network.CreateRoom) object;
                        String code = generateRoomCode();
                        List<Connection> room = new ArrayList<>();
                        room.add(connection);
                        rooms.put(code, room);
                        playerRooms.put(connection.getID(), code);

                        Network.PlayerConnected p = players.get(connection.getID());
                        if (p != null) { p.name = msg.name; p.catIndex = msg.catIndex; }

                        Network.RoomJoined response = new Network.RoomJoined();
                        response.roomCode = code;
                        server.sendToTCP(connection.getID(), response);

                        Network.LobbyUpdate update = new Network.LobbyUpdate();
                        update.currentPlayers = 1;
                        server.sendToTCP(connection.getID(), update);
                        System.out.println("Room created: " + code);
                    }

                    else if (object instanceof Network.JoinLobby) {
                        Network.JoinLobby msg = (Network.JoinLobby) object;
                        Network.PlayerConnected p = players.get(connection.getID());
                        if (p != null) { p.name = msg.name; p.catIndex = msg.catIndex; }

                        if (msg.roomCode != null && !msg.roomCode.isEmpty()) {
                            // Private room join
                            List<Connection> room = rooms.get(msg.roomCode.toUpperCase());
                            if (room == null) {
                                Network.RoomError err = new Network.RoomError();
                                err.message = "Room not found: " + msg.roomCode;
                                server.sendToTCP(connection.getID(), err);
                                return;
                            }
                            room.add(connection);
                            playerRooms.put(connection.getID(), msg.roomCode.toUpperCase());

                            Network.RoomJoined joined = new Network.RoomJoined();
                            joined.roomCode = msg.roomCode.toUpperCase();
                            server.sendToTCP(connection.getID(), joined);

                            broadcastLobbyUpdate(room);
                            if (room.size() >= MAX_ROOM_PLAYERS) startGame(room);
                        } else {
                            // Public matchmaking
                            synchronized (publicQueue) {
                                publicQueue.add(connection);
                                broadcastPublicLobbyUpdate();
                                if (publicQueue.size() >= MAX_ROOM_PLAYERS) {
                                    List<Connection> gameRoom = new ArrayList<>(publicQueue);
                                    publicQueue.clear();
                                    String code = generateRoomCode();
                                    rooms.put(code, gameRoom);
                                    for (Connection c : gameRoom) playerRooms.put(c.getID(), code);
                                    startGame(gameRoom);
                                }
                            }
                        }
                    }

                    else if (object instanceof Network.ClientReady) {
                        Network.KibbleInitialSync syncMsg = new Network.KibbleInitialSync();
                        syncMsg.kibbles = new ArrayList<>(kibbles.values());
                        server.sendToTCP(connection.getID(), syncMsg);

                        for (Network.PlayerConnected existing : players.values()) {
                            if (existing.id != connection.getID()) {
                                server.sendToTCP(connection.getID(), existing);
                            }
                        }
                        Network.PlayerConnected thisPlayer = players.get(connection.getID());
                        if (thisPlayer != null) {
                            server.sendToAllExceptTCP(connection.getID(), thisPlayer);
                        }
                    }

                    else if (object instanceof Network.PlayerMoved) {
                        Network.PlayerMoved moveEvent = (Network.PlayerMoved) object;
                        moveEvent.id = connection.getID();
                        Network.PlayerConnected p = players.get(connection.getID());
                        if (p != null) { p.x = moveEvent.x; p.y = moveEvent.y; }
                        server.sendToAllExceptUDP(connection.getID(), moveEvent);
                    }

                    else if (object instanceof Network.KibbleEaten) {
                        Network.KibbleEaten eaten = (Network.KibbleEaten) object;
                        if (kibbles.containsKey(eaten.kibbleId)) {
                            kibbles.remove(eaten.kibbleId);
                            server.sendToAllExceptTCP(connection.getID(), eaten);
                        }
                    }

                    else if (object instanceof Network.CatDefeated) {
                        server.sendToAllTCP((Network.CatDefeated) object);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Client disconnected: " + connection.getID());
                    players.remove(connection.getID());

                    String roomCode = playerRooms.remove(connection.getID());
                    if (roomCode != null) {
                        List<Connection> room = rooms.get(roomCode);
                        if (room != null) {
                            room.remove(connection);
                            if (room.isEmpty()) rooms.remove(roomCode);
                            else broadcastLobbyUpdate(room);
                        }
                    } else {
                        synchronized (publicQueue) { publicQueue.remove(connection); }
                    }

                    Network.PlayerDisconnected msg = new Network.PlayerDisconnected();
                    msg.id = connection.getID();
                    server.sendToAllTCP(msg);
                }
            });

        } catch (IOException e) {
            System.err.println("Server failed to start.");
            e.printStackTrace();
        }
    }

    private static void startGame(List<Connection> room) {
        System.out.println("Starting game for room with " + room.size() + " players");
        for (Connection c : room) server.sendToTCP(c.getID(), new Network.GameStart());
    }

    private static void broadcastLobbyUpdate(List<Connection> room) {
        Network.LobbyUpdate update = new Network.LobbyUpdate();
        update.currentPlayers = room.size();
        for (Connection c : room) server.sendToTCP(c.getID(), update);
    }

    private static void broadcastPublicLobbyUpdate() {
        Network.LobbyUpdate update = new Network.LobbyUpdate();
        update.currentPlayers = publicQueue.size();
        for (Connection c : publicQueue) server.sendToTCP(c.getID(), update);
    }

    private static String generateRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        String code = sb.toString();
        return rooms.containsKey(code) ? generateRoomCode() : code;
    }
}
