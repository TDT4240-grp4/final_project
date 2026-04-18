package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tdt4240Grp04.clashofclaws.ecs.EntityFactory;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.MarkedForRemovalComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.OpponentComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.systems.CatBodySystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.DashSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.MovementSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.PhysicsSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.RemovalSystem;
import com.tdt4240Grp04.clashofclaws.audio.AudioManager;
import com.tdt4240Grp04.clashofclaws.listeners.CollisionListener;
import com.tdt4240Grp04.clashofclaws.network.GameClient;
import com.tdt4240Grp04.clashofclaws.network.Network;

import java.io.IOException;
import java.util.HashMap;

public class PlayLogic {
    private static final String TAG = "PlayLogic";
    private Engine engine;
    private Entity player;
    private World world;
    private Texture kibbleTexture;
    private Texture catHeadTexture;
    private EntityFactory entityFactory;
    private GameClient gameClient;
    private HashMap<Integer, Entity> otherPlayers;
    private boolean hadOpponents = false;
    private int killCount = 0;
    private float survivalSeconds = 0f;
    private Listener networkListener;

    private final float MAP_WIDTH = 200f;
    private final float MAP_HEIGHT = 200f;
    public PlayLogic(GameClient gameClient, String name, int catIndex) {
        this.gameClient = gameClient;
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);
        kibbleTexture = new Texture(Gdx.files.internal("kibble.png"));
        catHeadTexture = new Texture(Gdx.files.internal("cat" + (catIndex + 1) + "_head.png"));
        otherPlayers = new HashMap<>();

        world.setContactListener(new CollisionListener(engine, gameClient));

        engine.addSystem(new DashSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new RemovalSystem(world));
        engine.addSystem(new CatBodySystem(world));

        entityFactory = new EntityFactory(engine, world);

        createMapBounds();

        float startX = (float) (Math.random() * (MAP_WIDTH - 20f)) + 10f;
        float startY = (float) (Math.random() * (MAP_HEIGHT - 20f)) + 10f;
        player = entityFactory.createPlayer(startX, startY, getBodyHexColour(catIndex), name, catIndex, catHeadTexture);


        PlayerComponent pComp = player.getComponent(PlayerComponent.class);
        if (pComp != null) {
            pComp.networkID = gameClient.getClient().getID();
        }


        networkListener = new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof Network.PlayerConnected) {
                    Network.PlayerConnected msg = (Network.PlayerConnected) object;
                    Gdx.app.postRunnable(() -> {
                        if (otherPlayers.containsKey(msg.id)) return;
                        Entity newOpponent = entityFactory.createOpponent(msg.id, msg.x, msg.y, getBodyHexColour(msg.catIndex), msg.catIndex, msg.name);
                        otherPlayers.put(msg.id, newOpponent);
                        hadOpponents = true;
                        Gdx.app.log(TAG, "Spawned opponent with ID: " + msg.id);
                    });
                } else if (object instanceof Network.PlayerDisconnected) {
                    Network.PlayerDisconnected msg = (Network.PlayerDisconnected) object;
                    Gdx.app.postRunnable(() -> {
                        Entity playerToRemove = otherPlayers.get(msg.id);
                        if (playerToRemove != null) {
                            engine.removeEntity(playerToRemove);
                            otherPlayers.remove(msg.id);
                        }
                    });
                } else if (object instanceof Network.PlayerMoved) {
                    Network.PlayerMoved msg = (Network.PlayerMoved) object;
                    Gdx.app.postRunnable(() -> {
                        Entity movedPlayer = otherPlayers.get(msg.id);
                        if (movedPlayer != null) {
                            PhysicsComponent physComp = movedPlayer.getComponent(PhysicsComponent.class);
                            physComp.body.setTransform(msg.x, msg.y, 0);

                            OpponentComponent oppComp = movedPlayer.getComponent(OpponentComponent.class);
                            if (oppComp != null) {
                                oppComp.score = msg.score;
                            }
                        }
                    });
                } else if (object instanceof Network.KibbleInitialSync) {
                    Network.KibbleInitialSync msg = (Network.KibbleInitialSync) object;
                    Gdx.app.postRunnable(() -> {
                        for (Network.KibbleData data : msg.kibbles) {
                            entityFactory.createKibble(data.id, data.x, data.y, kibbleTexture);
                        }
                    });
                } else if (object instanceof Network.KibbleEaten) {
                    Network.KibbleEaten msg = (Network.KibbleEaten) object;
                    Gdx.app.postRunnable(() -> {
                        // Find the kibble entity with this ID and mark it for removal
                        for (Entity e : engine.getEntitiesFor(Family.all(KibbleComponent.class).get())) {
                            if (e.getComponent(KibbleComponent.class).id == msg.kibbleId) {
                                e.add(engine.createComponent(MarkedForRemovalComponent.class));
                                break;
                            }
                        }

                        Entity eater = otherPlayers.get(msg.eatenByPlayerId);
                        if (eater != null) {
                            CatBodyComponent body = eater.getComponent(CatBodyComponent.class);
                            if (body != null) {
                                SizeComponent sizeComp = eater.getComponent(SizeComponent.class);
                                body.maxLength = Math.min(body.maxLength + (int)((sizeComp != null) ? sizeComp.growthRate : 5), 500);
                            }
                        }

                        OpponentComponent oppComp = eater.getComponent(OpponentComponent.class);
                        if (oppComp != null) {
                            oppComp.score += 10; // increase opponent's score
                        }
                    });
                } else if (object instanceof Network.CatDefeated) {
                    Network.CatDefeated msg = (Network.CatDefeated) object;
                    Gdx.app.postRunnable(() -> {
                        PlayerComponent pComp = player.getComponent(PlayerComponent.class);
                        int myId = (pComp != null) ? pComp.networkID : -1;

                        int loserScore = 0;
                        if (msg.winnerId == myId) killCount++;
                        Gdx.app.log(TAG, "1111111111111111111");

                        // 1. Handle Loser
                        if (msg.loserId == myId) {
                            // I am the loser
                            if (pComp != null) {
                                loserScore = pComp.score;
                                pComp.isDead = true;
                                AudioManager.getInstance().playDieSound();
                                Gdx.app.log(TAG, "2222222222");
                            }
                        } else {
                            // Opponent is the loser
                            Entity loser = otherPlayers.get(msg.loserId);
                            if (loser != null) {
                                OpponentComponent oppComp = loser.getComponent(OpponentComponent.class);
                                if (oppComp != null) {
                                    loserScore = oppComp.score;
                                    Gdx.app.log(TAG, "33333333");
                                }
                                loser.add(engine.createComponent(MarkedForRemovalComponent.class));
                                otherPlayers.remove(msg.loserId);
                            }
                        }

                        if (msg.winnerId == myId) {
                            // I am the winner
                            if (pComp != null) {
                                pComp.score += loserScore;
                                Gdx.app.log(TAG, "4444444444444");
                            }
                        } else {
                            Entity winner = otherPlayers.get(msg.winnerId);
                            if (winner != null) {
                                OpponentComponent oppComp = winner.getComponent(OpponentComponent.class);
                                if (oppComp != null) {
                                    oppComp.score += loserScore;
                                    Gdx.app.log(TAG, "5555555555");
                                }
                            }
                        }
                    });
                }
            }
        };
        gameClient.getClient().addListener(networkListener);

        gameClient.sendTCP(new Network.ClientReady());
    }

    private String getBodyHexColour(int catIndex) {
        if (catIndex + 1 == 1) {
            return "ffeedb";
        }
        else if (catIndex + 1 == 2) {
            return "2b1803";
        }
        // add on for other cat
        return "ffeedb";
    }

    private void createMapBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        Body boundsBody = world.createBody(bodyDef);

        ChainShape shape = new ChainShape();
        float[] vertices = new float[] {
            0, 0,
            MAP_WIDTH, 0,
            MAP_WIDTH, MAP_HEIGHT,
            0, MAP_HEIGHT,
            0, 0
        };
        shape.createChain(vertices);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        boundsBody.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float dt) {
        if (!isPlayerDead()) survivalSeconds += dt;
        world.step(1/60f, 6, 2);
        engine.update(dt);

        PhysicsComponent physComp = player.getComponent(PhysicsComponent.class);
        PlayerComponent pComp = player.getComponent(PlayerComponent.class);

        if (physComp != null && pComp != null && pComp.networkID != -1) {
            Network.PlayerMoved msg = new Network.PlayerMoved();
            msg.x = physComp.body.getPosition().x;
            msg.y = physComp.body.getPosition().y;
            msg.score = pComp.score;
            msg.id = pComp.networkID;
            gameClient.sendUDP(msg);
        }
    }

    public Engine getEngine() {
        return engine;
    }

    public Entity getPlayer() {
        return player;
    }

    public boolean isPlayerDead() {
        PlayerComponent pComp = player.getComponent(PlayerComponent.class);
        return pComp == null || pComp.isDead;
    }

    public boolean hasPlayerWon() {
        return hadOpponents && otherPlayers.isEmpty() && !isPlayerDead();
    }

    public void dispose() {
        if (networkListener != null) {
            gameClient.getClient().removeListener(networkListener);
        }
        kibbleTexture.dispose();
        catHeadTexture.dispose();
        world.dispose();
        gameClient.disconnect();
    }

    public int getKillCount()        { return killCount; }
    public float getSurvivalSeconds() { return survivalSeconds; }
    public int getScore() {
        PlayerComponent pc = player.getComponent(PlayerComponent.class);
        return pc != null ? pc.score : 0;
    }
    public int getKibbleCount() {
        PlayerComponent pc = player.getComponent(PlayerComponent.class);
        return pc != null ? pc.kibbleCount : 0;
    }
}
