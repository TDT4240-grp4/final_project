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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;

public class ResultsState extends State {
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture catTexture;
    private final ResultsData data;
    private final FirebaseSDK firebase;

    public ResultsState(StateManager gsm, FirebaseSDK firebase, ResultsData data) {
        super(gsm);
        this.firebase = firebase;
        this.data = data;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin  = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        catTexture = new Texture(Gdx.files.internal("cat" + (data.catIndex + 1) + ".png"));

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title
        String titleText = data.isWinner ? "WINNER!" : "YOU DIED";
        Color  titleColor = data.isWinner ? Color.valueOf("1ca1e4") : Color.RED;
        Label titleLabel = new Label(titleText, skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(titleColor);
        titleLabel.setAlignment(Align.center);
        root.add(titleLabel).padBottom(20).row();

        // Cat image + name
        root.add(new Image(catTexture)).size(160, 160).padBottom(10).row();
        Label nameLabel = new Label(data.playerName, skin);
        nameLabel.setFontScale(1.4f);
        nameLabel.setAlignment(Align.center);
        root.add(nameLabel).padBottom(30).row();

        // Stats table
        Table stats = new Table();
        stats.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.35f)));
        stats.pad(30);
        addStatRow(stats, "SCORE",   String.valueOf(data.score));
        addStatRow(stats, "KILLS",   String.valueOf(data.kills));
        addStatRow(stats, "KIBBLES", String.valueOf(data.kibbleCount));
        addStatRow(stats, "TIME",    formatTime(data.survivalSeconds));
        root.add(stats).width(500).padBottom(40).row();

        // Back button
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = skin.getFont("Boogaloo-Regular");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up   = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        btnStyle.down = skin.newDrawable("white", Color.valueOf("1480b0"));
        TextButton backBtn = new TextButton("BACK TO MENU", btnStyle);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm, firebase));
            }
        });
        root.add(backBtn).width(400).height(80);

        stage.addActor(root);
    }

    private void addStatRow(Table t, String label, String value) {
        Label lbl = new Label(label, skin);
        lbl.setFontScale(1.1f);
        lbl.setColor(Color.LIGHT_GRAY);
        Label val = new Label(value, skin);
        val.setFontScale(1.3f);
        val.setColor(Color.WHITE);
        t.add(lbl).width(200).left().padBottom(12);
        t.add(val).width(200).right().padBottom(12).row();
    }

    private String formatTime(float seconds) {
        int m = (int)(seconds / 60);
        int s = (int)(seconds % 60);
        return m + "m " + s + "s";
    }

    @Override public void update(float dt) { stage.act(dt); }

    @Override public void render(SpriteBatch sb) {
        float r = data.isWinner ? 0.8f : 0.15f;
        float g = data.isWinner ? 0.93f : 0.05f;
        float b = data.isWinner ? 1f    : 0.05f;
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
        catTexture.dispose();
    }
}
