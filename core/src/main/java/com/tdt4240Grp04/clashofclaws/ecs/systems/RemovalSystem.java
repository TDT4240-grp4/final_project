package com.tdt4240Grp04.clashofclaws.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.MarkedForRemovalComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class RemovalSystem extends IteratingSystem {

    private ComponentMapper<PhysicsComponent> pm = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<CatBodyComponent> cbcm = ComponentMapper.getFor(CatBodyComponent.class);
    private World world;

    public RemovalSystem(World world) {
        super(Family.all(MarkedForRemovalComponent.class).get());
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = pm.get(entity);
        if (physics != null) {
            world.destroyBody(physics.body);
        }

        CatBodyComponent catBody = cbcm.get(entity);
        if (catBody != null && catBody.bodySegmentBodies != null) {
            for (com.badlogic.gdx.physics.box2d.Body segmentBody : catBody.bodySegmentBodies) {
                world.destroyBody(segmentBody);
            }
            catBody.bodySegmentBodies.clear();
        }
        getEngine().removeEntity(entity);
    }
}
