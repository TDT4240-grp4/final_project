package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;

public class PlayView {
    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;

    public PlayView(Engine engine) {
        this.engine = engine;
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(SpriteBatch batch) {
        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : engine.getEntitiesFor(Family.all(CharacterComponent.class, SizeComponent.class).get())) {
            CharacterComponent character = CharacterComponent.MAPPER.get(entity);
            SizeComponent size = SizeComponent.MAPPER.get(entity);
            shapeRenderer.circle(character.x, character.y, size.width / 2);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (Entity entity: engine.getEntitiesFor(Family.all(TextureComponent.class, PhysicsComponent.class, SizeComponent.class).get())) {
            TextureComponent texture = TextureComponent.MAPPER.get(entity);
            PhysicsComponent phys = PhysicsComponent.MAPPER.get(entity);
            SizeComponent size = SizeComponent.MAPPER.get(entity);

            batch.draw(texture.texture,
                phys.body.getPosition().x - size.width / 2,
                phys.body.getPosition().y - size.height / 2,
                size.width, size.height);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
