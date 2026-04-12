package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class PlayState extends State {
    private PlayLogic playLogic;
    private PlayView playView;
    private PlayController playController;

    public PlayState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        playLogic = new PlayLogic();
        playView = new PlayView(playLogic.getEngine());
        playController = new PlayController(playLogic.getPlayer());
    }

    @Override
    public void update(float dt) {
        playLogic.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        playView.render();
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
    }
}
