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
import com.tdt4240Grp04.clashofclaws.audio.AudioManager;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;

public class PlayController {
    private Touchpad touchpad;
    private TextButton dashButton;
    private Entity player;
    private boolean spaceWasPressed = false;

    public PlayController(Entity player, Stage stage, Skin skin) {
        this.player = player;

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float jSize = Math.min(screenW, screenH) * 0.22f;
        float knobSize = jSize * 0.42f;

        // Joystick — bottom RIGHT, styled consistently with lobby
        Touchpad.TouchpadStyle tpStyle = new Touchpad.TouchpadStyle();
        tpStyle.background = skin.newDrawable("white", new Color(0.1f, 0.1f, 0.15f, 0.55f));
        tpStyle.knob = skin.newDrawable("white", Color.valueOf("1ca1e4dd"));
        tpStyle.knob.setMinWidth(knobSize);
        tpStyle.knob.setMinHeight(knobSize);

        touchpad = new Touchpad(jSize * 0.05f, tpStyle);
        touchpad.setBounds(screenW - jSize - 24, 24, jSize, jSize);
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
        dashStyle.font = skin.getFont("Boogaloo-Regular");
        dashStyle.fontColor = Color.WHITE;
        dashStyle.up   = colorDrawable(new Color(0.15f, 0.45f, 0.95f, 0.9f));
        dashStyle.down = colorDrawable(new Color(0.05f, 0.25f, 0.75f, 1.0f));
        dashStyle.over = colorDrawable(new Color(0.25f, 0.55f, 1.00f, 0.95f));

        dashButton = new TextButton("DASH", dashStyle);
        dashButton.getLabel().setFontScale(0.7f);
        dashButton.setSize(jSize, jSize);
        dashButton.setPosition(24, 24);
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
                if (!spaceWasPressed) AudioManager.getInstance().playDashSound();
                stamina.isDashing = true;
            } else if (!spaceNow && spaceWasPressed) {
                stamina.isDashing = false;
            }
        }
        spaceWasPressed = spaceNow;
    }

    private void activateDash() {
        StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
        if (stamina != null && stamina.currentStamina > 0) {
            stamina.isDashing = true;
            AudioManager.getInstance().playDashSound();
        }
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
