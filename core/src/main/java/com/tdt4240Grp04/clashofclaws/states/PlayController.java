package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

public class PlayController {
    private Touchpad touchpad;
    private TextButton dashButton;
    private Entity player;

    public PlayController(Entity player, Stage stage, Skin skin) {
        this.player = player;

        // Joystick — bottom LEFT
        touchpad = new Touchpad(5, skin);
        touchpad.setBounds(50, 50, 250, 250);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CharacterComponent character = CharacterComponent.MAPPER.get(player);
                if (character != null) {
                    character.dirX = touchpad.getKnobPercentX();
                    character.dirY = touchpad.getKnobPercentY();
                }
            }
        });
        stage.addActor(touchpad);

        // Dash button — bottom RIGHT
        dashButton = new TextButton("DASH", skin);
        int screenW = Gdx.graphics.getWidth();
        dashButton.setSize(180, 180);
        dashButton.setPosition(screenW - 220, 50);
        dashButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
                if (stamina != null && stamina.currentStamina > 0) {
                    stamina.isDashing = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
                if (stamina != null) {
                    stamina.isDashing = false;
                }
            }
        });
        stage.addActor(dashButton);
    }

    public void dispose() { }
}
