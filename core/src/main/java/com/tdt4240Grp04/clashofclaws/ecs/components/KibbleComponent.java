package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class KibbleComponent implements Component, Poolable {
    public int id;
    public float value = 10.0f;

    @Override
    public void reset() {
        id = -1;
        value = 10.0f;
    }
}
