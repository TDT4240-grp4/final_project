package com.tdt4240Grp04.clashofclaws.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LoginState extends State {
    private Stage stage;
    private FirebaseSDK firebase;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture logoTexture1;
    private Texture logoTexture2;

    public LoginState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.firebase = firebase;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        logoTexture1 = new Texture(Gdx.files.internal("cat1.png"));
        Image logo1 = new Image(logoTexture1);
        logoTexture2 = new Texture(Gdx.files.internal("cat2.png"));
        Image logo2 = new Image(logoTexture2);


        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("CLASH OF CLAWS", skin, "title");
        titleLabel.setFontScale(2.5f);
        final Label feedbackLabel = new Label("", skin);

        Label emailLabel = new Label("Email:", skin);
        final TextField emailField = new TextField("", skin);
        emailField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });


        Label passwordLabel = new Label("Password:", skin);
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });

        Label taglineLabel = new Label("EAT. GROW. CONQUER.", skin, "title");
        taglineLabel.setFontScale(1.5f);

        TextButton loginBtn = new TextButton("Login", skin);
        TextButton registerBtn = new TextButton("Create Account", skin);

        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();
                Gdx.app.log("Login", "Attempting login for: " + email);
                firebase.login(email, password, new FirebaseSDK.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.postRunnable(() -> {
                            feedbackLabel.setText("Login successful!");
                            //gsm.set(new MainMenuState(gsm, firebase)); // Example of changing state
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Gdx.app.postRunnable(() -> feedbackLabel.setText("Error: " + message));
                    }
                });
            }
        });

        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gsm.set(new RegisterState(gsm, firebase));
                    }
                });
            }
        });

        table.clearChildren();

        Table loginForm = new Table();
        loginForm.add(feedbackLabel).colspan(2).padBottom(10).row();

        loginForm.add(emailLabel).padRight(20);
        loginForm.add(emailField).width(300).padBottom(10).row();

        loginForm.add(passwordLabel).padRight(20);
        loginForm.add(passwordField).width(300).padBottom(50).row();

        loginForm.add(loginBtn).colspan(2).width(200).height(50).padBottom(10).row();
        loginForm.add(registerBtn).colspan(2).width(200).height(50);

        Table brandingGroup = new Table();
        brandingGroup.add(logo1).size(500, 500).padBottom(20);
        brandingGroup.add(logo2).size(500, 500).padBottom(20).row();
        brandingGroup.add(titleLabel).colspan(2).padTop(10).center().row();
        brandingGroup.add(taglineLabel).colspan(2).padTop(30).center();

        table.add(brandingGroup).expand().left().padLeft(100);

        table.add(loginForm).expand().left().padLeft(20);

        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0.804f, 0.933f, 0.996f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (atlas != null) atlas.dispose();
        if (logoTexture1 != null) logoTexture1.dispose();
     }
}
