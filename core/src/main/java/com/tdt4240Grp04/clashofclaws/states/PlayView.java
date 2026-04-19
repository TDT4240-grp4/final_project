package com.tdt4240Grp04.clashofclaws.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.tdt4240Grp04.clashofclaws.ecs.components.CatTypeComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.PowerupComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.StaminaComponent;
import com.tdt4240Grp04.clashofclaws.ecs.components.TextureComponent;
import com.tdt4240Grp04.clashofclaws.network.Network;

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
    private PlayLogic playLogic;
    private Texture speedTexture;
    private Texture shieldTexture;
    private Texture magnetTexture;

    private Label scoreLabel;
    private Label staminaLabel;
    private Label powerupLabel;
    private Label timerLabel;
    private Table leaderboardTable;
    private Label coordsLabel;
    private HashMap<Entity, Label> nameLabels;
    private boolean quitRequested = false;
    public static boolean showCoordinates = false;

    private static final float MAP_WIDTH = 200f;
    private static final float MAP_HEIGHT = 200f;

    public PlayView(Engine engine, Entity player, PlayLogic playLogic) {
        this.engine = engine;
        this.player = player;
        this.playLogic = playLogic;
        this.shapeRenderer = new ShapeRenderer();
        speedTexture  = new Texture(Gdx.files.internal("red_fish.png"));
        shieldTexture = new Texture(Gdx.files.internal("fish.png"));
        magnetTexture = new Texture(Gdx.files.internal("grey_fish.png"));
        this.gameViewport = new FitViewport(25f, 25f * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()));
        this.uiViewport = new ScreenViewport();
        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        stage = new Stage(uiViewport);

        float W = Gdx.graphics.getWidth();
        float H = Gdx.graphics.getHeight();
        float uiScale = W / 1280f; // scale relative to reference 1280px width

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setFontScale(0.55f * uiScale);
        table.add(scoreLabel).padLeft(W * 0.015f).padTop(H * 0.025f).row();

        staminaLabel = new Label("Stamina: 100%", skin);
        staminaLabel.setFontScale(0.45f * uiScale);
        table.add(staminaLabel).padLeft(W * 0.015f).padTop(H * 0.008f).row();

        powerupLabel = new Label("", skin);
        powerupLabel.setFontScale(0.45f * uiScale);
        powerupLabel.setColor(Color.YELLOW);
        table.add(powerupLabel).padLeft(W * 0.015f).padTop(H * 0.008f);

        stage.addActor(table);

        // Timer — top center
        timerLabel = new Label("3:00", skin);
        timerLabel.setFontScale(0.6f * uiScale);
        timerLabel.setColor(Color.WHITE);
        Table timerTable = new Table();
        timerTable.top().center();
        timerTable.setFillParent(true);
        timerTable.add(timerLabel).padTop(H * 0.025f);
        stage.addActor(timerTable);

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
        coordsLabel.setFontScale(0.4f * uiScale);
        coordsLabel.setColor(Color.WHITE);
        coordsTable.add(coordsLabel).padBottom(H * 0.025f).padRight(W * 0.015f);
        stage.addActor(coordsTable);

        // Small subtle quit — top right, below leaderboard won't work, use top-left corner
        TextButton quitBtn = new TextButton("quit", skin);
        quitBtn.getLabel().setFontScale(0.3f * uiScale);
        quitBtn.setColor(new Color(0.5f, 0.5f, 0.5f, 0.6f));
        Table quitTable = new Table();
        quitTable.top().left();
        quitTable.setFillParent(true);
        quitTable.add(quitBtn).width(W * 0.07f).height(H * 0.05f).padLeft(W * 0.015f).padTop(H * 0.28f);
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
            PhysicsComponent phys = PhysicsComponent.MAPPER.get(entity);
            if (catBody.bodyParts.size < 1) continue;

            float width = catBody.segmentRadius * 2f;
            float outlineWidth = width + 0.06f;

            // Draw outline pass (black), then fill pass (color) for clean border
            Vector2 head = phys.body.getPosition();
            Vector2 prev = catBody.bodyParts.get(0);
            Vector2 tail = catBody.bodyParts.get(catBody.bodyParts.size - 1);
            int capSegs = 10;

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rectLine(head.x, head.y, prev.x, prev.y, outlineWidth);
            for (int si = 0; si < catBody.bodyParts.size - 1; si++) {
                Vector2 a = catBody.bodyParts.get(si);
                Vector2 b = catBody.bodyParts.get(si + 1);
                shapeRenderer.rectLine(a.x, a.y, b.x, b.y, outlineWidth);
            }
            shapeRenderer.circle(tail.x, tail.y, outlineWidth / 2f, capSegs);

            shapeRenderer.setColor(catBody.color);
            shapeRenderer.rectLine(head.x, head.y, prev.x, prev.y, width);
            for (int si = 0; si < catBody.bodyParts.size - 1; si++) {
                Vector2 a = catBody.bodyParts.get(si);
                Vector2 b = catBody.bodyParts.get(si + 1);
                shapeRenderer.rectLine(a.x, a.y, b.x, b.y, width);
            }
            shapeRenderer.circle(tail.x, tail.y, width / 2f, capSegs);
        }
        shapeRenderer.end();

        // Draw shield barrier around any cat with shieldActive
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.valueOf("1a3cff"));
        for (Entity entity : engine.getEntitiesFor(Family.all(PhysicsComponent.class, CatTypeComponent.class).get())) {
            CatTypeComponent ct = CatTypeComponent.MAPPER.get(entity);
            if (!ct.shieldActive) continue;
            // Cross-check: for entities with a PowerupComponent, the active type must be SHIELD
            PowerupComponent ppCheck = PowerupComponent.MAPPER.get(entity);
            if (ppCheck != null && ppCheck.activeType != PowerupComponent.SHIELD) continue;
            PhysicsComponent phys = PhysicsComponent.MAPPER.get(entity);
            float cx = phys.body.getPosition().x;
            float cy = phys.body.getPosition().y;
            shapeRenderer.circle(cx, cy, 0.9f, 36);
            shapeRenderer.circle(cx, cy, 1.0f, 36);
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
        // Draw powerup pickups as fish sprites
        if (playLogic != null) {
            for (Network.PowerupSpawned sp : playLogic.getActivePowerups()) {
                Texture tex;
                switch (sp.type) {
                    case 1:  tex = speedTexture;  break;
                    case 2:  tex = shieldTexture; break;
                    case 3:  tex = magnetTexture; break;
                    default: continue;
                }
                float TARGET = 1.2f;
                float tw = tex.getWidth(), th = tex.getHeight();
                float w = (tw >= th) ? TARGET : TARGET * tw / th;
                float h = (th >  tw) ? TARGET : TARGET * th / tw;
                batch.draw(tex, sp.x - w / 2, sp.y - h / 2, w, h);
            }
        }
        batch.end();

        // draw UI
        PlayerComponent pc = player.getComponent(PlayerComponent.class);
        if (pc != null) {
            scoreLabel.setText("Score: " + pc.score);
        }

        // Update countdown timer
        float remaining = playLogic.getRemainingTime();
        int mins = (int)(remaining / 60);
        int secs = (int)(remaining % 60);
        timerLabel.setText(String.format("%d:%02d", mins, secs));
        timerLabel.setColor(remaining <= 30f ? Color.RED : Color.WHITE);

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

        // Update active powerup label
        PowerupComponent pp = PowerupComponent.MAPPER.get(player);
        if (pp != null && pp.activeType != PowerupComponent.NONE) {
            String[] names = {"", "SPEED BOOST", "SHIELD", "KIBBLE MAGNET"};
            powerupLabel.setText(names[pp.activeType] + " " + (int)pp.remainingSeconds + "s");
        } else {
            powerupLabel.setText("");
        }

        // Update leaderboard
        float W = Gdx.graphics.getWidth();
        float uiScale = W / 1280f;
        leaderboardTable.clear();
        Label lbTitle = new Label("-- Leaderboard --", skin);
        lbTitle.setFontScale(0.45f * uiScale);
        leaderboardTable.add(lbTitle).padTop(20).padRight(20).row();

        // Collect all player/opponent (name, score, color) tuples
        List<Object[]> entries = new ArrayList<>();
        PlayerComponent myComp = player.getComponent(PlayerComponent.class);
        if (myComp != null) {
            entries.add(new Object[]{myComp.name + " (you)", myComp.score, bodyColor(player)});
        }
        for (Entity e : engine.getEntitiesFor(Family.all(OpponentComponent.class).get())) {
            OpponentComponent opp = e.getComponent(OpponentComponent.class);
            if (opp != null) {
                entries.add(new Object[]{opp.name, opp.score, bodyColor(e)});
            }
        }

        // Sort by score descending
        entries.sort((a, b) -> (Integer)b[1] - (Integer)a[1]);

        // Add rows — label color matches the player's body color
        for (int i = 0; i < entries.size(); i++) {
            Object[] entry = entries.get(i);
            Label row = new Label((i + 1) + ". " + entry[0] + "  " + entry[1], skin);
            row.setFontScale(0.4f * uiScale);
            row.setColor((Color) entry[2]);
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
                label.setFontScale(0.4f * uiScale);
                label.setColor(bodyColor(e));
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

    private Color bodyColor(Entity e) {
        CatBodyComponent body = CatBodyComponent.MAPPER.get(e);
        return (body != null && body.color != null) ? body.color : Color.WHITE;
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
        speedTexture.dispose();
        shieldTexture.dispose();
        magnetTexture.dispose();
    }
}
