package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;

public class PlayView {
    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private Viewport gameViewport;
    private Viewport uiViewport;

    private Entity player;
    private Skin skin;
    private TextureAtlas atlas;

    private Stage stage;
    private Label scoreLabel;
    private Label coordinatesLabel;
    private Label nameLabel;

    private static final float MAP_WIDTH = 200f;
    private static final float MAP_HEIGHT = 200f;

    public PlayView(Engine engine,  Entity player) {
        this.engine = engine;
        this.player = player;
        this.shapeRenderer = new ShapeRenderer();
        this.gameViewport = new FitViewport(25f, 25f * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()));
        this.uiViewport = new ScreenViewport();
        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        stage = new Stage(uiViewport);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setFontScale(1f);
        table.add(scoreLabel).padLeft(20).padTop(20);

        stage.addActor(table);

        Table coordinatesTable = new Table();
        coordinatesTable.bottom().right();
        coordinatesTable.setFillParent(true);

        coordinatesLabel = new Label("X: 0 Y: 0", skin);
        coordinatesTable.add(coordinatesLabel).padRight(20).padBottom(20);
        stage.addActor(coordinatesTable);

        PlayerComponent pComp = player.getComponent(PlayerComponent.class);
        String playerName = pComp.name;
        nameLabel = new Label(playerName, skin);
        nameLabel.setColor(Color.WHITE);
        stage.addActor(nameLabel);
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public void render(SpriteBatch batch) {
        // centre camera on player
        PhysicsComponent playerPhys = PhysicsComponent.MAPPER.get(player);
        if (playerPhys != null && playerPhys.body != null) {
            float targetX = playerPhys.body.getPosition().x;
            float targetY = playerPhys.body.getPosition().y;

            // Figure out half the screen size so the camera doesn't show past the edges
            float halfViewWidth = gameViewport.getWorldWidth() / 2f;
            float halfViewHeight = gameViewport.getWorldHeight() / 2f;

            float clampedX;
            if (gameViewport.getWorldWidth() > MAP_WIDTH) {
                clampedX = MAP_WIDTH / 2f;
            } else {
                clampedX = MathUtils.clamp(targetX, halfViewWidth, MAP_WIDTH - halfViewWidth);
            }

            // Y-Axis: Center if screen is taller than map, otherwise clamp normally
            float clampedY;
            if (gameViewport.getWorldHeight() > MAP_HEIGHT) {
                clampedY = MAP_HEIGHT / 2f;
            } else {
                clampedY = MathUtils.clamp(targetY, halfViewHeight, MAP_HEIGHT - halfViewHeight);
            }
            // Move the camera
            gameViewport.getCamera().position.set(clampedX, clampedY, 0);
            gameViewport.getCamera().update();
        }

        gameViewport.apply();

        // 1. Draw cat body
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : engine.getEntitiesFor(Family.all(PhysicsComponent.class, CatBodyComponent.class).get())) {
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
        batch.setProjectionMatrix(gameViewport.getCamera().combined);
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

        // 3. Draw UI
        PlayerComponent pc = player.getComponent(PlayerComponent.class);
        if (pc != null) {
            scoreLabel.setText("Score: " + pc.score);
        }
        PhysicsComponent playerPhysForLabel = PhysicsComponent.MAPPER.get(player);
        if (playerPhysForLabel != null && playerPhysForLabel.body != null) {
            coordinatesLabel.setText(String.format("X: %.2f Y: %.2f", playerPhysForLabel.body.getPosition().x, playerPhysForLabel.body.getPosition().y));
        }

        PhysicsComponent physComp = player.getComponent(PhysicsComponent.class);
        SizeComponent sizeComp = player.getComponent(SizeComponent.class);

        if (physComp != null && sizeComp != null) {
            float playerX = physComp.body.getPosition().x;
            float playerY = physComp.body.getPosition().y;

            Vector3 worldPos = new com.badlogic.gdx.math.Vector3(
                playerX,
                playerY + (sizeComp.height / 2f),
                0
            );

            Vector3 screenPos = gameViewport.getCamera().project(worldPos);

            nameLabel.setPosition(
                screenPos.x - (nameLabel.getWidth() / 2f),
                screenPos.y + 10f
            );
        }
        stage.getViewport().apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
