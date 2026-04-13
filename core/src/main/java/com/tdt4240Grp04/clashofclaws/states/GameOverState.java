package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class GameOverState extends State {
    private Stage stage;
    private Skin skin;
    private FirebaseSDK firebase;

    public GameOverState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.firebase = firebase;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        Label deathLabel = new Label("u died", skin, "title");
        deathLabel.setFontScale(3f);

        Label instructionLabel = new Label("Tap to continue", skin);

        table.add(deathLabel).center().padBottom(20).row();
        table.add(instructionLabel).center();

        stage.addActor(table);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
        // Transition to Login
        if (Gdx.input.justTouched()) {
            gsm.set(new LoginState(gsm, firebase));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.2f, 0.2f, 1f);
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
