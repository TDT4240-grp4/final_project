package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    private Entity player;

    private static final float MAP_WIDTH = 2000f;
    private static final float MAP_HEIGHT = 2000f;

    public PlayView(Engine engine,  Entity player) {
        this.engine = engine;
        this.player = player;
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(SpriteBatch batch) {
        // centre camera on player
        PhysicsComponent playerPhys = PhysicsComponent.MAPPER.get(player);
        if (playerPhys != null && playerPhys.body != null) {
            float targetX = playerPhys.body.getPosition().x;
            float targetY = playerPhys.body.getPosition().y;

            // Figure out half the screen size so the camera doesn't show past the edges
            float halfViewWidth = viewport.getWorldWidth() / 2f;
            float halfViewHeight = viewport.getWorldHeight() / 2f;

            float clampedX;
            if (viewport.getWorldWidth() > MAP_WIDTH) {
                clampedX = MAP_WIDTH / 2f;
            } else {
                clampedX = MathUtils.clamp(targetX, halfViewWidth, MAP_WIDTH - halfViewWidth);
            }

            // Y-Axis: Center if screen is taller than map, otherwise clamp normally
            float clampedY;
            if (viewport.getWorldHeight() > MAP_HEIGHT) {
                clampedY = MAP_HEIGHT / 2f;
            } else {
                clampedY = MathUtils.clamp(targetY, halfViewHeight, MAP_HEIGHT - halfViewHeight);
            }
            // Move the camera
            viewport.getCamera().position.set(clampedX, clampedY, 0);
            viewport.getCamera().update();
        }

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
