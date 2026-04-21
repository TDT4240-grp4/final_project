package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AddNameState extends State {
    private Stage stage;
    private Skin skin;
    private Texture kittyTexture;
    private int selectedCatIndex;
    private String roomCode;

    public AddNameState(StateManager gsm,  int catIndex) {
        this(gsm, catIndex, null);
    }

    public AddNameState(StateManager gsm, int catIndex, String roomCode) {
        super(gsm);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.selectedCatIndex = catIndex;
        this.roomCode = roomCode;

        // Load the specific cat chosen in the previous screen
        kittyTexture = new Texture("cat" + (catIndex + 1) + ".png");
        Image kittyImage = new Image(kittyTexture);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        Label title = new Label("NAME YOUR KITTY", skin);
        title.setFontScale(0.9f);
        title.setColor(Color.BLACK);

        final TextField nameField = new TextField("", skin);
        nameField.setMessageText("Kitty Name...");
        nameField.getStyle().font.getData().setScale(0.75f);
        nameField.setAlignment(com.badlogic.gdx.utils.Align.center);

        TextButton returnBtn = new TextButton("Back", skin);
        returnBtn.getLabel().setFontScale(0.55f);
        TextButton letsClawBtn = new TextButton("LET'S CLAW", skin);
        letsClawBtn.getLabel().setFontScale(0.55f);
        letsClawBtn.setColor(Color.valueOf("1ca1e4"));

        returnBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new CharacterSelectionState(gsm));
            }
        });

        letsClawBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText().trim();
                if (!name.isEmpty() && name.matches("^[a-zA-Z0-9]*$")) {
                    Gdx.app.log("Game", "Starting game with: " + name);
                    gsm.set(new LobbyState(gsm, name, selectedCatIndex, roomCode));
                } else {
                    nameField.setColor(Color.RED);
                }
            }
        });

        float btnH = H * 0.1f;
        mainTable.add(title).padBottom(H * 0.03f).row();
        mainTable.add(kittyImage).size(H * 0.35f).padBottom(H * 0.03f).row();
        mainTable.add(nameField).width(W * 0.4f).height(btnH).padBottom(H * 0.04f).row();

        Table btnRow = new Table();
        btnRow.add(returnBtn).width(W * 0.18f).height(btnH).padRight(20);
        btnRow.add(letsClawBtn).width(W * 0.32f).height(btnH);

        mainTable.add(btnRow);
        stage.addActor(mainTable);
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.93f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {

    }

    @Override public void update(float dt) { stage.act(dt); }
    @Override public void dispose() {
        stage.dispose();
        kittyTexture.dispose();
    }
}
