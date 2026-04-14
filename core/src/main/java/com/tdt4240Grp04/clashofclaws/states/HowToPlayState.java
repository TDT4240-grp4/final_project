package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class HowToPlayState extends State {
    private Stage stage;
    private Skin skin;
    private Texture rightPawTexture, leftPawTexture;
    private int currentPage = 1;
    private Image tutorialImage;
    private Label instructionsLabel;
    private Texture[] tutorialTextures;

    public HowToPlayState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        rightPawTexture = new Texture(Gdx.files.internal("right.png"));
        leftPawTexture = new Texture(Gdx.files.internal("left.png"));

        // 1. Setup the 4 tutorial images
        tutorialTextures = new Texture[4];
        tutorialTextures[0] = new Texture("tutorial_1.jpg");
        tutorialTextures[1] = new Texture("tutorial_2.png");
        tutorialTextures[2] = new Texture("tutorial_3.jpg");
        tutorialTextures[3] = new Texture("tutorial_4.jpg");

        // Initialize components
        tutorialImage = new Image(tutorialTextures[0]);
        instructionsLabel = new Label("", skin);
        instructionsLabel.setWrap(true);
        instructionsLabel.setAlignment(Align.center);
        updatePage(); // Call this once to fill in the first page of text

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label title = new Label("How To Play", skin);
        title.setFontScale(2.0f);
        title.setColor(Color.BLACK);

        // Content Row Table
        Table contentTable = new Table();

        // Setup Paw Buttons
        ImageButton leftPaw = new ImageButton(new TextureRegionDrawable(leftPawTexture));
        ImageButton rightPaw = new ImageButton(new TextureRegionDrawable(rightPawTexture));

        // --- ADD LISTENERS HERE ---
        rightPaw.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentPage < 4) {
                    currentPage++;
                    updatePage();
                }
            }
        });

        leftPaw.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentPage > 1) {
                    currentPage--;
                    updatePage();
                }
            }
        });

        // Assemble the Content Row: [Left Paw] [Image] [Text] [Right Paw]
        contentTable.add(leftPaw).size(100, 80).expandX().left().padLeft(50);
        contentTable.add(tutorialImage).size(450, 300).pad(10);
        contentTable.add(instructionsLabel).width(450).fillX().pad(10);
        contentTable.add(rightPaw).size(100, 80).expandX().right().padRight(50);

        // Back Button
        TextButton backBtn = new TextButton("BACK", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm, firebase));
            }
        });

        // Final Assembly
        mainTable.add(title).padTop(20).row();
        mainTable.add(contentTable).expand().center().row();
        mainTable.add(backBtn).width(200).height(60).padBottom(20);

        stage.addActor(mainTable);
    }

    // --- HELPER METHOD: This swaps the content based on currentPage ---
    private void updatePage() {
        // Change the image
        tutorialImage.setDrawable(new TextureRegionDrawable(tutorialTextures[currentPage - 1]));

        // Change the text
        switch(currentPage) {
            case 1:
                instructionsLabel.setText("EAT & GROW:\nUse the joystick to move.\nEat kibbles to grow!");
                break;
            case 2:
                instructionsLabel.setText("BATTLE:\nCut off other cats with your body.\nEat their remains!");
                break;
            case 3:
                instructionsLabel.setText("DASH:\nUse limited stamina to boost.\nWatch the bar above!");
                break;
            case 4:
                instructionsLabel.setText("WIN:\nLast kitty standing wins!");
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.93f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void update(float dt) { stage.act(dt); }

    @Override public void dispose() {
        stage.dispose();
        leftPawTexture.dispose();
        rightPawTexture.dispose();
        for (Texture t : tutorialTextures) t.dispose();
    }
}
