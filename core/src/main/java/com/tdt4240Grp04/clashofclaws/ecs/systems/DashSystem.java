package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

public class DashSystem extends IteratingSystem {

    public DashSystem() {
        // Added to engine BEFORE MovementSystem so it runs first
        super(Family.all(StaminaComponent.class, CharacterComponent.class, CatTypeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StaminaComponent stamina = StaminaComponent.MAPPER.get(entity);
        CharacterComponent charComp = CharacterComponent.MAPPER.get(entity);
        CatTypeComponent catType = CatTypeComponent.MAPPER.get(entity);

        if (stamina.isDashing && stamina.currentStamina > 0) {
            charComp.speedMultiplier = catType.dashMultiplier;
            stamina.currentStamina = Math.max(0, stamina.currentStamina - stamina.drainRate * deltaTime);
        } else {
            charComp.speedMultiplier = 1.0f;
            stamina.isDashing = false;
            stamina.currentStamina = Math.min(stamina.maxStamina,
                stamina.currentStamina + stamina.rechargeRate * deltaTime);
        }
    }
}
