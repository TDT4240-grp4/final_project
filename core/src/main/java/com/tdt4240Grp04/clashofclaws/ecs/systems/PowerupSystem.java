package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PowerupComponent;

public class PowerupSystem extends EntitySystem {
    private ImmutableArray<Entity> players;

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, PowerupComponent.class).get());
    }

    @Override
    public void update(float dt) {
        for (Entity e : players) {
            PowerupComponent p = PowerupComponent.MAPPER.get(e);
            if (p.activeType == PowerupComponent.NONE) continue;

            p.remainingSeconds -= dt;
            if (p.remainingSeconds <= 0f) {
                expireEffect(e, p);
            }
        }
    }

    private void expireEffect(Entity e, PowerupComponent p) {
        if (p.activeType == PowerupComponent.SHIELD) {
            CatTypeComponent ct = CatTypeComponent.MAPPER.get(e);
            if (ct != null) ct.shieldActive = false;
        }
        // SPEED_BOOST: PowerupSpeedDecorator reads activeType==NONE automatically.
        // KIBBLE_MAGNET: collection loop in PlayLogic checks activeType each frame.
        p.activeType       = PowerupComponent.NONE;
        p.remainingSeconds = 0f;
        p.speedMultiplier  = 1.0f;
    }
}
