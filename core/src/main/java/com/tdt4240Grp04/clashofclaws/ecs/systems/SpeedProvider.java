package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;

/** Returns the effective movement speed for an entity this frame. */
public interface SpeedProvider {
    float getSpeed(Entity entity);
}
