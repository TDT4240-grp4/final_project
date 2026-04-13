package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;
import com.tdt4240Grp04.clashofclaws.ecs.systems.MovementSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.PhysicsSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.RemovalSystem;
import com.tdt4240Grp04.clashofclaws.listeners.CollisionListener;

public class PlayLogic {
    private Engine engine;
    private Entity player;
    private World world;
    private Texture kibbleTexture;

    public PlayLogic() {
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);
        kibbleTexture = new Texture(Gdx.files.internal("kibble.png"));

        world.setContactListener(new CollisionListener(engine));

        engine.addSystem(new MovementSystem());
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new RemovalSystem(world));

        player = spawnPlayer();

        for (int i = 0; i < 10; i++) {
            spawnKibble();
        }
    }

    private Entity spawnPlayer() {
        Entity player = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        charComp.x = Gdx.graphics.getWidth() / 2f;
        charComp.y = Gdx.graphics.getHeight() / 2f;
        charComp.speed = 150f;
        player.add(charComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        player.add(sizeComp);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(charComp.x, charComp.y);
        Body body = world.createBody(bodyDef);
        body.setUserData(player);

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

    private void spawnKibble() {
        Entity kibble = engine.createEntity();

        KibbleComponent kibbleComp = engine.createComponent(KibbleComponent.class);
        kibble.add(kibbleComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        sizeComp.width = 50;
        sizeComp.height = 50;
        kibble.add(sizeComp);

        float x = (float) (Math.random() * Gdx.graphics.getWidth());
        float y = (float) (Math.random() * Gdx.graphics.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        body.setUserData(kibble);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sizeComp.width / 2, sizeComp.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        physicsComponent.body = body;
        kibble.add(physicsComponent);

        TextureComponent texComp = engine.createComponent(TextureComponent.class);
        texComp.texture = kibbleTexture;
        kibble.add(texComp);

        engine.addEntity(kibble);
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

    public void dispose() {
        kibbleTexture.dispose();
        world.dispose();
    }
}
