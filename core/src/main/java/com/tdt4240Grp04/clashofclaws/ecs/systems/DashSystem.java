package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

/** Manages stamina drain and recharge each frame. Speed application is handled by DashSpeedDecorator. */
public class DashSystem extends IteratingSystem {

    public DashSystem() {
        super(Family.all(StaminaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StaminaComponent stamina = StaminaComponent.MAPPER.get(entity);

        if (stamina.isDashing && stamina.currentStamina > 0) {
            stamina.currentStamina = Math.max(0, stamina.currentStamina - stamina.drainRate * deltaTime);
        } else {
            stamina.isDashing = false;
            stamina.currentStamina = Math.min(stamina.maxStamina,
                stamina.currentStamina + stamina.rechargeRate * deltaTime);
        }
    }
}
