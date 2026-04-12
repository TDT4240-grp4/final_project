package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {
    public Body body;

    public static final ComponentMapper<PhysicsComponent> MAPPER
            = ComponentMapper.getFor(PhysicsComponent.class);
}
