package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;
import com.tdt4240Grp04.clashofclaws.ecs.systems.CatBodySystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.MovementSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.PhysicsSystem;
import com.tdt4240Grp04.clashofclaws.ecs.systems.RemovalSystem;
import com.tdt4240Grp04.clashofclaws.listeners.CollisionListener;

public class PlayLogic {
    private Engine engine;
    private Entity player;
    private World world;
    private Texture kibbleTexture;
    private Texture catHeadTexture;

    private final float MAP_WIDTH = 200f;
    private final float MAP_HEIGHT = 200f;
    public PlayLogic() {
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);
        kibbleTexture = new Texture(Gdx.files.internal("kibble.png"));
        catHeadTexture = new Texture(Gdx.files.internal("cat1_head.png"));

        world.setContactListener(new CollisionListener(engine));

        engine.addSystem(new MovementSystem());
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new RemovalSystem(world));
        engine.addSystem(new CatBodySystem(world));

        createMapBounds();
        //player = spawnPlayer();
        player = spawnPlayer(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, "ffeedb", 0);

        for (int i = 0; i < 100; i++) {
            spawnKibble();
        }
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
    private Entity spawnPlayer(float startX, float startY, String hexColor, int startingScore) {
        Entity player = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        //charComp.x = MAP_WIDTH / 2f;
        //charComp.y = MAP_HEIGHT / 2f;
        charComp.x = startX;
        charComp.y = startY;
        charComp.speed = 10f;
        player.add(charComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        player.add(sizeComp);

        //player.add(engine.createComponent(PlayerComponent.class));

        PlayerComponent playerComp = engine.createComponent(PlayerComponent.class);
        playerComp.score = startingScore; // Give them a score to test the transfer!
        player.add(playerComp);

        CatBodyComponent catBody = engine.createComponent(CatBodyComponent.class);
        //catBody.color = com.badlogic.gdx.graphics.Color.valueOf("ffeedb"); // Match your cat color!
        catBody.color = com.badlogic.gdx.graphics.Color.valueOf(hexColor);
        player.add(catBody);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(charComp.x, charComp.y);
        bodyDef.fixedRotation = true;
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

        TextureComponent texComp = engine.createComponent(TextureComponent.class);
        texComp.texture = catHeadTexture;
        player.add(texComp);

        engine.addEntity(player);
        return player;
    }

    private void spawnKibble() {
        Entity kibble = engine.createEntity();

        KibbleComponent kibbleComp = engine.createComponent(KibbleComponent.class);
        kibble.add(kibbleComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        sizeComp.width = 0.5f;
        sizeComp.height = 0.5f;
        kibble.add(sizeComp);

        float x = (float) (Math.random() * MAP_WIDTH);
        float y = (float) (Math.random() * MAP_HEIGHT);

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
