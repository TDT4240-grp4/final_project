package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.CharacterComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;

public class PlayView {
    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Entity player;

    private static final float MAP_WIDTH = 200f;
    private static final float MAP_HEIGHT = 200f;

    public PlayView(Engine engine,  Entity player) {
        this.engine = engine;
        this.player = player;
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FitViewport(25f, 25f * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()));    }

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

        // 1. Draw cat body
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : engine.getEntitiesFor(Family.all(PlayerComponent.class, CatBodyComponent.class).get())) {
            CatBodyComponent catBody = CatBodyComponent.MAPPER.get(entity);
            // 1. Draw slightly larger black circles first for the outline
            shapeRenderer.setColor(Color.BLACK);
            for (Vector2 segment : catBody.bodyParts) {
                shapeRenderer.circle(segment.x, segment.y, catBody.segmentRadius + 0.03f, 30);
            }

            // 2. Draw the inner color circles on top
            shapeRenderer.setColor(catBody.color);
            for (Vector2 segment : catBody.bodyParts) {
                shapeRenderer.circle(segment.x, segment.y, catBody.segmentRadius, 30);
            }
        }
        shapeRenderer.end();


        // 2. Draw everything else, including the cat head (TextureComponent)
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (Entity entity: engine.getEntitiesFor(Family.all(TextureComponent.class, PhysicsComponent.class, SizeComponent.class).get())) {
            TextureComponent texture = TextureComponent.MAPPER.get(entity);
            PhysicsComponent phys = PhysicsComponent.MAPPER.get(entity);
            SizeComponent size = SizeComponent.MAPPER.get(entity);

            // Draw the texture centered
            batch.draw(texture.texture,
                phys.body.getPosition().x - size.width / 2,
                phys.body.getPosition().y - size.height / 2,
                size.width / 2, size.height / 2, // origin for rotation
                size.width, size.height,
                1, 1, // scale
                phys.body.getAngle() * MathUtils.radiansToDegrees, // rotation
                0, 0, // srcX, srcY
                texture.texture.getWidth(), texture.texture.getHeight(), // srcWidth, srcHeight
                false, false); // flipX, flipY
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
