package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.systems.MovementSystem;

public class PlayLogic {
    private Engine engine;

    public PlayLogic() {
        engine = new Engine();

        engine.addSystem(new MovementSystem());

        spawnPlayer();
    }

    private void spawnPlayer() {
        Entity player = engine.createEntity();

        CharacterComponent charComp = engine.createComponent(CharacterComponent.class);
        charComp.x = 100;
        charComp.y = 100;
        charComp.speed = 150f;
        player.add(charComp);

        SizeComponent sizeComp = engine.createComponent(SizeComponent.class);
        player.add(sizeComp);

        engine.addEntity(player);
    }

    public void update(float dt) {
        engine.update(dt);
    }

    public Engine getEngine() {
        return engine;
    }
}
