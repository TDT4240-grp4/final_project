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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tdt4240Grp04.clashofclaws.ecs.components.CatBodyComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.OpponentComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PhysicsComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PlayerComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.SizeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private Label staminaLabel;
    private Table leaderboardTable;
    private Label coordsLabel;
    private HashMap<Entity, Label> nameLabels;
    private boolean quitRequested = false;
    public static boolean showCoordinates = true;

    private static final float MAP_WIDTH = 200f;
    private static final float MAP_HEIGHT = 200f;

    public PlayView(Engine engine, Entity player) {
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
        table.add(scoreLabel).padLeft(20).padTop(20).row();

        staminaLabel = new Label("Stamina: 100%", skin);
        staminaLabel.setFontScale(0.8f);
        table.add(staminaLabel).padLeft(20).padTop(5);

        stage.addActor(table);

        // Leaderboard — top right
        leaderboardTable = new Table();
        leaderboardTable.top().right();
        leaderboardTable.setFillParent(true);
        stage.addActor(leaderboardTable);

        nameLabels = new HashMap<>();

        Table coordsTable = new Table();
        coordsTable.bottom().right();
        coordsTable.setFillParent(true);
        coordsLabel = new Label("", skin);
        coordsLabel.setFontScale(0.7f);
        coordsLabel.setColor(Color.WHITE);
        coordsTable.add(coordsLabel).padBottom(20).padRight(20);
        stage.addActor(coordsTable);

        TextButton quitBtn = new TextButton("QUIT", skin);
        Table quitTable = new Table();
        quitTable.bottom().left();
        quitTable.setFillParent(true);
        quitTable.add(quitBtn).width(160).height(60).padLeft(20).padBottom(20);
        quitBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                quitRequested = true;
            }
        });
        stage.addActor(quitTable);
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public boolean isQuitRequested() {
        return quitRequested;
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

        // cat body
        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : engine.getEntitiesFor(Family.all(PhysicsComponent.class, CatBodyComponent.class).get())) {
            CatBodyComponent catBody = CatBodyComponent.MAPPER.get(entity);
            // Draw tail-to-head so head visually overlaps tail
            for (int si = catBody.bodyParts.size - 1; si >= 0; si--) {
                Vector2 segment = catBody.bodyParts.get(si);
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.circle(segment.x, segment.y, catBody.segmentRadius + 0.03f, 30);
                shapeRenderer.setColor(catBody.color);
                shapeRenderer.circle(segment.x, segment.y, catBody.segmentRadius, 30);
            }
        }
        shapeRenderer.end();


        // draw everything else, including the cat head (TextureComponent)
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

        // draw UI
        PlayerComponent pc = player.getComponent(PlayerComponent.class);
        if (pc != null) {
            scoreLabel.setText("Score: " + pc.score);
        }

        // Update coordinates label
        PhysicsComponent playerPhysCoords = PhysicsComponent.MAPPER.get(player);
        if (showCoordinates && playerPhysCoords != null) {
            float cx = playerPhysCoords.body.getPosition().x;
            float cy = playerPhysCoords.body.getPosition().y;
            coordsLabel.setText(String.format("X: %.1f  Y: %.1f", cx, cy));
            coordsLabel.setVisible(true);
        } else {
            coordsLabel.setVisible(false);
        }

        // Update stamina label
        StaminaComponent stamina = StaminaComponent.MAPPER.get(player);
        if (stamina != null) {
            int pct = (int)((stamina.currentStamina / stamina.maxStamina) * 100);
            staminaLabel.setText("Stamina: " + pct + "%");
            staminaLabel.setColor(pct > 50 ? Color.GREEN : pct > 20 ? Color.YELLOW : Color.RED);
        }

        // Update leaderboard
        leaderboardTable.clear();
        leaderboardTable.add(new Label("-- Leaderboard --", skin)).padTop(20).padRight(20).row();

        // Collect all player/opponent (name, score) pairs
        List<String[]> entries = new ArrayList<>();
        PlayerComponent myComp = player.getComponent(PlayerComponent.class);
        if (myComp != null) {
            entries.add(new String[]{myComp.name + " (you)", String.valueOf(myComp.score), "me"});
        }
        for (Entity e : engine.getEntitiesFor(Family.all(OpponentComponent.class).get())) {
            OpponentComponent opp = e.getComponent(OpponentComponent.class);
            if (opp != null) {
                entries.add(new String[]{opp.name, String.valueOf(opp.score), "opp"});
            }
        }

        // Sort by score descending
        entries.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        // Add rows
        for (int i = 0; i < entries.size(); i++) {
            String[] entry = entries.get(i);
            Label row = new Label((i + 1) + ". " + entry[0] + "  " + entry[1], skin);
            row.setFontScale(0.8f);
            if ("me".equals(entry[2])) row.setColor(Color.CYAN);
            leaderboardTable.add(row).padRight(20).row();
        }

        for (Entity e : engine.getEntitiesFor(Family.one(PlayerComponent.class, OpponentComponent.class).get())) {
            PhysicsComponent physComp = e.getComponent(PhysicsComponent.class);
            SizeComponent sizeComp = e.getComponent(SizeComponent.class);

            if (physComp == null || sizeComp == null) continue;

            Label label = nameLabels.get(e);

            // If the label doesn't exist yet, create it
            if (label == null) {
                String name = "Unknown";
                PlayerComponent pComp = e.getComponent(PlayerComponent.class);
                OpponentComponent oComp = e.getComponent(OpponentComponent.class);
                if (pComp != null) name = pComp.name;
                if (oComp != null && oComp.name != null) name = oComp.name;

                label = new Label(name, skin);
                label.setColor(Color.WHITE);
                stage.addActor(label);
                nameLabels.put(e, label);
            }

            // Project physics coordinates to screen coordinates
            Vector3 worldPos = new com.badlogic.gdx.math.Vector3(
                physComp.body.getPosition().x,
                physComp.body.getPosition().y + (sizeComp.height / 2f),
                0
            );
            Vector3 screenPos = gameViewport.getCamera().project(worldPos);

            label.setPosition(
                screenPos.x - (label.getWidth() / 2f),
                screenPos.y + 10f
            );
        }

        // Cleanup labels for dead/disconnected cats
        nameLabels.entrySet().removeIf(entry -> {
            if (!engine.getEntities().contains(entry.getKey(), true)) {
                entry.getValue().remove(); // removes label from the stage
                return true;               // removes from the map
            }
            return false;
        });

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
