package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;

public class MovementSystem extends IteratingSystem {

    private static final float MAX_SPEED = 5f;
    private static final float MIN_SPEED = 1.5f;
    private static final float MAX_LENGTH_FOR_MIN_SPEED = 150f;
    private static final float STARTING_LENGTH = 10f;


    public MovementSystem() {
        super(Family.all(CharacterComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComp = PlayerComponent.MAPPER.get(entity);
        PhysicsComponent physComponent = PhysicsComponent.MAPPER.get(entity);
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);
        CatBodyComponent catBodyComp = CatBodyComponent.MAPPER.get(entity);


        float currentSpeed = charComp.speed;

        if (playerComp != null && catBodyComp != null) {
            float lengthRange = MAX_LENGTH_FOR_MIN_SPEED - STARTING_LENGTH;
            float speedRange = MAX_SPEED - MIN_SPEED;

            if (lengthRange > 0) {
                float lengthProgress = Math.max(0, catBodyComp.maxLength - STARTING_LENGTH);
                float speedReduction = (lengthProgress / lengthRange) * speedRange;
                currentSpeed = Math.max(MIN_SPEED, MAX_SPEED - speedReduction);
            } else {
                currentSpeed = MAX_SPEED;
            }
        }


        float velX = charComp.dirX * currentSpeed;
        float velY = charComp.dirY * currentSpeed;

        // Apply velocity to the Box2D body instead of moving the component directly
        physComponent.body.setLinearVelocity(velX, velY);
    }
}
