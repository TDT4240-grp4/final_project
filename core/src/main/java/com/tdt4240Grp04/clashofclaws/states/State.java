package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * An abstract representation of a game screen
 */
public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse;
    protected StateManager gsm;

    protected State(StateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    //protected abstract void handleInput();
    public abstract void update(float dt);
    // dt is amount of time since the last frame was rendered
    public abstract void render(SpriteBatch sb);
    // SpriteBatch is a collection of Sprite
    public abstract void resize(int width, int height);
    public abstract void dispose();
}
