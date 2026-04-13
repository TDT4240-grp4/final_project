package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class KibbleComponent implements Component, Poolable {
    public float value = 10.0f;

    @Override
    public void reset() {
        value = 10.0f;
    }
}
