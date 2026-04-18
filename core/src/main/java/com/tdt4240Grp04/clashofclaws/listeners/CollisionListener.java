package com.tdt4240Grp04.clashofclaws.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.MarkedForRemovalComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.OpponentComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.network.GameClient;
import com.tdt4240Grp04.clashofclaws.network.Network;

public class CollisionListener implements ContactListener {

    private Engine engine;
    private GameClient gameClient;
    private ComponentMapper<KibbleComponent> kcm = ComponentMapper.getFor(KibbleComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<CatBodyComponent> cbcm = ComponentMapper.getFor(CatBodyComponent.class);
    private ComponentMapper<OpponentComponent> ocm = ComponentMapper.getFor(OpponentComponent.class);
    private ComponentMapper<SizeComponent> scm = ComponentMapper.getFor(SizeComponent.class);

    public CollisionListener(Engine engine, GameClient gameClient) {
        this.engine = engine;
        this.gameClient = gameClient;
    }

    private boolean isCat(Entity e) {
        return pcm.has(e) || ocm.has(e);
    }

    private int getCatScore(Entity e) {
        if (pcm.has(e)) return pcm.get(e).score;
        if (ocm.has(e)) return ocm.get(e).score;
        return 0;
    }

    private int getCatId(Entity e) {
        if (pcm.has(e)) return pcm.get(e).networkID;
        if (ocm.has(e)) return ocm.get(e).networkId;
        return -1;
    }

    private void addCatScore(Entity e, int amount) {
        if (pcm.has(e)) pcm.get(e).score += amount;
        if (ocm.has(e)) ocm.get(e).score += amount;
    }

    private void setCatDead(Entity e) {
        if (pcm.has(e)) {
            pcm.get(e).isDead = true;
        }
        if (ocm.has(e)) {
            ocm.get(e).isDead = true;
        }
    }

    private boolean isCatDead(Entity e) {
        if (pcm.has(e) && pcm.get(e).isDead) return true;
        if (ocm.has(e) && ocm.get(e).isDead) return true;
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getBody().getUserData() instanceof Entity && fixtureB.getBody().getUserData() instanceof Entity) {
            Entity entityA = (Entity) fixtureA.getBody().getUserData();
            Entity entityB = (Entity) fixtureB.getBody().getUserData();

            // Cat-Kibble collision
            if ((kcm.has(entityA) && isCat(entityB)) || (kcm.has(entityB) && isCat(entityA))) {
                handleKibbleCollision(entityA, entityB);
            }
            // Cat-Cat collision (Opponent vs Player, Player vs Player, etc)
            else if (isCat(entityA) && isCat(entityB) && entityA != entityB) {
                handleCatCollision(entityA, entityB, fixtureA, fixtureB);
            }
            // Cat-Self collision
            else if (isCat(entityA) && isCat(entityB) && entityA == entityB) {
                handleSelfCollision(entityA, fixtureA, fixtureB);
            }
        }
    }

    private void handleKibbleCollision(Entity entityA, Entity entityB) {
        Gdx.app.log("Collision", "Cat-Kibble collision detected");
        Entity cat = kcm.has(entityA) ? entityB : entityA;
        Entity kibble = kcm.has(entityA) ? entityA : entityB;

        kibble.add(engine.createComponent(MarkedForRemovalComponent.class));

        CatBodyComponent body = cbcm.get(cat);
        if (body != null) {
            SizeComponent sizeComp = scm.get(cat);
            body.maxLength = Math.min(body.maxLength + (int)((sizeComp != null) ? sizeComp.growthRate : 5), 500);
        }

        addCatScore(cat, 10);

        if (pcm.has(cat)) {
            Network.KibbleEaten msg = new Network.KibbleEaten();
            msg.kibbleId = kcm.get(kibble).id;
            msg.eatenByPlayerId = pcm.get(cat).networkID;
            gameClient.sendTCP(msg);
        }
    }

    private void handleCatCollision(Entity catA, Entity catB, Fixture fixtureA, Fixture fixtureB) {
        if (isCatDead(catA) || isCatDead(catB)) return;

        int scoreA = getCatScore(catA);
        int scoreB = getCatScore(catB);
        boolean isAHead = !fixtureA.isSensor();
        boolean isBHead = !fixtureB.isSensor();
        Gdx.app.log("Collision", "Cat-Cat collision detected between two cats with scores " + scoreA + " and " + scoreB);

        if (isAHead && !isBHead) {
            Gdx.app.log("Collision", "Cat B defeated Cat A (Head vs Body)");
            setCatDead(catA);
            if(pcm.has(catA)){
                Network.CatDefeated msg = new Network.CatDefeated();
                msg.winnerId = getCatId(catB);
                msg.loserId = getCatId(catA);
                gameClient.sendTCP(msg);
            }
        } else if (!isAHead && isBHead) {
            Gdx.app.log("Collision", "Cat A defeated Cat B (Head vs Body)");
            setCatDead(catB);
            if(pcm.has(catB)){
                Network.CatDefeated msg = new Network.CatDefeated();
                msg.winnerId = getCatId(catA);
                msg.loserId = getCatId(catB);
                gameClient.sendTCP(msg);
            }
        } else if (isAHead && isBHead) {
            if (scoreA > scoreB) {
                Gdx.app.log("Collision", "Cat B defeated Cat A (Head vs Head)");
                setCatDead(catA);
                if(pcm.has(catA)){
                    Network.CatDefeated msg = new Network.CatDefeated();
                    msg.winnerId = getCatId(catB);
                    msg.loserId = getCatId(catA);
                    gameClient.sendTCP(msg);
                }
            } else if (scoreB > scoreA) {
                Gdx.app.log("Collision", "Cat A defeated Cat B (Head vs Head)");
                setCatDead(catB);
                if(pcm.has(catB)){
                    Network.CatDefeated msg = new Network.CatDefeated();
                    msg.winnerId = getCatId(catA);
                    msg.loserId = getCatId(catB);
                    gameClient.sendTCP(msg);
                }
            } else {
                // Equal score tie — both die
                Gdx.app.log("Collision", "Head vs Head tie — both cats die");
                setCatDead(catA);
                setCatDead(catB);
                if (pcm.has(catA)) {
                    Network.CatDefeated msg = new Network.CatDefeated();
                    msg.winnerId = -1;
                    msg.loserId = getCatId(catA);
                    gameClient.sendTCP(msg);
                }
                if (pcm.has(catB)) {
                    Network.CatDefeated msg = new Network.CatDefeated();
                    msg.winnerId = -1;
                    msg.loserId = getCatId(catB);
                    gameClient.sendTCP(msg);
                }
            }
        }
    }

    private void handleSelfCollision(Entity cat, Fixture fixtureA, Fixture fixtureB) {
        // Self-collision does not kill — only opponent body contact is lethal
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
