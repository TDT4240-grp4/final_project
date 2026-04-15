package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.tdt4240Grp04.clashofclaws.network.GameClient;
import com.tdt4240Grp04.clashofclaws.network.Network;

import java.io.IOException;

public class LobbyState extends State {
    private Stage stage;
    private Skin skin;
    private Texture lobbyBg, catTexture;
    private String playerName;
    private int selectedCatIndex;
    private FirebaseSDK firebase;
    private GameClient gameClient;

    // Player Components
    private Image playerCat;
    private Label nameLabel;
    private Vector2 playerPos;
    private float speed = 300f; // Adjusted for full screen

    // UI Components
    private Label statusLabel, codeLabel;
    private Touchpad joystick;
    private int playerCount = 1;
    private float countdown = 10f;

    public LobbyState(StateManager gsm, FirebaseSDK firebase, String name, int catIndex) {
        super(gsm);
        this.playerName = name;
        this.selectedCatIndex = catIndex;
        this.firebase = firebase;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        gameClient = new GameClient("10.0.2.2", 54555, 54777); // Local
        //gameClient = new GameClient("20.251.119.106", 54555, 54777); // Azure

        gameClient.getClient().addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                // when connected, tell the server we joined the lobby
                Network.JoinLobby joinMsg = new Network.JoinLobby();
                joinMsg.name = playerName;
                joinMsg.catIndex = selectedCatIndex;
                gameClient.sendTCP(joinMsg);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.LobbyUpdate) {
                    Network.LobbyUpdate update = (Network.LobbyUpdate) object;
                    // update UI on main thread
                    Gdx.app.postRunnable(() -> {
                        statusLabel.setText("WAITING... " + update.currentPlayers + "/2 PLAYERS");
                    });
                } else if (object instanceof Network.GameStart) {
                    Gdx.app.postRunnable(() -> {
                        // pass the connected gameClient to PlayState
                        gsm.set(new PlayState(gsm, firebase, gameClient, playerName, selectedCatIndex));
                    });
                }
            }
        });

        new Thread(() -> {
            try {
                gameClient.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 1. Full Screen Background
        lobbyBg = new Texture("lobbyarea.png");
        Image background = new Image(lobbyBg);
        background.setFillParent(true); // Takes up the whole screen
        stage.addActor(background);

        // 2. Player Setup
        catTexture = new Texture("cat" + (catIndex + 1) + ".png");
        playerCat = new Image(catTexture);
        playerCat.setSize(80, 80);
        playerPos = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

        nameLabel = new Label(name, skin);
        nameLabel.setAlignment(Align.center);
        nameLabel.setColor(Color.WHITE);

        stage.addActor(playerCat);
        stage.addActor(nameLabel);

        // 3. Floating Status "Popup" at the Top
        Table topPopup = new Table();
        topPopup.setFillParent(true);
        topPopup.top().padTop(20);

        // Style the popup background (optional, using a tint for visibility)
        topPopup.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));

        codeLabel = new Label("GAME CODE: ABCD", skin);
        // ****statusLabel = new Label("WAITING FOR 1 MORE PLAYER...", skin);
        statusLabel = new Label("CONNECTING TO SERVER...", skin);
        statusLabel.setColor(Color.YELLOW);

        topPopup.add(codeLabel).row();
        topPopup.add(statusLabel).padTop(5);
        stage.addActor(topPopup);


        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();

        touchpadStyle.background = skin.newDrawable("white", new Color(1, 1, 1, 0.2f));
        touchpadStyle.knob = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        touchpadStyle.knob.setMinWidth(80);
        touchpadStyle.knob.setMinHeight(80);
        joystick = new Touchpad(10, touchpadStyle);
        joystick.setBounds(80, 80, 200, 200); // Position and overall size
        stage.addActor(joystick);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);

        if (joystick.isTouched()) {
            playerPos.x += joystick.getKnobPercentX() * speed * dt;
            playerPos.y += joystick.getKnobPercentY() * speed * dt;
        }

        if (playerPos.x < 0) playerPos.x = 0;
        if (playerPos.x > Gdx.graphics.getWidth() - playerCat.getWidth())
            playerPos.x = Gdx.graphics.getWidth() - playerCat.getWidth();
        if (playerPos.y < 0) playerPos.y = 0;
        if (playerPos.y > Gdx.graphics.getHeight() - playerCat.getHeight())
            playerPos.y = Gdx.graphics.getHeight() - playerCat.getHeight();

        playerCat.setPosition(playerPos.x, playerPos.y);

        // Name tag follows above the cat
        nameLabel.setPosition(
            playerPos.x + (playerCat.getWidth() / 2f) - (nameLabel.getWidth() / 2f),
            playerPos.y + playerCat.getHeight() + 5
        );
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override public void dispose() {
        stage.dispose();
        lobbyBg.dispose();
        catTexture.dispose();
    }
}
