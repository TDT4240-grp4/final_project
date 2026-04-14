package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;

public class PlayController {
    private Touchpad touchpad;
    private Entity player;
    private Skin skin;

    public PlayController(Entity player, Stage stage, Skin skin) {
        this.player = player;
        this.skin = skin;

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
        stage.addActor(touchpad);
    }

    public void dispose() {
        // The skin is managed by PlayView, so we don't dispose it here.
    }
}
