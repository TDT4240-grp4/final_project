package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToPlayState extends State {
    private Stage stage;
    private Skin skin;
    private Texture rightPawTexture, leftPawTexture;
    private int currentPage = 1;
    private static final int TOTAL_PAGES = 5;
    private Label instructionsLabel;
    private Table powerupPanel;
    private Texture speedTex, shieldTex, magnetTex, dividerTex;
    private Texture dotTexActive, dotTexInactive;
    private Image[] dots;

    public HowToPlayState(StateManager gsm) {
        super(gsm);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        rightPawTexture = new Texture(Gdx.files.internal("right.png"));
        leftPawTexture  = new Texture(Gdx.files.internal("left.png"));

        // Powerup textures
        speedTex  = new Texture(Gdx.files.internal("red_fish.png"));
        shieldTex = new Texture(Gdx.files.internal("fish.png"));
        magnetTex = new Texture(Gdx.files.internal("grey_fish.png"));

        // Divider texture
        Pixmap divPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        divPixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        divPixmap.fill();
        dividerTex = new Texture(divPixmap);
        divPixmap.dispose();

        // Dot textures
        int dotPx = 16;
        Pixmap activePixmap = new Pixmap(dotPx, dotPx, Pixmap.Format.RGBA8888);
        activePixmap.setColor(Color.WHITE);
        activePixmap.fillCircle(dotPx / 2, dotPx / 2, dotPx / 2 - 1);
        dotTexActive = new Texture(activePixmap);
        activePixmap.dispose();

        Pixmap inactivePixmap = new Pixmap(dotPx, dotPx, Pixmap.Format.RGBA8888);
        inactivePixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        inactivePixmap.fillCircle(dotPx / 2, dotPx / 2, dotPx / 2 - 1);
        dotTexInactive = new Texture(inactivePixmap);
        inactivePixmap.dispose();

        // Sizing constants
        float pawW       = W * 0.07f;
        float pawH       = H * 0.10f;
        float contentW   = W * 0.78f;
        float contentH   = H * 0.50f;
        float colW       = contentW / 3f - W * 0.02f;
        float imgSize    = H * 0.13f;
        float labelW     = colW - W * 0.02f;
        float dotSize    = H * 0.022f;
        float dotPad     = W * 0.012f;
        float btnW       = W * 0.15f;
        float btnH       = H * 0.08f;

        // Instructions label (pages 1-3, 5)
        instructionsLabel = new Label("", skin);
        instructionsLabel.setWrap(true);
        instructionsLabel.setAlignment(Align.center);

        // --- Powerup panel (page 4) ---
        powerupPanel = new Table();
        powerupPanel.pad(H * 0.01f);
        powerupPanel.center();

        Label powerupHeader = new Label("POWER-UPS:", skin);
        powerupHeader.setAlignment(Align.center);
        powerupPanel.add(powerupHeader).center().padBottom(H * 0.02f).colspan(5).row();

        // Speed Boost column
        Table speedCol = new Table();
        speedCol.center();
        speedCol.add(new Image(speedTex)).size(imgSize, imgSize).center().padBottom(H * 0.01f).row();
        Label speedLabel = new Label("Speed Boost:\n1.5x speed for 5 sec", skin);
        speedLabel.setWrap(true);
        speedLabel.setAlignment(Align.center);
        speedLabel.setFontScale(0.7f);
        speedCol.add(speedLabel).width(labelW).center();

        // Shield column
        Table shieldCol = new Table();
        shieldCol.center();
        shieldCol.add(new Image(shieldTex)).size(imgSize, imgSize).center().padBottom(H * 0.01f).row();
        Label shieldLabel = new Label("Shield:\nBlocks fatal collision for 8 sec", skin);
        shieldLabel.setWrap(true);
        shieldLabel.setAlignment(Align.center);
        shieldLabel.setFontScale(0.7f);
        shieldCol.add(shieldLabel).width(labelW).center();

        // Kibble Magnet column
        Table magnetCol = new Table();
        magnetCol.center();
        magnetCol.add(new Image(magnetTex)).size(imgSize, imgSize).center().padBottom(H * 0.01f).row();
        Label magnetLabel = new Label("Kibble Magnet:\nPulls kibbles to you for 6 sec", skin);
        magnetLabel.setWrap(true);
        magnetLabel.setAlignment(Align.center);
        magnetLabel.setFontScale(0.7f);
        magnetCol.add(magnetLabel).width(labelW).center();

        float divPad = W * 0.01f;
        powerupPanel.add(speedCol).width(colW).center();
        powerupPanel.add(new Image(dividerTex)).width(2).fillY().pad(0, divPad, 0, divPad);
        powerupPanel.add(shieldCol).width(colW).center();
        powerupPanel.add(new Image(dividerTex)).width(2).fillY().pad(0, divPad, 0, divPad);
        powerupPanel.add(magnetCol).width(colW).center().row();
        powerupPanel.setVisible(false);

        updatePage();

        // --- Main layout ---
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label title = new Label("How To Play", skin);
        title.setFontScale(H * 0.0025f);
        title.setColor(Color.BLACK);

        // Paw buttons
        ImageButton leftPaw  = new ImageButton(new TextureRegionDrawable(leftPawTexture));
        ImageButton rightPaw = new ImageButton(new TextureRegionDrawable(rightPawTexture));

        rightPaw.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (currentPage < TOTAL_PAGES) { currentPage++; updatePage(); }
            }
        });
        leftPaw.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (currentPage > 1) { currentPage--; updatePage(); }
            }
        });

        // Content stack
        Stack contentStack = new Stack();
        contentStack.add(instructionsLabel);
        contentStack.add(powerupPanel);

        Table contentTable = new Table();
        contentTable.add(leftPaw).size(pawW, pawH).left().padRight(W * 0.01f);
        contentTable.add(contentStack).size(contentW, contentH).pad(H * 0.01f);
        contentTable.add(rightPaw).size(pawW, pawH).right().padLeft(W * 0.01f);

        // Dot indicators
        Table dotsTable = new Table();
        dots = new Image[TOTAL_PAGES];
        for (int i = 0; i < TOTAL_PAGES; i++) {
            dots[i] = new Image(i == 0 ? dotTexActive : dotTexInactive);
            dotsTable.add(dots[i]).size(dotSize, dotSize)
                .padRight(i < TOTAL_PAGES - 1 ? dotPad : 0);
        }

        // Back button
        TextButton backBtn = new TextButton("BACK", skin);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm));
            }
        });

        mainTable.add(title).padTop(H * 0.03f).row();
        mainTable.add(contentTable).expand().center().row();
        mainTable.add(dotsTable).padBottom(H * 0.01f).row();
        mainTable.add(backBtn).width(btnW).height(btnH).padBottom(H * 0.03f);

        stage.addActor(mainTable);
    }

    private void updatePage() {
        boolean isPowerupPage = (currentPage == 4);
        instructionsLabel.setVisible(!isPowerupPage);
        powerupPanel.setVisible(isPowerupPage);

        switch (currentPage) {
            case 1: instructionsLabel.setText("EAT & GROW:\nUse the joystick to move.\nEat kibbles to grow!"); break;
            case 2: instructionsLabel.setText("BATTLE:\nCut off other cats with your body.\nEat their remains!"); break;
            case 3: instructionsLabel.setText("DASH:\nUse limited stamina to boost.\nWatch the bar above!"); break;
            case 5: instructionsLabel.setText("WIN:\nLast kitty standing wins!"); break;
        }

        if (dots != null) {
            for (int i = 0; i < dots.length; i++) {
                dots[i].setDrawable(new TextureRegionDrawable(
                    i == currentPage - 1 ? dotTexActive : dotTexInactive));
            }
        }
    }

    @Override public void render(SpriteBatch sb) {
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
        speedTex.dispose();
        shieldTex.dispose();
        magnetTex.dispose();
        dividerTex.dispose();
        dotTexActive.dispose();
        dotTexInactive.dispose();
    }
}
