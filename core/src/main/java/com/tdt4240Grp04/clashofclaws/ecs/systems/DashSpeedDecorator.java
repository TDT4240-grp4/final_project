package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

/** Decorator that multiplies base speed by the dash multiplier when the entity is dashing. */
public class DashSpeedDecorator implements SpeedProvider {

    private final SpeedProvider wrapped;

    public DashSpeedDecorator(SpeedProvider wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public float getSpeed(Entity entity) {
        float baseSpeed = wrapped.getSpeed(entity);
        StaminaComponent stamina = StaminaComponent.MAPPER.get(entity);
        CatTypeComponent catType = CatTypeComponent.MAPPER.get(entity);

        if (stamina != null && catType != null && stamina.isDashing && stamina.currentStamina > 0) {
            return baseSpeed * catType.dashMultiplier;
        }
        return baseSpeed;
    }
}
