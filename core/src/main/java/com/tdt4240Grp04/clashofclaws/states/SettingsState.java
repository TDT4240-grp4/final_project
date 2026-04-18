package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.tdt4240Grp04.clashofclaws.audio.AudioManager;

public class SettingsState extends State {
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private final FirebaseSDK firebase;

    public SettingsState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.firebase = firebase;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin  = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        AudioManager audio = AudioManager.getInstance();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.pad(60);

        // Title
        Label title = new Label("SETTINGS", skin);
        title.setFontScale(2f);
        title.setColor(Color.valueOf("1ca1e4"));
        root.add(title).colspan(2).padBottom(50).row();

        // Sound effects row
        TextButton soundToggle = makeToggle(audio.isSoundEnabled());
        Slider soundSlider = makeSlider(audio.getSoundVolume(), !audio.isSoundEnabled());
        addSettingRow(root, "SOUND EFFECTS", soundToggle, soundSlider);

        soundToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                boolean on = soundToggle.isChecked();
                audio.setSoundEnabled(on);
                soundSlider.setDisabled(!on);
                soundSlider.getColor().a = on ? 1f : 0.4f;
            }
        });
        soundSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                audio.setSoundVolume(soundSlider.getValue());
            }
        });

        // Music row
        TextButton musicToggle = makeToggle(audio.isMusicEnabled());
        Slider musicSlider = makeSlider(audio.getMusicVolume(), !audio.isMusicEnabled());
        addSettingRow(root, "MUSIC", musicToggle, musicSlider);

        musicToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                boolean on = musicToggle.isChecked();
                audio.setMusicEnabled(on);
                musicSlider.setDisabled(!on);
                musicSlider.getColor().a = on ? 1f : 0.4f;
            }
        });
        musicSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                audio.setMusicVolume(musicSlider.getValue());
            }
        });

        // Back button
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = skin.getFont("Boogaloo-Regular");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up   = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        btnStyle.down = skin.newDrawable("white", Color.valueOf("1480b0"));
        TextButton backBtn = new TextButton("BACK", btnStyle);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm, firebase));
            }
        });
        root.add(backBtn).colspan(2).width(300).height(70).padTop(50);

        stage.addActor(root);
    }

    private TextButton makeToggle(boolean initialState) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font      = skin.getFont("Boogaloo-Regular");
        style.fontColor = Color.WHITE;
        style.up        = skin.newDrawable("white", initialState ? Color.valueOf("1ca1e4") : Color.GRAY);
        style.checked   = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        TextButton btn = new TextButton(initialState ? "ON" : "OFF", style);
        btn.setChecked(initialState);
        btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                btn.setText(btn.isChecked() ? "ON" : "OFF");
                btn.getStyle().up = skin.newDrawable("white",
                    btn.isChecked() ? Color.valueOf("1ca1e4") : Color.GRAY);
            }
        });
        return btn;
    }

    private Slider makeSlider(float initialValue, boolean disabled) {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = skin.newDrawable("white", Color.DARK_GRAY);
        style.background.setMinHeight(8f);
        style.knob = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        style.knob.setMinWidth(24f);
        style.knob.setMinHeight(24f);
        Slider slider = new Slider(0f, 1f, 0.05f, false, style);
        slider.setValue(initialValue);
        slider.setDisabled(disabled);
        if (disabled) slider.getColor().a = 0.4f;
        return slider;
    }

    private void addSettingRow(Table root, String labelText, TextButton toggle, Slider slider) {
        Label lbl = new Label(labelText, skin);
        lbl.setFontScale(1.1f);
        lbl.setColor(Color.WHITE);

        Table row = new Table();
        row.add(lbl).width(280).left();
        row.add(toggle).width(100).height(50).padLeft(20);
        root.add(row).colspan(2).left().padBottom(10).row();
        root.add(slider).colspan(2).width(500).height(40).padBottom(30).row();
    }

    @Override public void update(float dt) { stage.act(dt); }

    @Override public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.12f, 0.14f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
