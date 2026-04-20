package com.tdt4240Grp04.clashofclaws;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.audio.AudioManager;
import com.tdt4240Grp04.clashofclaws.states.LoginState;
import com.tdt4240Grp04.clashofclaws.states.StateManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ClashOfClaws extends ApplicationAdapter {
    private SpriteBatch batch;
    private StateManager gsm;
    private FirebaseSDK firebase;
    private Music music;

    public ClashOfClaws(FirebaseSDK firebase) {
        this.firebase = firebase;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new StateManager();
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        AudioManager.getInstance().playMusic(music);
        AudioManager.getInstance().loadGameSounds();
        gsm.push(new LoginState(gsm, firebase));
    }

    @Override
    public void resize(int width, int height) {
        if (gsm != null) gsm.resize(width, height);
    }

    @Override
    public void render() {
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (gsm != null) gsm.dispose();
        if (music != null) music.dispose();
        AudioManager.getInstance().disposeGameSounds();
    }
}
