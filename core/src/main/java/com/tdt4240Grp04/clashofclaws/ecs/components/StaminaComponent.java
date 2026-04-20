package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class StaminaComponent implements Component {
    public float currentStamina;
    public float maxStamina;
    public float drainRate;
    public float rechargeRate;
    public boolean isDashing = false;

    public static final ComponentMapper<StaminaComponent> MAPPER =
        ComponentMapper.getFor(StaminaComponent.class);
}
