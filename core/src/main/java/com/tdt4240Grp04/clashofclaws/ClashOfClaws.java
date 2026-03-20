package com.tdt4240Grp04.clashofclaws;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.States.LoginState;
import com.tdt4240Grp04.clashofclaws.States.StateManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ClashOfClaws extends ApplicationAdapter {
    private SpriteBatch batch;
    private StateManager gsm;
    private FirebaseSDK firebase;

    public ClashOfClaws(FirebaseSDK firebase) {
        this.firebase = firebase;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new StateManager();
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
    }
}
