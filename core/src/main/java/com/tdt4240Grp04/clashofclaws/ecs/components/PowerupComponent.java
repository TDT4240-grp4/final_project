package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PowerupComponent implements Component {
    public static final int NONE          = 0;
    public static final int SPEED_BOOST   = 1;
    public static final int SHIELD        = 2;
    public static final int KIBBLE_MAGNET = 3;

    public int   activeType       = NONE;
    public float remainingSeconds = 0f;
    public float speedMultiplier  = 1.0f;

    public static final ComponentMapper<PowerupComponent> MAPPER =
        ComponentMapper.getFor(PowerupComponent.class);
}
