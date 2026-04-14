package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class PlayState extends State {
    private PlayLogic playLogic;
    private PlayView playView;
    private PlayController playController;
    private FirebaseSDK firebase;

    public PlayState(StateManager gsm, FirebaseSDK firebase, String name, int catIndex) {
        super(gsm);
        this.firebase = firebase;
        playLogic = new PlayLogic(name, catIndex);
        playView = new PlayView(playLogic.getEngine(), playLogic.getPlayer());
        playController = new PlayController(playLogic.getPlayer(), playView.getStage(), playView.getSkin());

        Gdx.input.setInputProcessor(playView.getStage());
    }

    @Override
    public void update(float dt) {
        playLogic.update(dt);
        if (playLogic.isPlayerDead()) {
            gsm.set(new GameOverState(gsm, firebase));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.804f, 0.933f, 0.996f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playView.render(sb);
    }

    @Override
    public void resize(int width, int height) {
        playView.resize(width, height);
    }

    @Override
    public void dispose() {
        playView.dispose();
        playController.dispose();
        playLogic.dispose();
    }
}
