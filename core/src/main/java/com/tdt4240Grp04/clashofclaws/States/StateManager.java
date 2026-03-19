package com.tdt4240Grp04.clashofclaws.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

public class StateManager {
    private static StateManager instance;
    private Stack<State> states;

    private StateManager() {
        this.states = new Stack<>();
    }

    public static StateManager getInstance() {
        if (instance == null) instance = new StateManager();
        return instance;
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

    public void update(float dt) {
        if (!states.isEmpty()) states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        if (!states.isEmpty()) states.peek().render(sb);
    }
}
