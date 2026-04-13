package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;

public class PlayView {
    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;

    public PlayView(Engine engine) {
        this.engine = engine;
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render() {
        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : engine.getEntitiesFor(Family.all(CharacterComponent.class, SizeComponent.class).get())) {
            CharacterComponent character = CharacterComponent.MAPPER.get(entity);
            SizeComponent size = SizeComponent.MAPPER.get(entity);
            shapeRenderer.circle(character.x, character.y, size.width / 2);
        }

        shapeRenderer.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
