package com.tdt4240Grp04.clashofclaws.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;

public class Network {

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(PlayerConnected.class);
        kryo.register(PlayerDisconnected.class);
        kryo.register(PlayerMoved.class);
        kryo.register(KibbleData.class);
        kryo.register(KibbleInitialSync.class);
        kryo.register(KibbleEaten.class);
        kryo.register(ArrayList.class);
        kryo.register(CatDefeated.class);
        kryo.register(JoinLobby.class);
        kryo.register(LobbyUpdate.class);
        kryo.register(GameStart.class);
        kryo.register(ClientReady.class);
        kryo.register(CreateRoom.class);
        kryo.register(RoomJoined.class);
        kryo.register(RoomError.class);
    }

    public static class JoinLobby {
        public String name;
        public int catIndex;
        public String roomCode; // null = public matchmaking
    }

    public static class CreateRoom {
        public String name;
        public int catIndex;
    }

    public static class RoomJoined {
        public String roomCode; // server sends back the generated or matched code
    }

    public static class RoomError {
        public String message; // e.g. "Room not found"
    }

    public static class LobbyUpdate {
        public int currentPlayers;
    }

    public static class GameStart { }

    public static class ClientReady { }

    public static class KibbleData {
        public int id;
        public float x, y;
    }

    public static class KibbleInitialSync {
        public ArrayList<KibbleData> kibbles;
    }

    public static class KibbleEaten {
        public int kibbleId;
        public int eatenByPlayerId;
    }

    public static class PlayerConnected {
        public int id;
        public float x, y;
        public String name;
        public int catIndex;
    }

    public static class PlayerDisconnected {
        public int id;
    }

    public static class PlayerMoved {
        public int id;
        public float x, y;
        public int score;
    }

    public static class CatDefeated {
        public int winnerId;
        public int loserId;
    }
}
