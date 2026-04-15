package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class WinnerState extends State {
    private Stage stage;
    private Skin skin;
    private FirebaseSDK firebase;

    public WinnerState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.firebase = firebase;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        Label winLabel = new Label("WINNER!", skin);
        winLabel.setFontScale(2.5f);
        winLabel.setColor(Color.valueOf("1ca1e4")); // Blue color
        winLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        Label instructionLabel = new Label("Tap to return to Main Menu", skin);

        table.add(winLabel).center().padBottom(30).row();
        table.add(instructionLabel).center();

        stage.addActor(table);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
        if (Gdx.input.justTouched()) {
            gsm.set(new LoginState(gsm, firebase));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.93f, 1f, 1f); // Light blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
