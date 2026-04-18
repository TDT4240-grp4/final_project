package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;

/** Base speed scaled by cat body length — longer cat moves slower. */
public class BaseSpeedProvider implements SpeedProvider {

    private static final float DEFAULT_MAX_SPEED = 5f;
    private static final float DEFAULT_MIN_SPEED = 1.5f;
    private static final float MAX_LENGTH_FOR_MIN_SPEED = 500f;

    private float logTimer = 0f;

    @Override
    public float getSpeed(Entity entity) {
        CatBodyComponent catBodyComp = CatBodyComponent.MAPPER.get(entity);
        CatTypeComponent catTypeComp = CatTypeComponent.MAPPER.get(entity);

        float maxSpeed = (catTypeComp != null) ? catTypeComp.maxSpeed : DEFAULT_MAX_SPEED;
        float minSpeed = (catTypeComp != null) ? catTypeComp.minSpeed : DEFAULT_MIN_SPEED;
        float startingLength = (catTypeComp != null) ? catTypeComp.startingBodyLength : 1f;

        float result;
        if (catBodyComp != null) {
            float lengthRange = MAX_LENGTH_FOR_MIN_SPEED - startingLength;
            float speedRange = maxSpeed - minSpeed;
            if (lengthRange > 0) {
                float lengthProgress = Math.max(0, catBodyComp.maxLength - startingLength);
                float speedReduction = (lengthProgress / lengthRange) * speedRange;
                result = Math.max(minSpeed, maxSpeed - speedReduction);
            } else {
                result = maxSpeed;
            }
        } else {
            result = maxSpeed;
        }

        logTimer += Gdx.graphics.getDeltaTime();
        if (logTimer >= 3f) {
            logTimer = 0f;
            int catIdx = (catTypeComp != null) ? catTypeComp.catIndex : -1;
            int maxLen = (catBodyComp != null) ? catBodyComp.maxLength : -1;
            Gdx.app.log("Speed", "catIdx=" + catIdx + " maxSpeed=" + maxSpeed + " maxLen=" + maxLen + " speed=" + result);
        }

        return result;
    }
}
