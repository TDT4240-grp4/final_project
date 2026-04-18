package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.tdt4240Grp04.clashofclaws.network.GameClient;

public class PlayState extends State {
    private PlayLogic playLogic;
    private PlayView playView;
    private PlayController playController;
    private FirebaseSDK firebase;
    private GameClient gameClient;
    private String playerName;
    private int catIndex;

    public PlayState(StateManager gsm, FirebaseSDK firebase, GameClient gameClient, String name, int catIndex) {
        super(gsm);
        this.firebase = firebase;
        this.gameClient = gameClient;
        this.playerName = name;
        this.catIndex = catIndex;
        playLogic = new PlayLogic(gameClient, name, catIndex);
        playView = new PlayView(playLogic.getEngine(), playLogic.getPlayer());
        playController = new PlayController(playLogic.getPlayer(), playView.getStage(), playView.getSkin());

        Gdx.input.setInputProcessor(playView.getStage());
    }

    @Override
    public void update(float dt) {
        playLogic.update(dt);
        if (playView.isQuitRequested()) {
            gameClient.disconnect();
            gsm.set(new LobbyState(gsm, firebase, playerName, catIndex));
            return;
        }
        if (playLogic.isPlayerDead()) {
            gsm.set(new GameOverState(gsm, firebase));
        }
        else if (playLogic.hasPlayerWon()) {
            gsm.set(new WinnerState(gsm, firebase));
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
