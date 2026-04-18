package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class CatTypeComponent implements Component {
    public int catIndex = 0;
    public float maxSpeed = 5.0f;
    public float minSpeed = 1.5f;
    public float dashMultiplier = 1.4f;
    public int startingBodyLength = 1;

    public static final ComponentMapper<CatTypeComponent> MAPPER =
        ComponentMapper.getFor(CatTypeComponent.class);
}
