package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;

public class PlayController {
    private Stage gameStage;
    private Stage uiStage;
    private Touchpad touchpad;
    private Entity player;
    private Skin skin;

    public PlayController(Entity player) {
        this.player = player;

        gameStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        uiStage = new Stage(new ScreenViewport());

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        touchpad = new Touchpad(5, skin);
        touchpad.setBounds(100, 100, 300, 300);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                CharacterComponent character = CharacterComponent.MAPPER.get(player);
                if (character != null) {
                    character.dirX = touchpad.getKnobPercentX();
                    character.dirY = touchpad.getKnobPercentY();
                }
            }
        });
        uiStage.addActor(touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer(uiStage, gameStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void render() {
        // First, update the game logic
        gameStage.act(Gdx.graphics.getDeltaTime());
        uiStage.act(Gdx.graphics.getDeltaTime());

        PhysicsComponent physics = PhysicsComponent.MAPPER.get(player);
        if (physics != null) {
            Vector2 position = physics.body.getPosition();
            gameStage.getViewport().getCamera().position.set(position.x, position.y, 0);
            gameStage.getViewport().getCamera().update();
        }

        // Finally, draw everything
        gameStage.draw();
        uiStage.draw();
    }

    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        gameStage.dispose();
        uiStage.dispose();
        skin.dispose();
    }
}
