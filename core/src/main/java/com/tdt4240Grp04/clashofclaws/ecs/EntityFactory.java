package com.tdt4240Grp04.clashofclaws.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.config.GameConfig;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.KibbleComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.OpponentComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;

/** Creates and registers fully-configured ECS entities. */
public class EntityFactory {

    private final Engine engine;
    private final World world;

    public EntityFactory(Engine engine, World world) {
        this.engine = engine;
        this.world = world;
    }

    public Entity createPlayer(float startX, float startY, String hexColor, String playerName,
                               int catIndex, Texture catHeadTexture) {
        Entity player = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        charComp.x = startX;
        charComp.y = startY;
        charComp.speed = GameConfig.getMaxSpeed(catIndex);
        player.add(charComp);

        CatTypeComponent catTypeComp = engine.createComponent(CatTypeComponent.class);
        catTypeComp.catIndex = catIndex;
        catTypeComp.maxSpeed = GameConfig.getMaxSpeed(catIndex);
        catTypeComp.minSpeed = GameConfig.getMinSpeed(catIndex);
        catTypeComp.dashMultiplier = GameConfig.getDashMultiplier(catIndex);
        catTypeComp.startingBodyLength = GameConfig.getStartingLength(catIndex);
        player.add(catTypeComp);

        StaminaComponent staminaComp = engine.createComponent(StaminaComponent.class);
        staminaComp.maxStamina = GameConfig.getMaxStamina(catIndex);
        staminaComp.currentStamina = staminaComp.maxStamina;
        staminaComp.drainRate = GameConfig.getDrainRate(catIndex);
        staminaComp.rechargeRate = GameConfig.getRechargeRate(catIndex);
        player.add(staminaComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        sizeComp.growthRate = GameConfig.getGrowthRate(catIndex);
        player.add(sizeComp);

        PlayerComponent playerComp = engine.createComponent(PlayerComponent.class);
        playerComp.score = 0;
        playerComp.name = playerName;
        playerComp.networkID = -1;
        player.add(playerComp);

        CatBodyComponent catBody = engine.createComponent(CatBodyComponent.class);
        catBody.color = com.badlogic.gdx.graphics.Color.valueOf(hexColor);
        catBody.maxLength = GameConfig.getStartingLength(catIndex);
        player.add(catBody);

        player.add(createPhysicsBody(player, startX, startY, sizeComp, BodyDef.BodyType.DynamicBody));

        TextureComponent texComp = engine.createComponent(TextureComponent.class);
        texComp.texture = catHeadTexture;
        player.add(texComp);

        engine.addEntity(player);
        return player;
    }

    public Entity createOpponent(int networkId, float startX, float startY, String hexColor,
                                 int catIndex, String name) {
        Entity opponent = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        charComp.x = startX;
        charComp.y = startY;
        charComp.speed = GameConfig.getMaxSpeed(catIndex);
        opponent.add(charComp);

        CatTypeComponent catTypeComp = engine.createComponent(CatTypeComponent.class);
        catTypeComp.catIndex = catIndex;
        catTypeComp.maxSpeed = GameConfig.getMaxSpeed(catIndex);
        catTypeComp.minSpeed = GameConfig.getMinSpeed(catIndex);
        catTypeComp.dashMultiplier = GameConfig.getDashMultiplier(catIndex);
        catTypeComp.startingBodyLength = GameConfig.getStartingLength(catIndex);
        opponent.add(catTypeComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        sizeComp.growthRate = GameConfig.getGrowthRate(catIndex);
        opponent.add(sizeComp);

        OpponentComponent opponentComp = engine.createComponent(OpponentComponent.class);
        opponentComp.networkId = networkId;
        opponentComp.name = name;
        opponent.add(opponentComp);

        CatBodyComponent catBody = engine.createComponent(CatBodyComponent.class);
        catBody.color = com.badlogic.gdx.graphics.Color.valueOf(hexColor);
        catBody.maxLength = GameConfig.getStartingLength(catIndex);
        opponent.add(catBody);

        opponent.add(createPhysicsBody(opponent, startX, startY, sizeComp, BodyDef.BodyType.KinematicBody));

        TextureComponent texComp = engine.createComponent(TextureComponent.class);
        texComp.texture = new Texture(Gdx.files.internal("cat" + (catIndex + 1) + "_head.png"));
        opponent.add(texComp);

        engine.addEntity(opponent);
        return opponent;
    }

    public Entity createKibble(int id, float x, float y, Texture kibbleTexture) {
        Entity kibble = engine.createEntity();

        KibbleComponent kibbleComp = engine.createComponent(KibbleComponent.class);
        kibbleComp.id = id;
        kibble.add(kibbleComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        sizeComp.width = 0.5f;
        sizeComp.height = 0.5f;
        kibble.add(sizeComp);

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
        return kibble;
    }

    private PhysicsComponent createPhysicsBody(Entity owner, float x, float y,
                                               SizeComponent sizeComp, BodyDef.BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);
        body.setUserData(owner);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sizeComp.width / 2, sizeComp.height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        body.createFixture(fixtureDef);
        shape.dispose();

        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        physicsComponent.body = body;
        return physicsComponent;
    }
}
