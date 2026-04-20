package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.tdt4240Grp04.clashofclaws.ecs.components.PowerupComponent;

public class PowerupSpeedDecorator implements SpeedProvider {

    private final SpeedProvider wrapped;

    public PowerupSpeedDecorator(SpeedProvider wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public float getSpeed(Entity entity) {
        float base = wrapped.getSpeed(entity);
        PowerupComponent p = PowerupComponent.MAPPER.get(entity);
        if (p != null && p.activeType == PowerupComponent.SPEED_BOOST) {
            return base * 1.5f;
        }
        return base;
    }
}
