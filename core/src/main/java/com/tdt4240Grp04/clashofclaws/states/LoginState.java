package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class LoginState extends State {
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture angryCatTexture, titleTexture;

    public LoginState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        angryCatTexture = new Texture(Gdx.files.internal("angrycat.png"));
        titleTexture = new Texture(Gdx.files.internal("title.png"));

        // Define Styles once
        TextButton.TextButtonStyle primaryBtnStyle = createButtonStyle(Color.valueOf("1ca1e4"), Color.WHITE);
        TextButton.TextButtonStyle secondaryBtnStyle = createButtonStyle(Color.valueOf("85d6ff"), Color.BLACK);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // --- LEFT SIDE ---
        Table leftTable = new Table();
        leftTable.add(new Image(titleTexture)).size(600, 200).padBottom(20).row();
        leftTable.add(new Image(angryCatTexture)).size(400, 400);

        // --- RIGHT SIDE ---
        Table rightTable = new Table();

// --- Join Friend Box ---
        Table joinBox = new Table();
        joinBox.setBackground(skin.newDrawable("white", Color.WHITE)); // The big white container
        joinBox.pad(30);

        Label joinLabel = new Label("JOIN A FRIEND'S ROOM", skin);
        joinLabel.setColor(Color.BLACK);

// Create the blue row that holds BOTH the code and the arrow
        Table blueRow = new Table();
        blueRow.setBackground(skin.newDrawable("white", Color.valueOf("1ca1e4"))); // Blue background for the row
        blueRow.pad(5); // Padding so the code field doesn't touch the blue edges

        TextField codeField = new TextField("", skin);
        codeField.setMessageText("ROOM CODE");
// IMPORTANT: We set the background of the field to NULL so the blueRow shows through
        codeField.getStyle().background = null;
        codeField.getStyle().fontColor = Color.WHITE;

        TextButton arrowBtn = new TextButton("->", skin);
        arrowBtn.getStyle().up = null; // Remove button background so it's just text on the blueRow
        arrowBtn.getStyle().fontColor = Color.WHITE;

// Add field and arrow to the BLUE ROW
// Increased width to 350 so "ROOM CODE" is fully visible
        blueRow.add(codeField).width(350).height(60).padLeft(10);
        blueRow.add(arrowBtn).size(60, 60);

// Add everything to the white JOIN BOX
        joinBox.add(joinLabel).padBottom(20).row();
        joinBox.add(blueRow).width(420).height(70); // Fixed size for the blue bar

        // Main Buttons
        TextButton playBtn = new TextButton("PLAY", primaryBtnStyle);
        TextButton howToPlayBtn = new TextButton("HOW TO PLAY", secondaryBtnStyle);
        TextButton settingsBtn = new TextButton("SETTINGS", secondaryBtnStyle);

        // Assemble Right Table
        rightTable.add(joinBox).padBottom(20).row();
        rightTable.add(playBtn).width(320).height(80).padBottom(20).row();
        rightTable.add(howToPlayBtn).width(450).height(80).padBottom(20).row();
        rightTable.add(settingsBtn).width(450).height(80);

        mainTable.add(leftTable).expand().center().padLeft(50);
        mainTable.add(rightTable).expand().center().padRight(50);
        stage.addActor(mainTable);

        // --- LISTENERS ---
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new CharacterSelectionState(gsm, firebase));
            }
        });

        howToPlayBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new HowToPlayState(gsm, firebase));
            }
        });

        settingsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new SettingsState(gsm, firebase));
            }
        });

    }

    /**
     * Helper method to reduce repetition. Creates a tinted button style.
     */
    private TextButton.TextButtonStyle createButtonStyle(Color baseColor, Color fontColor) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("ComicConSans");
        style.fontColor = fontColor;
        style.up = skin.newDrawable("white", baseColor);
        style.down = skin.newDrawable("white", baseColor.cpy().mul(0.8f)); // Automatically makes click darker
        style.over = skin.newDrawable("white", baseColor.cpy().mul(1.1f)); // Automatically makes hover lighter
        return style;
    }

    @Override public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.93f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void update(float dt) { stage.act(dt); }

    @Override public void dispose() {
        stage.dispose(); skin.dispose(); atlas.dispose();
        angryCatTexture.dispose(); titleTexture.dispose();
    }
}
