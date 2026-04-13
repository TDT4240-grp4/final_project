package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;

public class PlayController {
    private Stage stage;
    private Touchpad touchpad;
    private Entity player;
    private Skin skin;

    public PlayController(Entity player) {
        this.player = player;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        touchpad = new Touchpad(10, skin);
        touchpad.setBounds(100, 100, 200, 200);
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
        stage.addActor(touchpad);
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
