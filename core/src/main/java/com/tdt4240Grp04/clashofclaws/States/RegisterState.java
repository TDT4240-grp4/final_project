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

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table container = new Table();

        container.setBackground(skin.getDrawable("white"));
        container.pad(50);


        Label titleLabel = new Label("REGISTER", skin, "title");
        final Label feedbackLabel = new Label("", skin);

        // Input Fields
        Label emailLabel = new Label("Email:", skin);
        final TextField emailField = new TextField("", skin);
        emailField.setMessageText("Enter email address");

        Label passwordLabel = new Label("Password:", skin);
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        Label confirmLabel = new Label("Confirm:", skin);
        final TextField confirmField = new TextField("", skin);
        confirmField.setPasswordMode(true);
        confirmField.setPasswordCharacter('*');

        TextButton registerBtn = new TextButton("CREATE ACCOUNT", skin);
        TextButton backBtn = new TextButton("BACK TO LOGIN", skin);

        container.add(titleLabel).colspan(2).padBottom(30).row();
        container.add(feedbackLabel).colspan(2).padBottom(10).row();

        container.add(emailLabel).left().padRight(10);
        container.add(emailField).width(350).padBottom(15).row();

        container.add(passwordLabel).left().padRight(10);
        container.add(passwordField).width(350).padBottom(15).row();

        container.add(confirmLabel).left().padRight(10);
        container.add(confirmField).width(350).padBottom(30).row();

        container.add(registerBtn).colspan(2).width(300).height(60).padBottom(10).row();
        container.add(backBtn).colspan(2).width(300).height(50);

        mainTable.add(container).center();

        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String confirm = confirmField.getText();

                if (!password.equals(confirm)) {
                    feedbackLabel.setText("Passwords do not match!");
                    return;
                }

                if (email.isEmpty() || password.isEmpty()) {
                    feedbackLabel.setText("Please fill all fields");
                    return;
                }

                firebase.register(email, password, new FirebaseSDK.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.postRunnable(() -> gsm.set(new LoginState(gsm, firebase)));
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
                gsm.set(new LoginState(gsm, firebase));
            }
        });
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
        atlas.dispose();
    }
}
