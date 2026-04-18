package com.tdt4240Grp04.clashofclaws.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS_TO_START = 2;

    private static ConcurrentHashMap<Integer, Network.PlayerConnected> players = new ConcurrentHashMap<>();

    // Room management
    private static ConcurrentHashMap<String, List<Connection>> rooms = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, String> playerRooms = new ConcurrentHashMap<>();
    // Per-room kibbles
    private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Network.KibbleData>> roomKibbles = new ConcurrentHashMap<>();
    private static int kibbleIdCounter = 0;

    // Per-room powerups
    private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Network.PowerupSpawned>> roomPowerups
        = new ConcurrentHashMap<>();
    private static int powerupIdCounter = 0;
    private static final java.util.Timer powerupTimer = new java.util.Timer(true);
    private static final float[] POWERUP_DURATIONS = { 0f, 5f, 8f, 6f }; // index = type
    private static final long POWERUP_RESPAWN_MS = 20_000L;
    private static final int POWERUPS_PER_TYPE = 8;

    // Public matchmaking queue
    private static List<Connection> publicQueue = new ArrayList<>();

    private static Server server;

    public static void main(String[] args) {
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

                        broadcastLobbyUpdate(room);
                        System.out.println("Room created: " + code);
                    }

                    else if (object instanceof Network.JoinLobby) {
                        Network.JoinLobby msg = (Network.JoinLobby) object;
                        Network.PlayerConnected p = players.get(connection.getID());
                        if (p != null) { p.name = msg.name; p.catIndex = msg.catIndex; }

                        if (msg.roomCode != null && !msg.roomCode.isEmpty()) {
                            // Private room join
                            String code = msg.roomCode.toUpperCase();
                            List<Connection> room = rooms.get(code);
                            if (room == null) {
                                Network.RoomError err = new Network.RoomError();
                                err.message = "Room not found: " + msg.roomCode;
                                server.sendToTCP(connection.getID(), err);
                                return;
                            }
                            if (room.size() >= MAX_PLAYERS) {
                                Network.RoomError err = new Network.RoomError();
                                err.message = "Room is full (max " + MAX_PLAYERS + ")";
                                server.sendToTCP(connection.getID(), err);
                                return;
                            }
                            room.add(connection);
                            playerRooms.put(connection.getID(), code);

                            Network.RoomJoined joined = new Network.RoomJoined();
                            joined.roomCode = code;
                            server.sendToTCP(connection.getID(), joined);

                            broadcastLobbyUpdate(room);
                            if (room.size() >= MAX_PLAYERS) startGame(code, room);
                        } else {
                            // Public matchmaking — join shared queue, start when MIN reached
                            synchronized (publicQueue) {
                                if (publicQueue.size() >= MAX_PLAYERS) {
                                    // Queue full, reject until current batch starts
                                    Network.RoomError err = new Network.RoomError();
                                    err.message = "Queue full, please wait...";
                                    server.sendToTCP(connection.getID(), err);
                                    return;
                                }
                                publicQueue.add(connection);
                                broadcastPublicLobbyUpdate();
                            }
                        }
                    }

                    else if (object instanceof Network.RequestStartGame) {
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode != null) {
                            // Private room host starts game
                            List<Connection> room = rooms.get(roomCode);
                            if (room != null && room.size() >= MIN_PLAYERS_TO_START) {
                                startGame(roomCode, room);
                            }
                        } else {
                            // Public queue player starts game
                            synchronized (publicQueue) {
                                if (publicQueue.contains(connection)) {
                                    if (publicQueue.size() >= MIN_PLAYERS_TO_START) {
                                        int count = Math.min(publicQueue.size(), MAX_PLAYERS);
                                        List<Connection> gameRoom = new ArrayList<>(publicQueue.subList(0, count));
                                        for (int i = 0; i < count; i++) publicQueue.remove(0);
                                        String code = generateRoomCode();
                                        rooms.put(code, gameRoom);
                                        for (Connection c : gameRoom) playerRooms.put(c.getID(), code);
                                        startGame(code, gameRoom);
                                    } else {
                                        Network.RoomError err = new Network.RoomError();
                                        err.message = "Need at least 2 players to start";
                                        server.sendToTCP(connection.getID(), err);
                                    }
                                }
                            }
                        }
                    }

                    else if (object instanceof Network.ClientReady) {
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode == null) return;
                        List<Connection> room = rooms.get(roomCode);
                        if (room == null) return;

                        // Send this room's kibbles
                        ConcurrentHashMap<Integer, Network.KibbleData> kibbles = roomKibbles.get(roomCode);
                        Network.KibbleInitialSync syncMsg = new Network.KibbleInitialSync();
                        syncMsg.kibbles = kibbles != null ? new ArrayList<>(kibbles.values()) : new ArrayList<>();
                        server.sendToTCP(connection.getID(), syncMsg);

                        // Re-send existing powerups (client listener may not have been ready when they were first broadcast)
                        ConcurrentHashMap<Integer, Network.PowerupSpawned> powerups = roomPowerups.get(roomCode);
                        if (powerups != null) {
                            for (Network.PowerupSpawned sp : powerups.values()) {
                                server.sendToTCP(connection.getID(), sp);
                            }
                        }

                        // Send existing roommates to this player
                        for (Connection roommate : room) {
                            if (roommate.getID() == connection.getID()) continue;
                            Network.PlayerConnected existing = players.get(roommate.getID());
                            if (existing != null) server.sendToTCP(connection.getID(), existing);
                        }

                        // Broadcast this player to existing roommates
                        Network.PlayerConnected thisPlayer = players.get(connection.getID());
                        if (thisPlayer != null) {
                            for (Connection roommate : room) {
                                if (roommate.getID() != connection.getID()) {
                                    server.sendToTCP(roommate.getID(), thisPlayer);
                                }
                            }
                        }
                    }

                    else if (object instanceof Network.PlayerMoved) {
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode == null) return;
                        Network.PlayerMoved moveEvent = (Network.PlayerMoved) object;
                        moveEvent.id = connection.getID();
                        Network.PlayerConnected p = players.get(connection.getID());
                        if (p != null) { p.x = moveEvent.x; p.y = moveEvent.y; }
                        sendToRoomExceptUDP(roomCode, connection.getID(), moveEvent);
                    }

                    else if (object instanceof Network.KibbleEaten) {
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode == null) return;
                        Network.KibbleEaten eaten = (Network.KibbleEaten) object;
                        ConcurrentHashMap<Integer, Network.KibbleData> kibbles = roomKibbles.get(roomCode);
                        if (kibbles != null && kibbles.containsKey(eaten.kibbleId)) {
                            kibbles.remove(eaten.kibbleId);
                            sendToRoomExceptTCP(roomCode, connection.getID(), eaten);

                            final String respawnRoom = roomCode;
                            powerupTimer.schedule(new java.util.TimerTask() {
                                @Override public void run() {
                                    List<Connection> r = rooms.get(respawnRoom);
                                    if (r == null || r.isEmpty()) return;
                                    ConcurrentHashMap<Integer, Network.KibbleData> rk = roomKibbles.get(respawnRoom);
                                    if (rk == null) return;
                                    Network.KibbleData newKibble = new Network.KibbleData();
                                    synchronized (GameServer.class) { newKibble.id = kibbleIdCounter++; }
                                    newKibble.x = (float)(Math.random() * 200f);
                                    newKibble.y = (float)(Math.random() * 200f);
                                    rk.put(newKibble.id, newKibble);
                                    for (Connection c : r) server.sendToTCP(c.getID(), newKibble);
                                }
                            }, 10_000L);
                        }
                    }

                    else if (object instanceof Network.CatDefeated) {
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode == null) return;
                        sendToRoomTCP(roomCode, (Network.CatDefeated) object);
                    }

                    else if (object instanceof Network.PowerupCollected) {
                        Network.PowerupCollected msg = (Network.PowerupCollected) object;
                        String roomCode = playerRooms.get(connection.getID());
                        if (roomCode == null) return;
                        ConcurrentHashMap<Integer, Network.PowerupSpawned> powerups = roomPowerups.get(roomCode);
                        if (powerups == null) return;
                        Network.PowerupSpawned spawn = powerups.remove(msg.powerupId);
                        if (spawn == null) return; // already collected

                        Network.PowerupEffect effect = new Network.PowerupEffect();
                        effect.type     = spawn.type;
                        effect.duration = POWERUP_DURATIONS[spawn.type];
                        server.sendToTCP(connection.getID(), effect);

                        Network.PowerupDespawned despawn = new Network.PowerupDespawned();
                        despawn.powerupId = msg.powerupId;
                        List<Connection> room = rooms.get(roomCode);
                        if (room != null) {
                            for (Connection c : room) {
                                if (c.getID() != connection.getID())
                                    server.sendToTCP(c.getID(), despawn);
                            }
                        }

                        final int respawnType = spawn.type;
                        final String respawnRoom = roomCode;
                        powerupTimer.schedule(new java.util.TimerTask() {
                            @Override public void run() {
                                List<Connection> r = rooms.get(respawnRoom);
                                if (r == null || r.isEmpty()) return;
                                ConcurrentHashMap<Integer, Network.PowerupSpawned> rp = roomPowerups.get(respawnRoom);
                                if (rp == null) return;
                                Network.PowerupSpawned newSpawn = new Network.PowerupSpawned();
                                synchronized (GameServer.class) { newSpawn.powerupId = powerupIdCounter++; }
                                newSpawn.type = respawnType;
                                newSpawn.x = 10f + (float)(Math.random() * 180f);
                                newSpawn.y = 10f + (float)(Math.random() * 180f);
                                rp.put(newSpawn.powerupId, newSpawn);
                                for (Connection c : r) server.sendToTCP(c.getID(), newSpawn);
                            }
                        }, POWERUP_RESPAWN_MS);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Client disconnected: " + connection.getID());
                    players.remove(connection.getID());

                    String roomCode = playerRooms.remove(connection.getID());

                    Network.PlayerDisconnected msg = new Network.PlayerDisconnected();
                    msg.id = connection.getID();

                    if (roomCode != null) {
                        List<Connection> room = rooms.get(roomCode);
                        if (room != null) {
                            room.remove(connection);
                            // Notify remaining roommates
                            for (Connection roommate : room) {
                                server.sendToTCP(roommate.getID(), msg);
                            }
                            if (room.isEmpty()) {
                                rooms.remove(roomCode);
                                roomKibbles.remove(roomCode);
                                roomPowerups.remove(roomCode);
                            } else {
                                broadcastLobbyUpdate(room);
                            }
                        }
                    } else {
                        synchronized (publicQueue) { publicQueue.remove(connection); }
                    }
                }
            });

        } catch (IOException e) {
            System.err.println("Server failed to start.");
            e.printStackTrace();
        }
    }

    private static void spawnRoomPowerups(String roomCode) {
        ConcurrentHashMap<Integer, Network.PowerupSpawned> powerups = new ConcurrentHashMap<>();
        List<Connection> room = rooms.get(roomCode);
        if (room == null) return;
        for (int type = 1; type <= 3; type++) {
            for (int i = 0; i < POWERUPS_PER_TYPE; i++) {
                Network.PowerupSpawned spawn = new Network.PowerupSpawned();
                synchronized (GameServer.class) { spawn.powerupId = powerupIdCounter++; }
                spawn.type = type;
                spawn.x = 10f + (float)(Math.random() * 180f);
                spawn.y = 10f + (float)(Math.random() * 180f);
                powerups.put(spawn.powerupId, spawn);
                for (Connection c : room) server.sendToTCP(c.getID(), spawn);
            }
        }
        roomPowerups.put(roomCode, powerups);
    }

    private static void startGame(String roomCode, List<Connection> room) {
        System.out.println("Starting game for room " + roomCode + " with " + room.size() + " players");
        // Generate kibbles for this room
        ConcurrentHashMap<Integer, Network.KibbleData> kibbles = new ConcurrentHashMap<>();
        synchronized (GameServer.class) {
            for (int i = 0; i < 200; i++) {
                Network.KibbleData k = new Network.KibbleData();
                k.id = kibbleIdCounter++;
                k.x = (float)(Math.random() * 200f);
                k.y = (float)(Math.random() * 200f);
                kibbles.put(k.id, k);
            }
        }
        roomKibbles.put(roomCode, kibbles);
        for (Connection c : room) server.sendToTCP(c.getID(), new Network.GameStart());
        spawnRoomPowerups(roomCode);
    }

    private static void sendToRoomTCP(String roomCode, Object msg) {
        List<Connection> room = rooms.get(roomCode);
        if (room == null) return;
        for (Connection c : room) server.sendToTCP(c.getID(), msg);
    }

    private static void sendToRoomExceptTCP(String roomCode, int excludeId, Object msg) {
        List<Connection> room = rooms.get(roomCode);
        if (room == null) return;
        for (Connection c : room) {
            if (c.getID() != excludeId) server.sendToTCP(c.getID(), msg);
        }
    }

    private static void sendToRoomExceptUDP(String roomCode, int excludeId, Object msg) {
        List<Connection> room = rooms.get(roomCode);
        if (room == null) return;
        for (Connection c : room) {
            if (c.getID() != excludeId) server.sendToUDP(c.getID(), msg);
        }
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
