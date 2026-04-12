package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.systems.MovementSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.PhysicsSystem;

public class PlayLogic {
    private Engine engine;
    private Entity player;
    private World world;

    public PlayLogic() {
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);

        engine.addSystem(new MovementSystem());
        engine.addSystem(new PhysicsSystem());

        player = spawnPlayer();
    }

    private Entity spawnPlayer() {
        Entity player = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        charComp.x = 100;
        charComp.y = 100;
        charComp.speed = 150f;
        player.add(charComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        player.add(sizeComp);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(charComp.x, charComp.y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sizeComp.width / 2, sizeComp.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        body.createFixture(fixtureDef);
        shape.dispose();

        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        physicsComponent.body = body;
        player.add(physicsComponent);


        engine.addEntity(player);
        return player;
    }

    public void update(float dt) {
        world.step(1/60f, 6, 2);
        engine.update(dt);
    }

    public Engine getEngine() {
        return engine;
    }

    public Entity getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }
}
