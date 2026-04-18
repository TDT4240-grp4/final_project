package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;

/** Base speed scaled by cat body length — longer cat moves slower. */
public class BaseSpeedProvider implements SpeedProvider {

    private static final float DEFAULT_MAX_SPEED = 5f;
    private static final float DEFAULT_MIN_SPEED = 1.5f;
    private static final float MAX_LENGTH_FOR_MIN_SPEED = 500f;

    @Override
    public float getSpeed(Entity entity) {
        CatBodyComponent catBodyComp = CatBodyComponent.MAPPER.get(entity);
        CatTypeComponent catTypeComp = CatTypeComponent.MAPPER.get(entity);

        float maxSpeed = (catTypeComp != null) ? catTypeComp.maxSpeed : DEFAULT_MAX_SPEED;
        float minSpeed = (catTypeComp != null) ? catTypeComp.minSpeed : DEFAULT_MIN_SPEED;
        float startingLength = (catTypeComp != null) ? catTypeComp.startingBodyLength : 1f;

        if (catBodyComp != null) {
            float lengthRange = MAX_LENGTH_FOR_MIN_SPEED - startingLength;
            float speedRange = maxSpeed - minSpeed;
            if (lengthRange > 0) {
                float lengthProgress = Math.max(0, catBodyComp.maxLength - startingLength);
                float speedReduction = (lengthProgress / lengthRange) * speedRange;
                return Math.max(minSpeed, maxSpeed - speedReduction);
            }
        }
        return maxSpeed;
    }
}
