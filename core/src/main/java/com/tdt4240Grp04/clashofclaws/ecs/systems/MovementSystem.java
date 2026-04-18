package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;

public class MovementSystem extends IteratingSystem {

    private final SpeedProvider speedProvider;

    public MovementSystem() {
        super(Family.all(CharacterComponent.class, PhysicsComponent.class, PlayerComponent.class).get());
        this.speedProvider = new DashSpeedDecorator(new BaseSpeedProvider());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physComponent = PhysicsComponent.MAPPER.get(entity);
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);

        float speed = speedProvider.getSpeed(entity);
        physComponent.body.setLinearVelocity(charComp.dirX * speed, charComp.dirY * speed);
    }
}
