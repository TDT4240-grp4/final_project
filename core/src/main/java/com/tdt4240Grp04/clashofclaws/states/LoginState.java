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

public class LoginState extends State {
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture angryCatTexture, titleTexture;

    public LoginState(StateManager gsm) {
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

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // --- LEFT SIDE: title + cat ---
        Table leftTable = new Table();
        leftTable.add(new Image(titleTexture)).width(W * 0.38f).height(H * 0.18f).padBottom(12).row();
        leftTable.add(new Image(angryCatTexture)).size(H * 0.52f);

        // --- RIGHT SIDE ---
        Table rightTable = new Table();

        // Join Friend Box
        Table joinBox = new Table();
        joinBox.setBackground(skin.newDrawable("white", Color.WHITE));
        joinBox.pad(16);

        Label joinLabel = new Label("JOIN A FRIEND'S ROOM", skin);
        joinLabel.setColor(Color.BLACK);
        joinLabel.setFontScale(0.55f);

        Table blueRow = new Table();
        blueRow.setBackground(skin.newDrawable("white", Color.valueOf("1ca1e4")));
        blueRow.pad(4);

        TextField codeField = new TextField("", skin);
        codeField.setMessageText("ROOM CODE");
        codeField.getStyle().background = null;
        codeField.getStyle().fontColor = Color.WHITE;
        codeField.getStyle().font.getData().setScale(0.55f);

        TextButton arrowBtn = new TextButton("->", skin);
        arrowBtn.getStyle().up = null;
        arrowBtn.getStyle().fontColor = Color.WHITE;
        arrowBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                String code = codeField.getText().trim().toUpperCase();
                if (code.length() == 6) {
                    gsm.set(new CharacterSelectionState(gsm, code));
                } else {
                    codeField.setColor(Color.RED);
                }
            }
        });

        float btnW = W * 0.28f;
        float btnH = H * 0.1f;

        blueRow.add(codeField).width(btnW * 0.8f).height(btnH).padLeft(8);
        blueRow.add(arrowBtn).size(btnH, btnH);

        joinBox.add(joinLabel).padBottom(10).row();
        joinBox.add(blueRow).width(btnW * 1.1f).height(btnH + 8);

        TextButton playBtn = new TextButton("PLAY", primaryBtnStyle);
        TextButton howToPlayBtn = new TextButton("HOW TO PLAY", secondaryBtnStyle);
        TextButton settingsBtn = new TextButton("SETTINGS", secondaryBtnStyle);

        rightTable.add(joinBox).padBottom(14).row();
        rightTable.add(playBtn).width(btnW).height(btnH).padBottom(10).row();
        rightTable.add(howToPlayBtn).width(btnW).height(btnH).padBottom(10).row();
        rightTable.add(settingsBtn).width(btnW).height(btnH);

        mainTable.add(leftTable).expand().center().padLeft(30);
        mainTable.add(rightTable).expand().center().padRight(30);
        stage.addActor(mainTable);

        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                String code = codeField.getText().trim().toUpperCase();
                if (code.length() == 6) {
                    gsm.set(new CharacterSelectionState(gsm, code));
                } else {
                    gsm.set(new CharacterSelectionState(gsm));
                }
            }
        });

        howToPlayBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new HowToPlayState(gsm));
            }
        });

        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new SettingsState(gsm));
            }
        });

    }

    /**
     * Helper method to reduce repetition. Creates a tinted button style.
     */
    private TextButton.TextButtonStyle createButtonStyle(Color baseColor, Color fontColor) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        com.badlogic.gdx.graphics.g2d.BitmapFont font = skin.getFont("ComicConSans");
        font.getData().setScale(0.55f);
        style.font = font;
        style.fontColor = fontColor;
        style.up = skin.newDrawable("white", baseColor);
        style.down = skin.newDrawable("white", baseColor.cpy().mul(0.8f));
        style.over = skin.newDrawable("white", baseColor.cpy().mul(1.1f));
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
