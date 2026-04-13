package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PlayerComponent implements Component {
    public int score = 0;
    public boolean isDead = false;

    public static final ComponentMapper<PlayerComponent> MAPPER
            = ComponentMapper.getFor(PlayerComponent.class);
}
