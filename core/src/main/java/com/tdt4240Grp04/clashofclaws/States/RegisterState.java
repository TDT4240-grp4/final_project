package com.tdt4240Grp04.clashofclaws.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tdt4240Grp04.clashofclaws.FirebaseSDK;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RegisterState extends State {
    private Stage stage;
    private FirebaseSDK firebase;
    private Skin skin;
    private TextureAtlas atlas;

    public RegisterState(StateManager gsm, FirebaseSDK firebase) {
        super(gsm);
        this.firebase = firebase;
        this.stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("Register New Account", skin);
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

        TextButton registerBtn = new TextButton("Register", skin);
        TextButton backBtn = new TextButton("Back to Login", skin);

        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();
                Gdx.app.log("Register", "Attempting registration for: " + email);
                firebase.register(email, password, new FirebaseSDK.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.postRunnable(() -> {
                            feedbackLabel.setText("Registration successful!");
                            gsm.set(new LoginState(gsm, firebase));
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Gdx.app.postRunnable(() -> feedbackLabel.setText("Error: " + message));
                    }
                });
            }
        });

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gsm.set(new LoginState(gsm, firebase));
                    }
                });
            }
        });

        table.add(titleLabel).colspan(2).padBottom(20).row();
        table.add(feedbackLabel).colspan(2).padBottom(10).row();

        table.add(emailLabel).padRight(10);
        table.add(emailField).width(300).padBottom(10).row();

        table.add(passwordLabel).padRight(10);
        table.add(passwordField).width(300).padBottom(20).row();

        table.add(registerBtn).colspan(2).width(200).height(50).padBottom(10).row();
        table.add(backBtn).colspan(2).width(200).height(50);

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
        Gdx.gl.glClearColor(0.2f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
