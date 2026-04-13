package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;

public class PlayState extends State {
    private PlayLogic playLogic;
    private PlayView playView;
    private PlayController playController;
    private FirebaseSDK firebase;

    public PlayState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        playLogic = new PlayLogic();
        playView = new PlayView(playLogic.getEngine(), playLogic.getPlayer());
        playController = new PlayController(playLogic.getPlayer());
    }

    @Override
    public void update(float dt) {
        playLogic.update(dt);

        PlayerComponent playerComp = playLogic.getPlayer().getComponent(PlayerComponent.class);
        if (playerComp != null && playerComp.isDead) {
            gsm.set(new GameOverState(gsm, firebase));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.804f, 0.933f, 0.996f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playView.render(sb);
        playController.render();
    }

    @Override
    public void resize(int width, int height) {
        playView.resize(width, height);
        playController.resize(width, height);
    }

    @Override
    public void dispose() {
        playView.dispose();
        playController.dispose();
        playLogic.dispose();
    }
}
