package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class CatBodySystem extends IteratingSystem {

    private World world;

    public CatBodySystem(World world) {
        super(Family.all(CatBodyComponent.class, PhysicsComponent.class).get());
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CatBodyComponent catBody = CatBodyComponent.MAPPER.get(entity);
        PhysicsComponent physics = PhysicsComponent.MAPPER.get(entity);

        Vector2 headPosition = physics.body.getPosition();

        if (catBody.bodyParts.size == 0) {
            catBody.bodyParts.add(new Vector2(headPosition));
            return;
        }

        Vector2 lastSegment = catBody.bodyParts.first();
        float distance = headPosition.dst(lastSegment);

        if (distance > catBody.segmentSpacing) {
            Vector2 newSegmentPos = new Vector2(headPosition);
            catBody.bodyParts.insert(0, newSegmentPos);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(newSegmentPos);
            Body segmentBody = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            CircleShape shape = new CircleShape();
            shape.setRadius(catBody.segmentRadius);
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true; // Body parts are sensors
            segmentBody.createFixture(fixtureDef);
            shape.dispose();

            segmentBody.setUserData(entity); // Associate with the player entity

            catBody.bodySegmentBodies.insert(0, segmentBody);

            while (catBody.bodyParts.size > catBody.maxLength) {
                catBody.bodyParts.pop();
                Body tailBody = catBody.bodySegmentBodies.pop();
                world.destroyBody(tailBody);
            }
        }
    }
}
