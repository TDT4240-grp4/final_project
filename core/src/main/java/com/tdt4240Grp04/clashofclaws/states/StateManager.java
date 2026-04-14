package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

public class StateManager {
    private Stack<State> states;

    public StateManager() {
        this.states = new Stack<>();
    }

    public void push(State state) {
        states.push(state);
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void pop() {
        if (!states.isEmpty()) states.pop().dispose();
    }

    public void set(State state) {
        if (!states.isEmpty()) states.pop().dispose();
        states.push(state);
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        if (!states.isEmpty()) states.peek().resize(width, height);
    }

    public void update(float dt) {
        if (!states.isEmpty()) states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        if (!states.isEmpty()) states.peek().render(sb);
    }

    public void dispose() {
        while (!states.isEmpty()) {
            states.pop().dispose();
        }
    }
}
