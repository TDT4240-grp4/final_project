package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class CharacterComponent implements Component {
    public float x, y;
    public float speed;
    public float dirX, dirY;
    public float speedMultiplier = 1.0f;

    public static final ComponentMapper<CharacterComponent> MAPPER
        = ComponentMapper.getFor(CharacterComponent.class);
}
