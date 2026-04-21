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

public class ResultsState extends State {
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture catTexture;
    private final ResultsData data;

    public ResultsState(StateManager gsm, ResultsData data) {
        super(gsm);
        this.data = data;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin  = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        catTexture = new Texture(Gdx.files.internal("cat" + (data.catIndex + 1) + ".png"));

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title
        String titleText = data.isWinner ? "WINNER!" : "YOU DIED";
        Color  titleColor = data.isWinner ? Color.valueOf("1ca1e4") : Color.RED;
        Label titleLabel = new Label(titleText, skin);
        titleLabel.setFontScale(H * 0.0025f);
        titleLabel.setColor(titleColor);
        titleLabel.setAlignment(Align.center);
        root.add(titleLabel).padBottom(H * 0.015f).row();

        // Cat image + name
        root.add(new Image(catTexture)).size(H * 0.15f, H * 0.15f).padBottom(H * 0.008f).row();
        Label nameLabel = new Label(data.playerName, skin);
        nameLabel.setFontScale(H * 0.0015f);
        nameLabel.setAlignment(Align.center);
        root.add(nameLabel).padBottom(H * 0.02f).row();

        // Stats table — header row + value row
        float colW = W * 0.1f;
        Table stats = new Table();
        stats.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.35f)));
        stats.pad(H * 0.09f);

        String[] headers = { "SCORE", "KILLS", "KIBBLES", "TIME" };
        String[] values  = {
            String.valueOf(data.score),
            String.valueOf(data.kills),
            String.valueOf(data.kibbleCount),
            formatTime(data.survivalSeconds)
        };

        for (String h : headers) {
            Label hLbl = new Label(h, skin);
            hLbl.setFontScale(H * 0.0009f);
            hLbl.setColor(Color.LIGHT_GRAY);
            hLbl.setAlignment(Align.center);
            stats.add(hLbl).width(colW).center().padRight(W * 0.06f);
        }
        stats.row().padTop(H * 0.04f);
        for (String v : values) {
            Label vLbl = new Label(v, skin);
            vLbl.setFontScale(H * 0.0011f);
            vLbl.setColor(Color.WHITE);
            vLbl.setAlignment(Align.center);
            stats.add(vLbl).width(colW).center().padRight(W * 0.06f);
        }

        root.add(stats).padBottom(H * 0.03f).row();

        // Back button
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = skin.getFont("Boogaloo-Regular");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up   = skin.newDrawable("white", Color.valueOf("1ca1e4"));
        btnStyle.down = skin.newDrawable("white", Color.valueOf("1480b0"));
        TextButton backBtn = new TextButton("BACK TO MENU", btnStyle);
        backBtn.getLabel().setFontScale(H * 0.0012f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gsm.set(new LoginState(gsm));
            }
        });
        root.add(backBtn).width(W * 0.25f).height(H * 0.075f);

        stage.addActor(root);
    }

    private String formatTime(float seconds) {
        int m = (int)(seconds / 60);
        int s = (int)(seconds % 60);
        return m + "m " + s + "s";
    }

    @Override public void update(float dt) { stage.act(dt); }

    @Override public void render(SpriteBatch sb) {
        float r = data.isWinner ? 0.8f  : 0.85f;
        float g = data.isWinner ? 0.93f : 0.15f;
        float b = data.isWinner ? 1f    : 0.15f;
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
