package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

public class PlayController {
    private Touchpad touchpad;
    private TextButton dashButton;
    private Entity player;
    private boolean spaceWasPressed = false;

    public PlayController(Entity player, Stage stage, Skin skin) {
        this.player = player;

        int screenW = Gdx.graphics.getWidth();

        // Joystick — bottom RIGHT
        touchpad = new Touchpad(5, skin);
        touchpad.setBounds(screenW - 300, 50, 250, 250);
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

        // Dash button — bottom LEFT, styled
        TextButton.TextButtonStyle dashStyle = new TextButton.TextButtonStyle();
        dashStyle.font = skin.getFont("default-font");
        dashStyle.fontColor = Color.WHITE;
        dashStyle.up   = colorDrawable(new Color(0.15f, 0.45f, 0.95f, 0.9f));
        dashStyle.down = colorDrawable(new Color(0.05f, 0.25f, 0.75f, 1.0f));
        dashStyle.over = colorDrawable(new Color(0.25f, 0.55f, 1.00f, 0.95f));

        dashButton = new TextButton("DASH", dashStyle);
        dashButton.getLabel().setFontScale(1.6f);
        dashButton.setSize(200, 200);
        dashButton.setPosition(50, 50);
        dashButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                activateDash();
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                deactivateDash();
            }
        });
        stage.addActor(dashButton);
    }

    /** Poll keyboard input each frame — call from PlayState.update(). */
    public void update() {
        // WASD / arrow keys for movement
        CharacterComponent character = CharacterComponent.MAPPER.get(player);
        if (character != null) {
            float dx = 0, dy = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    dy += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 1;
            if (dx != 0 || dy != 0) {
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                character.dirX = dx / len;
                character.dirY = dy / len;
            }
        }

        // Spacebar for dash
        boolean spaceNow = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
        if (stamina != null) {
            if (spaceNow && stamina.currentStamina > 0) {
                stamina.isDashing = true;
            } else if (!spaceNow && spaceWasPressed) {
                stamina.isDashing = false;
            }
        }
        spaceWasPressed = spaceNow;
    }

    private void activateDash() {
        StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
        if (stamina != null && stamina.currentStamina > 0) stamina.isDashing = true;
    }

    private void deactivateDash() {
        StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
        if (stamina != null) stamina.isDashing = false;
    }

    private static TextureRegionDrawable colorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        return drawable;
    }

    public void dispose() { }
}
