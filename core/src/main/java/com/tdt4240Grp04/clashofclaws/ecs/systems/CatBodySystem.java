package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class CatBodySystem extends IteratingSystem {

    public CatBodySystem() {
        super(Family.all(CatBodyComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CatBodyComponent catBody = CatBodyComponent.MAPPER.get(entity);
        PhysicsComponent physics = PhysicsComponent.MAPPER.get(entity);

        Vector2 headPosition = physics.body.getPosition();

        // If the body is empty, add the first segment at the head's position
        if (catBody.bodyParts.size == 0) {
            catBody.bodyParts.add(new Vector2(headPosition));
            return;
        }

        Vector2 lastSegment = catBody.bodyParts.first();
        float distance = headPosition.dst(lastSegment);

        // Add a new segment if the head has moved far enough
        if (distance > catBody.segmentSpacing) {
            // Add new segment right behind the head
            Vector2 newSegment = new Vector2(headPosition);
            catBody.bodyParts.insert(0, newSegment);

            // Remove tail segment if body is too long
            while (catBody.bodyParts.size > catBody.maxLength) {
                catBody.bodyParts.pop();
            }
        }
    }
}
