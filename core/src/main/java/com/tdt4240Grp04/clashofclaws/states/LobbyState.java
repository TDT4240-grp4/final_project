package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.tdt4240Grp04.clashofclaws.network.GameClient;
import com.tdt4240Grp04.clashofclaws.network.Network;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.io.IOException;

public class LobbyState extends State {
    private static final String SERVER_IP = "20.251.119.106"; // Azure
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;

    private Stage stage;
    private Skin skin;
    private Texture lobbyBg, catTexture;
    private String playerName;
    private int selectedCatIndex;
    private GameClient gameClient;

    private Image playerCat;
    private Label nameLabel;
    private Vector2 playerPos;
    private float speed = 300f;

    private Label statusLabel;
    private Label codeLabel;
    private Touchpad joystick;
    private TextButton startGameBtn;
    private TextButton backBtn;
    private boolean inPrivateRoom = false;
    private String presetRoomCode;

    // Room mode UI
    private Table modeTable;       // Quick Match / Create Room / Join Room buttons
    private Table joinRoomTable;   // TextField + Join button (shown after tapping Join Room)
    private TextField roomCodeField;

    public LobbyState(StateManager gsm, String name, int catIndex) {
        this(gsm, name, catIndex, null);
    }

    public LobbyState(StateManager gsm, String name, int catIndex, String roomCode) {
        super(gsm);
        this.playerName = name;
        this.selectedCatIndex = catIndex;
        this.presetRoomCode = roomCode;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        gameClient = new GameClient(SERVER_IP, TCP_PORT, UDP_PORT);

        gameClient.getClient().addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                if (presetRoomCode != null) {
                    Network.JoinLobby msg = new Network.JoinLobby();
                    msg.name = playerName;
                    msg.catIndex = selectedCatIndex;
                    msg.roomCode = presetRoomCode;
                    gameClient.sendTCP(msg);
                    Gdx.app.postRunnable(() -> {
                        inPrivateRoom = true;
                        backBtn.setVisible(true);
                        statusLabel.setText("JOINING ROOM " + presetRoomCode + "...");
                    });
                }
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.RoomJoined) {
                    Network.RoomJoined msg = (Network.RoomJoined) object;
                    Gdx.app.postRunnable(() -> {
                        codeLabel.setText("ROOM CODE: " + msg.roomCode);
                        modeTable.setVisible(false);
                        joinRoomTable.setVisible(false);
                    });
                } else if (object instanceof Network.RoomError) {
                    Network.RoomError err = (Network.RoomError) object;
                    Gdx.app.postRunnable(() -> statusLabel.setText(err.message));
                } else if (object instanceof Network.LobbyUpdate) {
                    Network.LobbyUpdate update = (Network.LobbyUpdate) object;
                    Gdx.app.postRunnable(() -> {
                        statusLabel.setText("WAITING... " + update.currentPlayers + "/6 PLAYERS");
                        if (inPrivateRoom && startGameBtn != null) {
                            startGameBtn.setVisible(update.currentPlayers >= 2);
                        }
                    });
                } else if (object instanceof Network.GameStart) {
                    Gdx.app.postRunnable(() ->
                        gsm.set(new PlayState(gsm, gameClient, playerName, selectedCatIndex)));
                }
            }
        });

        new Thread(() -> {
            try { gameClient.connect(); } catch (IOException e) { e.printStackTrace(); }
        }).start();

        // Background
        lobbyBg = new Texture("lobbyarea.png");
        Image background = new Image(lobbyBg);
        background.setFillParent(true);
        stage.addActor(background);

        // Player cat sprite
        catTexture = new Texture("cat" + (catIndex + 1) + ".png");
        playerCat = new Image(catTexture);
        playerCat.setSize(80, 80);
        playerPos = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        nameLabel = new Label(name, skin);
        nameLabel.setAlignment(Align.center);
        nameLabel.setColor(Color.WHITE);
        stage.addActor(playerCat);
        stage.addActor(nameLabel);

        // Status popup (top center)
        Table topPopup = new Table();
        topPopup.setFillParent(true);
        topPopup.top().padTop(20);
        topPopup.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        codeLabel = new Label("", skin);
        statusLabel = new Label("CONNECTING...", skin);
        statusLabel.setColor(Color.YELLOW);
        topPopup.add(codeLabel).row();
        topPopup.add(statusLabel).padTop(5);
        stage.addActor(topPopup);

        // Mode selection buttons (center screen)
        modeTable = new Table();
        modeTable.setFillParent(true);
        modeTable.center();

        TextButton quickMatchBtn = new TextButton("Quick Match", skin);
        quickMatchBtn.getLabel().setFontScale(0.55f);
        TextButton createRoomBtn = new TextButton("Create Private Room", skin);
        createRoomBtn.getLabel().setFontScale(0.55f);
        TextButton joinRoomBtn = new TextButton("Join with Code", skin);
        joinRoomBtn.getLabel().setFontScale(0.55f);

        quickMatchBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                Network.JoinLobby msg = new Network.JoinLobby();
                msg.name = playerName;
                msg.catIndex = selectedCatIndex;
                msg.roomCode = null;
                gameClient.sendTCP(msg);
                inPrivateRoom = true;
                modeTable.setVisible(false);
                backBtn.setVisible(true);
                startGameBtn.setVisible(true);
                statusLabel.setText("SEARCHING FOR MATCH...");
            }
        });

        createRoomBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                Network.CreateRoom msg = new Network.CreateRoom();
                msg.name = playerName;
                msg.catIndex = selectedCatIndex;
                gameClient.sendTCP(msg);
                inPrivateRoom = true;
                modeTable.setVisible(false);
                backBtn.setVisible(true);
                statusLabel.setText("WAITING FOR PLAYERS...");
            }
        });

        joinRoomBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                modeTable.setVisible(false);
                joinRoomTable.setVisible(true);
            }
        });

        TextButton modeBackBtn = new TextButton("Back", skin);
        modeBackBtn.getLabel().setFontScale(0.55f);
        modeBackBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gameClient.disconnect();
                gsm.set(new AddNameState(gsm, selectedCatIndex));
            }
        });

        modeTable.add(quickMatchBtn).width(400).height(80).pad(10).row();
        modeTable.add(createRoomBtn).width(400).height(80).pad(10).row();
        modeTable.add(joinRoomBtn).width(400).height(80).pad(10).row();
        modeTable.add(modeBackBtn).width(400).height(80).pad(10);
        if (presetRoomCode != null) modeTable.setVisible(false);
        stage.addActor(modeTable);

        // Join room via code (hidden initially)
        joinRoomTable = new Table();
        joinRoomTable.setFillParent(true);
        joinRoomTable.center();
        joinRoomTable.setVisible(false);

        roomCodeField = new TextField("", skin);
        roomCodeField.setMessageText("Enter 6-char code");
        roomCodeField.setMaxLength(6);
        TextButton confirmJoinBtn = new TextButton("JOIN", skin);
        confirmJoinBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                String code = roomCodeField.getText().trim().toUpperCase();
                if (code.length() == 6) {
                    Network.JoinLobby msg = new Network.JoinLobby();
                    msg.name = playerName;
                    msg.catIndex = selectedCatIndex;
                    msg.roomCode = code;
                    gameClient.sendTCP(msg);
                    inPrivateRoom = true;
                    joinRoomTable.setVisible(false);
                    backBtn.setVisible(true);
                    statusLabel.setText("JOINING ROOM " + code + "...");
                } else {
                    statusLabel.setText("Code must be 6 characters");
                }
            }
        });
        TextButton cancelJoinBtn = new TextButton("Cancel", skin);
        cancelJoinBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                joinRoomTable.setVisible(false);
                modeTable.setVisible(true);
            }
        });
        joinRoomTable.add(roomCodeField).width(300).height(60).pad(10).row();
        joinRoomTable.add(confirmJoinBtn).width(200).height(60).pad(10).row();
        joinRoomTable.add(cancelJoinBtn).width(200).height(60).pad(10);
        stage.addActor(joinRoomTable);

        // Start Game button — for private room host (hidden until 2+ players)
        startGameBtn = new TextButton("START GAME", skin);
        startGameBtn.setVisible(false);
        Table startTable = new Table();
        startTable.setFillParent(true);
        startTable.bottom().padBottom(320);
        startTable.add(startGameBtn).width(400).height(80);
        startGameBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gameClient.sendTCP(new Network.RequestStartGame());
            }
        });
        stage.addActor(startTable);

        // Back button — bottom center, shown when waiting in any mode
        backBtn = new TextButton("BACK", skin);
        backBtn.setVisible(false);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gameClient.disconnect();
                gsm.set(new LobbyState(gsm, playerName, selectedCatIndex));
            }
        });
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom().padBottom(30);
        backTable.add(backBtn).width(300).height(70);
        stage.addActor(backTable);

        // Joystick — bottom right, scaled to screen
        float jSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.22f;
        float knobSize = jSize * 0.42f;

        Touchpad.TouchpadStyle touchpadStyle = skin.get(Touchpad.TouchpadStyle.class);
        touchpadStyle.background.setMinWidth(jSize);
        touchpadStyle.background.setMinHeight(jSize);
        touchpadStyle.knob.setMinWidth(knobSize);
        touchpadStyle.knob.setMinHeight(knobSize);

        joystick = new Touchpad(jSize * 0.05f, touchpadStyle);
        joystick.setBounds(
            Gdx.graphics.getWidth() - jSize - 24,
            24,
            jSize,
            jSize
        );
        stage.addActor(joystick);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
        if (joystick.isTouched()) {
            playerPos.x += joystick.getKnobPercentX() * speed * dt;
            playerPos.y += joystick.getKnobPercentY() * speed * dt;
        }
        playerPos.x = Math.max(0, Math.min(playerPos.x, Gdx.graphics.getWidth() - playerCat.getWidth()));
        playerPos.y = Math.max(0, Math.min(playerPos.y, Gdx.graphics.getHeight() - playerCat.getHeight()));
        playerCat.setPosition(playerPos.x, playerPos.y);
        nameLabel.setPosition(
            playerPos.x + (playerCat.getWidth() / 2f) - (nameLabel.getWidth() / 2f),
            playerPos.y + playerCat.getHeight() + 5);
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override public void resize(int width, int height) { }

    @Override
    public void dispose() {
        stage.dispose();
        lobbyBg.dispose();
        catTexture.dispose();
    }
}
