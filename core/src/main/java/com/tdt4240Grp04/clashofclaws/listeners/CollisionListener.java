package com.tdt4240Grp04.clashofclaws.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.MarkedForRemovalComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;

public class CollisionListener implements ContactListener {

    private Engine engine;
    private ComponentMapper<KibbleComponent> kcm = ComponentMapper.getFor(KibbleComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<CatBodyComponent> cbcm = ComponentMapper.getFor(CatBodyComponent.class);


    public CollisionListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getBody().getUserData() instanceof Entity && fixtureB.getBody().getUserData() instanceof Entity) {
            Entity entityA = (Entity) fixtureA.getBody().getUserData();
            Entity entityB = (Entity) fixtureB.getBody().getUserData();

            // Player-Kibble collision
            if ((kcm.has(entityA) && pcm.has(entityB)) || (kcm.has(entityB) && pcm.has(entityA))) {
                handleKibbleCollision(entityA, entityB);
            }
            // Player-Player collision
            else if (pcm.has(entityA) && pcm.has(entityB) && entityA != entityB) {
                handlePlayerCollision(entityA, entityB, fixtureA, fixtureB);
            }
            // Player-Self collision
            else if (pcm.has(entityA) && pcm.has(entityB) && entityA == entityB) {
                handleSelfCollision(entityA, fixtureA, fixtureB);
            }
        }
    }

    private void handleKibbleCollision(Entity entityA, Entity entityB) {
        Gdx.app.log("Collision", "Player-Kibble collision detected");
        Entity player = kcm.has(entityA) ? entityB : entityA;
        Entity kibble = kcm.has(entityA) ? entityA : entityB;

        kibble.add(engine.createComponent(MarkedForRemovalComponent.class));

        CatBodyComponent body = cbcm.get(player);
        if (body != null) {
            body.maxLength += 5;
        }

        PlayerComponent playerComp = pcm.get(player);
        if (playerComp != null) {
            playerComp.score += 10;
        }
    }

    private void handlePlayerCollision(Entity playerA, Entity playerB, Fixture fixtureA, Fixture fixtureB) {
        //Gdx.app.log("Collision", "Player-Player collision detected");
        PlayerComponent pAComp = pcm.get(playerA);
        PlayerComponent pBComp = pcm.get(playerB);
        boolean isAHead = !fixtureA.isSensor();
        boolean isBHead = !fixtureB.isSensor();

        // Head vs Body
        if (isAHead && !isBHead) { // A is head, B is body
            //Gdx.app.log("Collision", "Player A head collided with Player B body");
            playerA.add(engine.createComponent(MarkedForRemovalComponent.class));
            pBComp.score += pAComp.score;
        } else if (!isAHead && isBHead) { // A is body, B is head
            //Gdx.app.log("Collision", "Player B head collided with Player A body");
            playerB.add(engine.createComponent(MarkedForRemovalComponent.class));
            pAComp.score += pBComp.score;
        }
        // Head vs Head
        else if (isAHead && isBHead) {
            if (pAComp.score > pBComp.score) {
                // Player A is larger, so Player B dies
                pBComp.isDead = true;
                playerB.add(engine.createComponent(MarkedForRemovalComponent.class));
                pAComp.score += pBComp.score; // Player A gets the points

            } else if (pBComp.score > pAComp.score) {
                // Player B is larger, so Player A dies
                pAComp.isDead = true;
                playerA.add(engine.createComponent(MarkedForRemovalComponent.class));
                pBComp.score += pAComp.score; // Player B gets the points

            } else {
                // Scores are exactly equal! Mutual destruction
                pAComp.isDead = true;
                pBComp.isDead = true;
                playerA.add(engine.createComponent(MarkedForRemovalComponent.class));
                playerB.add(engine.createComponent(MarkedForRemovalComponent.class));
            }
        }
    }

    private void handleSelfCollision(Entity player, Fixture fixtureA, Fixture fixtureB) {
        //Gdx.app.log("Collision", "Player-Self collision detected");
        boolean isAHead = !fixtureA.isSensor();
        boolean isBHead = !fixtureB.isSensor();

        if (isAHead != isBHead) {
            com.badlogic.gdx.physics.box2d.Body bodyFixture = isAHead ? fixtureB.getBody() : fixtureA.getBody();
            CatBodyComponent catBody = cbcm.get(player);

            if (catBody != null) {
                int segmentIndex = catBody.bodySegmentBodies.indexOf(bodyFixture, true);

                if (segmentIndex >= 5) {
                    PlayerComponent playerComp = pcm.get(player);
                    if (playerComp != null) {
                        playerComp.isDead = true;
                        //Gdx.app.log("Collision", "Player has collided with their own body.");
                    }
                }
            }
        }
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
