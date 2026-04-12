package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;

public class MovementSystem extends IteratingSystem {
    public MovementSystem() {
        super(Family.all(CharacterComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);
        charComp.x += charComp.dirX * charComp.speed * deltaTime;
        charComp.y += charComp.dirY * charComp.speed * deltaTime;
    }
}
