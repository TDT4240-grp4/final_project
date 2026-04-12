package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<CharacterComponent> cm = CharacterComponent.MAPPER;
    private ComponentMapper<PhysicsComponent> pm = PhysicsComponent.MAPPER;

    public PhysicsSystem() {
        super(Family.all(CharacterComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CharacterComponent character = cm.get(entity);
        PhysicsComponent physics = pm.get(entity);

        character.x = physics.body.getPosition().x;
        character.y = physics.body.getPosition().y;
    }
}
