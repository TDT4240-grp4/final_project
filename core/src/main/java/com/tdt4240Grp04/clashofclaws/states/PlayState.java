package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class PlayState extends State {
    private PlayLogic playLogic;
    // private PlayView playView;
    private PlayController playController;

    public PlayState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        playLogic = new PlayLogic();
        playController = new PlayController(playLogic.getPlayer());
    }

    @Override
    public void update(float dt) {
        // Controller would update the Model based on Input here
        playLogic.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        // View would render the Model entities here
        playController.render(); // The controller needs to be rendered to show the joystick
    }

    @Override
    public void resize(int width, int height) {
        // Update Viewport for dynamic scaling
        playController.resize(width, height);
    }

    @Override
    public void dispose() {
        // Clear resources
        playController.dispose();
    }
}
