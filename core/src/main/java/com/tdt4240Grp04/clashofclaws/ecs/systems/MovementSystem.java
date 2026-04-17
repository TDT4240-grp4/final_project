package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;

public class MovementSystem extends IteratingSystem {

    private static final float DEFAULT_MAX_SPEED = 5f;
    private static final float DEFAULT_MIN_SPEED = 1.5f;
    private static final float MAX_LENGTH_FOR_MIN_SPEED = 150f;
    private static final float STARTING_LENGTH = 10f;

    public MovementSystem() {
        super(Family.all(CharacterComponent.class, PhysicsComponent.class, PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physComponent = PhysicsComponent.MAPPER.get(entity);
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);
        CatBodyComponent catBodyComp = CatBodyComponent.MAPPER.get(entity);
        CatTypeComponent catTypeComp = CatTypeComponent.MAPPER.get(entity);

        float maxSpeed = (catTypeComp != null) ? catTypeComp.maxSpeed : DEFAULT_MAX_SPEED;
        float minSpeed = (catTypeComp != null) ? catTypeComp.minSpeed : DEFAULT_MIN_SPEED;

        float currentSpeed = maxSpeed;
        if (catBodyComp != null) {
            float lengthRange = MAX_LENGTH_FOR_MIN_SPEED - STARTING_LENGTH;
            float speedRange = maxSpeed - minSpeed;
            if (lengthRange > 0) {
                float lengthProgress = Math.max(0, catBodyComp.maxLength - STARTING_LENGTH);
                float speedReduction = (lengthProgress / lengthRange) * speedRange;
                currentSpeed = Math.max(minSpeed, maxSpeed - speedReduction);
            }
        }

        // Apply dash multiplier set by DashSystem
        currentSpeed *= charComp.speedMultiplier;

        physComponent.body.setLinearVelocity(charComp.dirX * currentSpeed, charComp.dirY * currentSpeed);
    }
}
