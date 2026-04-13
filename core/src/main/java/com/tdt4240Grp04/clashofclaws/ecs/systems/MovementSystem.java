package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class MovementSystem extends IteratingSystem {
    public MovementSystem() {
        super(Family.all(CharacterComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);
        PhysicsComponent physComponent = PhysicsComponent.MAPPER.get(entity);

        float velX = charComp.dirX * charComp.speed;
        float velY = charComp.dirY * charComp.speed;

        // Apply velocity to the Box2D body instead of moving the component directly
        physComponent.body.setLinearVelocity(velX, velY);
    }
}
