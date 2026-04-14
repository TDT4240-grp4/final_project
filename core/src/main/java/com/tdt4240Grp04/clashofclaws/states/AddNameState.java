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
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class AddNameState extends State {
    private Stage stage;
    private Skin skin;
    private Texture kittyTexture;
    private int selectedCatIndex;

    public AddNameState(StateManager gsm, FirebaseSDK firebase, int catIndex) {
        super(gsm);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.selectedCatIndex = catIndex;

        // Load the specific cat chosen in the previous screen
        kittyTexture = new Texture("cat" + (catIndex + 1) + ".png");
        Image kittyImage = new Image(kittyTexture);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // 1. Title
        Label title = new Label("NAME YOUR KITTY", skin);
        title.setFontScale(1.8f);
        title.setColor(Color.BLACK);

        // 2. Input Field
        final TextField nameField = new TextField("", skin);
        nameField.setMessageText("Kitty Name...");

        // 3. Buttons
        TextButton returnBtn = new TextButton("BACK", skin);
        TextButton letsClawBtn = new TextButton("LET'S CLAW", skin);
        letsClawBtn.setColor(Color.valueOf("1ca1e4")); // Your blue theme

        // --- BUTTON LOGIC ---
        returnBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new CharacterSelectionState(gsm, firebase));
            }
        });

        letsClawBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText().trim();

                // VALIDATION: No empty, only letters and numbers
                if (!name.isEmpty() && name.matches("^[a-zA-Z0-9]*$")) {
                    Gdx.app.log("Game", "Starting game with: " + name);
                    gsm.set(new LobbyState(gsm, firebase, name, selectedCatIndex));
                } else {
                    // Visual feedback: Shake the field or turn it red
                    nameField.setColor(Color.RED);
                }
            }
        });

        // --- ASSEMBLE ---
        mainTable.add(title).padBottom(20).row();
        mainTable.add(kittyImage).size(200, 200).padBottom(20).row();
        mainTable.add(nameField).width(400).height(60).padBottom(30).row();

        Table btnRow = new Table();
        btnRow.add(returnBtn).width(200).height(60).padRight(20);
        btnRow.add(letsClawBtn).width(400).height(60);

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
