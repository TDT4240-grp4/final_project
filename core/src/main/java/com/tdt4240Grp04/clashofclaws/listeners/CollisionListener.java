package com.tdt4240Grp04.clashofclaws.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.MarkedForRemovalComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;

public class CollisionListener implements ContactListener {

    private Engine engine;
    private ComponentMapper<KibbleComponent> kcm = ComponentMapper.getFor(KibbleComponent.class);
    private ComponentMapper<CharacterComponent> ccm = ComponentMapper.getFor(CharacterComponent.class);
    private ComponentMapper<SizeComponent> scm = ComponentMapper.getFor(SizeComponent.class);
    private ComponentMapper<CatBodyComponent> bcm = ComponentMapper.getFor(CatBodyComponent.class);

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

            Entity player = null;
            Entity kibble = null;

            if (ccm.has(entityA) && kcm.has(entityB)) {
                player = entityA;
                kibble = entityB;
            } else if (ccm.has(entityB) && kcm.has(entityA)) {
                player = entityB;
                kibble = entityA;
            }

            if (player != null && kibble != null) {
                kibble.add(engine.createComponent(MarkedForRemovalComponent.class));
                if (bcm.has(player)) {
                    CatBodyComponent body = bcm.get(player);
                    body.maxLength += 5;
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
