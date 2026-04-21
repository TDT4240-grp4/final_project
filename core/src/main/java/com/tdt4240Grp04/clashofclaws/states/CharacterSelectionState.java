package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;

public class CharacterSelectionState extends State {
    private Stage stage;
    private Skin skin;
    private Texture[] catTextures;
    private Texture kibbleTexture, leftPawTex, rightPawTex;
    private int currentCatIndex = 0;

    // UI Elements that change
    private Image bigCatDisplay;
    private Table statsTable;
    private Table previewBar;

    private String roomCode;

    public CharacterSelectionState(StateManager gsm) {
        this(gsm, null);
    }

    public CharacterSelectionState(StateManager gsm, String roomCode) {
        super(gsm);
        this.roomCode = roomCode;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load Assets
        kibbleTexture = new Texture("fish.png");
        leftPawTex = new Texture("left.png");
        rightPawTex = new Texture("right.png");
        catTextures = new Texture[]{new Texture("cat1.png"), new Texture("cat2.png"), new Texture("cat3.png")};

        bigCatDisplay = new Image(catTextures[0]);
        statsTable = new Table();
        previewBar = new Table();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // 1. Title
        Label title = new Label("Kitty Selection", skin);
        title.setFontScale(0.9f);

        ImageButton leftBtn = new ImageButton(new Image(leftPawTex).getDrawable());
        ImageButton rightBtn = new ImageButton(new Image(rightPawTex).getDrawable());
        TextButton backBtn = new TextButton("BACK", skin);
        backBtn.getLabel().setFontScale(0.55f);
        TextButton playBtn = new TextButton("SELECT & PLAY", skin);
        playBtn.getLabel().setFontScale(0.55f);
        playBtn.setColor(Color.valueOf("1ca1e4"));

        //button functions
        leftBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCatIndex = (currentCatIndex == 0) ? catTextures.length - 1 : currentCatIndex - 1;
                updateUI();
            }
        });

        rightBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCatIndex = (currentCatIndex + 1) % catTextures.length;
                updateUI();
            }
        });

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm));
            }
        });

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new AddNameState(gsm, currentCatIndex, roomCode));
                Gdx.app.log("Game", "Starting game with Cat: " + currentCatIndex);
            }
        });

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float catSize = H * 0.37f;
        float arrowSize = H * 0.08f;
        float btnH = H * 0.1f;

        // [Left Paw] [Big Cat] [Stats] [Right Paw]
        Table centerRow = new Table();
        centerRow.add(leftBtn).size(arrowSize).pad(10);
        centerRow.add(bigCatDisplay).size(catSize).pad(10);
        centerRow.add(statsTable).width(W * 0.35f).pad(10);
        centerRow.add(rightBtn).size(arrowSize).pad(10);

        Table navigationRow = new Table();
        navigationRow.add(backBtn).width(W * 0.2f).height(btnH).pad(10);
        navigationRow.add(playBtn).width(W * 0.35f).height(btnH).pad(10);

        // Assemble: title → [cat + stats row] → preview → nav
        mainTable.add(title).padTop(H * 0.03f).row();
        mainTable.add(centerRow).expand().center().row();
        mainTable.add(previewBar).padBottom(H * 0.01f).row();
        mainTable.add(navigationRow).padBottom(H * 0.02f);


        stage.addActor(mainTable);
        updateUI();
    }

    private void updateUI() {
        bigCatDisplay.setDrawable(new Image(catTextures[currentCatIndex]).getDrawable());

        // Update Stats for each cat
        statsTable.clear();
        if (currentCatIndex == 0) {
            addStatRow("Speed", 3);
            addStatRow("Starting Size", 2);
            addStatRow("Growing Size", 5);
            addStatRow("Dash Speed", 4);
        } else if (currentCatIndex == 1) {
            addStatRow("Speed", 4);
            addStatRow("Starting Size", 3);
            addStatRow("Growing Size", 2);
            addStatRow("Dash Speed", 5);
        } else if (currentCatIndex == 2) {
            addStatRow("Speed", 5);
            addStatRow("Starting Size", 1);
            addStatRow("Growing Size", 3);
            addStatRow("Dash Speed", 4);
        }

        // Update Preview Bar
        previewBar.clear();

        // Calculate indices for circular scrolling
        int prevIndex = (currentCatIndex == 0) ? catTextures.length - 1 : currentCatIndex - 1;
        int nextIndex = (currentCatIndex + 1) % catTextures.length;

        Image prevCat = new Image(catTextures[prevIndex]);
        prevCat.setColor(1, 1, 1, 0.3f); // Very faded
        float H = Gdx.graphics.getHeight();
        float smallCat = H * 0.04f;
        float bigCat   = H * 0.065f;
        previewBar.add(prevCat).size(smallCat).padRight(16);

        Image centerCat = new Image(catTextures[currentCatIndex]);
        centerCat.setColor(Color.WHITE);
        previewBar.add(centerCat).size(bigCat).padLeft(8).padRight(8);

        Image nextCat = new Image(catTextures[nextIndex]);
        nextCat.setColor(1, 1, 1, 0.3f);
        previewBar.add(nextCat).size(smallCat).padLeft(16);
    }

    private void addStatRow(String name, int kibbleCount) {
        Label label = new Label(name, skin);
        label.setFontScale(0.55f);
        statsTable.add(label).left().padRight(10);
        Table kibbleRow = new Table();
        for (int i = 0; i < kibbleCount; i++) {
            kibbleRow.add(new Image(kibbleTexture)).width(24).height(15).padRight(4);
        }
        statsTable.add(kibbleRow).left().row();
    }

    @Override public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.8f, 0.93f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override public void dispose() {
        stage.dispose();
        kibbleTexture.dispose();
        for (Texture t : catTextures) t.dispose();
    }
}
