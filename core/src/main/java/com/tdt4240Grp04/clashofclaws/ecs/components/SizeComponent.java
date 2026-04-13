package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class SizeComponent implements Component {
    public float scale = 1.0f;
    public float growthRate = 0.1f;
    public float width = 1f;
    public float height = 1f;

    public static final ComponentMapper<SizeComponent> MAPPER
        = ComponentMapper.getFor(SizeComponent.class);
}
