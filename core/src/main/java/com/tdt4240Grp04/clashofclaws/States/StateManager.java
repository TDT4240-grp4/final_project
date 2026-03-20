package com.tdt4240Grp04.clashofclaws.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

public class StateManager {
    private Stack<State> states;

    public StateManager() {
        this.states = new Stack<>();
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop() {
        if (!states.isEmpty()) states.pop().dispose();
    }

    public void set(State state) {
        if (!states.isEmpty()) states.pop().dispose();
        states.push(state);
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
