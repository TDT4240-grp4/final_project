package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class PlayState extends State {
    private PlayLogic playLogic;
    // private PlayView playView;
    // private PlayController playController;

    public PlayState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        playLogic = new PlayLogic();
    }

    @Override
    public void update(float dt) {
        // Controller would update the Model based on Input here
        playLogic.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        // View would render the Model entities here
    }

    @Override
    public void resize(int width, int height) {
        // Update Viewport for dynamic scaling
    }

    @Override
    public void dispose() {
        // Clear resources
    }
}
