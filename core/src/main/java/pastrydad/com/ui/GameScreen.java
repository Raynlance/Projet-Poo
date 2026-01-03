package pastrydad.com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pastrydad.com.GameMain;
import pastrydad.com.map.GameMap;
import pastrydad.com.map.MapLoader;
import pastrydad.com.map.MapRenderer;
import pastrydad.com.input.CameraController;
import pastrydad.com.combat.UnitManager;
import pastrydad.com.combat.MovementSystem;
import pastrydad.com.input.GameInputProcessor;
import pastrydad.com.entities.Unit;
import java.util.List;

public class GameScreen implements Screen {
    private GameMain game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont hudFont;
    private ShapeRenderer shapeRenderer;
    
    private OrthographicCamera hudCamera;
    private Viewport viewport;
    
    // system tae the map
    private GameMap gameMap;
    private MapRenderer mapRenderer;
    private OrthographicCamera mapCamera;
    private CameraController cameraController;
    
    // Combat systems
    private UnitManager unitManager;
    private MovementSystem movementSystem;
    private GameInputProcessor gameInputProcessor;
    
    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 600f;
    
    // HUD 
    private int playerHealth = 100;
    private int maxHealth = 100;
    private int score = 0;
    private int flour = 50;      
    private int sugar = 30;      
    private int money = 100;     
    
    //icônes HUD
    private Texture heartIcon;
    private Texture starIcon;
    private Texture wheatIcon;
    private Texture sugarIcon;
    private Texture coinIcon;
    private boolean useIcons = false;
    
    // Musique
    private Music backgroundMusic;
    
   
    private Color healthColor = new Color(1f, 0.3f, 0.5f, 1f);
    private Color healthBgColor = new Color(1f, 0.8f, 0.9f, 1f);
    private Color hudBgColor = new Color(1f, 0.85f, 0.95f, 0.95f);
    private Color hudBorderColor = new Color(1f, 0.6f, 0.8f, 1f);
    
    public GameScreen(GameMain game) {
        this.game = game;
        
        System.out.println("🎮 GameScreen - Initialisation...");
        
        // HUD Camera 
        hudCamera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudCamera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        hudCamera.update();
        
        //  LOAD MAP 
        System.out.println("🗺️ Loading map...");
        gameMap = MapLoader.loadMap("game_map.tmx");
        
        if (gameMap != null) {
            mapRenderer = new MapRenderer(gameMap);
            
            
            mapCamera = new OrthographicCamera();
            mapCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            
            // Initialize camera at map center
            float centerX = gameMap.getMapWidth() * gameMap.getTileWidth() / 2f;
            float centerY = gameMap.getMapHeight() * gameMap.getTileHeight() / 2f;
            mapCamera.position.set(centerX, centerY, 0);
            mapCamera.update();
            
            // CREATE CAMERA CONTROLLER 
            cameraController = new CameraController(mapCamera, gameMap);
            
            // Initialize combat systems
            unitManager = new UnitManager(gameMap);
            movementSystem = new MovementSystem(gameMap, unitManager);
            gameInputProcessor = new GameInputProcessor( mapCamera, gameMap, unitManager, movementSystem, cameraController);
            
            // Setup input multiplexer to handle both camera and game input
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(gameInputProcessor);  // Game input first
            multiplexer.addProcessor(cameraController);    // Camera second
            Gdx.input.setInputProcessor(multiplexer);
            
            // Create some test units
            createTestUnits();
            
            System.out.println("✅ Map loaded successfully!");
            System.out.println("   Map size: " + gameMap.getMapWidth() + "x" + gameMap.getMapHeight() + " tiles");
            System.out.println("   Tile size: " + gameMap.getTileWidth() + "x" + gameMap.getTileHeight() + " pixels");
        } else {
            System.err.println("❌ Failed to load map!");
        }
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.6f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        
        hudFont = new BitmapFont();
        hudFont.getData().setScale(1.8f);
        
        loadIcons();
        loadMusic();
        
        System.out.println("✅ GameScreen - Initialisé avec succès !");
        System.out.println("   Controls:");
        System.out.println("   - WASD / Arrow Keys: Pan camera");
        System.out.println("   - Mouse Wheel: Zoom in/out");
        System.out.println("   - Right Mouse / Middle Mouse: Drag to pan");
        System.out.println("   - Left Click: Select/Move/Attack units");
        System.out.println("   - T/Enter: End turn");
        System.out.println("   - Space/ESC: Deselect unit or return to menu");
    }
    
    private void createTestUnits() {
        System.out.println("🦒 Creating test units...");
        
        // Create player units (blue/friendly)
        unitManager.createWhiskGiraffe(5, 5, true);
        unitManager.createPanGiraffe(7, 5, true);
        unitManager.createRollingPinGiraffe(6, 6, true);
        
        // Create enemy units (red)
        unitManager.createWhiskGiraffe(15, 10, false);
        unitManager.createPanGiraffe(17, 11, false);
        
        System.out.println("✅ Created " + unitManager.getPlayerUnitCount() + " player units");
        System.out.println("✅ Created " + unitManager.getEnemyUnitCount() + " enemy units");
    }
    
    private void loadIcons() {
        try {
            heartIcon = new Texture(Gdx.files.internal("ui/icons/heart.png"));
            starIcon = new Texture(Gdx.files.internal("ui/icons/star.png"));
            wheatIcon = new Texture(Gdx.files.internal("ui/icons/wheat.png"));
            sugarIcon = new Texture(Gdx.files.internal("ui/icons/sugar.png"));
            coinIcon = new Texture(Gdx.files.internal("ui/icons/coin.png"));
            useIcons = true;
            System.out.println("✅ Icônes HUD chargées !");
        } catch (Exception e) {
            System.out.println("⚠️ Icônes non trouvées, utilisation de symboles texte");
            useIcons = false;
        }
    }
    
    private void loadMusic() {
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/game.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(1.0f);
            backgroundMusic.play();
            System.out.println("🎵 Musique de jeu lancée !");
        } catch (Exception e) {
            System.out.println("⚠️ Musique non trouvée : " + e.getMessage());
        }
    }
    
    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        if (gameMap != null && mapRenderer != null && cameraController != null) {
            
            cameraController.update(delta);
            
            // Render the map
            batch.setProjectionMatrix(mapCamera.combined);
            mapRenderer.render(batch, mapCamera);
            
            // Render visual feedback (movement ranges)
            renderMovementRanges();
            
            // Render units
            batch.begin();
            unitManager.renderAll(batch);
            batch.end();
            
            // Render selection highlight
            renderSelectionHighlight();
        }
        
        //  RENDER HUD ON TOP
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        
        drawHUD();
    }
    
    private void update(float delta) {
        // === ICI VOTRE ÉQUIPE AJOUTERA LA LOGIQUE DU JEU ===
        // Exemple :
        // combatSystem.update(delta);
        // entityManager.update(delta);
        // buildingManager.update(delta);
    }
    
    private void handleInput() {
        // ESC now handled by GameInputProcessor to deselect units
        // Only return to menu if no unit is selected
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gameInputProcessor != null && gameInputProcessor.getSelectedUnit() == null) {
                System.out.println("🔙 Retour au menu...");
                game.setScreen(new MenuScreen(game));
            }
        }
        
        // === TOUCHES DE TEST ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            playerHealth = Math.max(0, playerHealth - 10);
            System.out.println("❤️ Vie : " + playerHealth);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            playerHealth = Math.min(maxHealth, playerHealth + 10);
            System.out.println("💚 Vie : " + playerHealth);
        }
        
        // Reset camera (R key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && cameraController != null) {
            cameraController.resetCamera();
            System.out.println("📷 Camera reset to center");
        }
    }
    
    private void renderMovementRanges() {
        if (gameInputProcessor == null) {
            return;
        }
        
        shapeRenderer.setProjectionMatrix(mapCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw reachable tiles (green)
        List<int[]> reachable = gameInputProcessor.getReachableTiles();
        if (reachable != null) {
            shapeRenderer.setColor(0.2f, 1f, 0.2f, 0.3f); // Green transparent
            for (int[] tile : reachable) {
                float x = tile[0] * gameMap.getTileWidth();
                float y = tile[1] * gameMap.getTileHeight();
                shapeRenderer.rect(x, y, gameMap.getTileWidth(), gameMap.getTileHeight());
            }
        }
        
        // Draw attackable tiles (red)
        List<int[]> attackable = gameInputProcessor.getAttackableTiles();
        if (attackable != null) {
            shapeRenderer.setColor(1f, 0.2f, 0.2f, 0.3f); // Red transparent
            for (int[] tile : attackable) {
                float x = tile[0] * gameMap.getTileWidth();
                float y = tile[1] * gameMap.getTileHeight();
                shapeRenderer.rect(x, y, gameMap.getTileWidth(), gameMap.getTileHeight());
            }
        }
        
        shapeRenderer.end();
    }
    
    private void renderSelectionHighlight() {
        if (gameInputProcessor == null) {
            return;
        }
        
        Unit selected = gameInputProcessor.getSelectedUnit();
        if (selected != null && selected.isAlive()) {
            shapeRenderer.setProjectionMatrix(mapCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            
            // Yellow border for selected unit
            shapeRenderer.setColor(1f, 1f, 0f, 1f);
            float x = selected.getTileX() * gameMap.getTileWidth();
            float y = selected.getTileY() * gameMap.getTileHeight();
            float width = gameMap.getTileWidth();
            float height = gameMap.getTileHeight();
            
            // Draw thick border
            for (int i = 0; i < 3; i++) {
                shapeRenderer.rect(x - i, y - i, width + i * 2, height + i * 2);
            }
            
            shapeRenderer.end();
        }
    }
    
    private void drawHUD() {
        float hudHeight = 80f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Border
        shapeRenderer.setColor(hudBorderColor);
        shapeRenderer.rect(0, VIRTUAL_HEIGHT - hudHeight, VIRTUAL_WIDTH, hudHeight);
        
        // Background
        shapeRenderer.setColor(hudBgColor);
        shapeRenderer.rect(4, VIRTUAL_HEIGHT - (hudHeight - 4), VIRTUAL_WIDTH - 8, hudHeight - 8);
        
        shapeRenderer.end();
        
        float healthBarX = 90;
        float healthBarY = VIRTUAL_HEIGHT - 50;
        float healthBarWidth = 180;
        float healthBarHeight = 20;
        
        batch.begin();
        
        // Heart icon
        if (useIcons && heartIcon != null) {
            batch.draw(heartIcon, healthBarX - 35, healthBarY - 5, 32, 32);
        } else {
            hudFont.setColor(new Color(1f, 0.2f, 0.4f, 1f));
            hudFont.draw(batch, "♥", healthBarX - 45, healthBarY + 22);
        }
        
        batch.end();
        
        // Health bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(hudBorderColor);
        shapeRenderer.rect(healthBarX - 2, healthBarY - 2, healthBarWidth + 4, healthBarHeight + 4);
        
        shapeRenderer.setColor(healthBgColor);
        shapeRenderer.rect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        
        float healthPercent = (float) playerHealth / maxHealth;
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(healthBarX, healthBarY, healthBarWidth * healthPercent, healthBarHeight);
        
        shapeRenderer.end();
        
        batch.begin();
        
        // Health text
        hudFont.setColor(Color.WHITE);
        String healthText = playerHealth + "/" + maxHealth;
        hudFont.draw(batch, healthText, healthBarX + 45, healthBarY + 18);
        
        // Score
        float scoreX = 280;
        float scoreY = VIRTUAL_HEIGHT - 30;
        
        if (useIcons && starIcon != null) {
            batch.draw(starIcon, scoreX - 5, scoreY - 20, 28, 28);
        } else {
            hudFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
            hudFont.draw(batch, "★", scoreX, scoreY);
        }
        
        hudFont.setColor(new Color(0.7f, 0.5f, 0.8f, 1f));
        hudFont.draw(batch, "Score:", scoreX + 30, scoreY);
        hudFont.setColor(Color.WHITE);
        hudFont.draw(batch, String.valueOf(score), scoreX + 120, scoreY);
        
        // Resources
        float iconSize = 24f;
        float resourceY = VIRTUAL_HEIGHT - 30;
        
        // Flour
        float flourX = 430;
        if (useIcons && wheatIcon != null) {
            batch.draw(wheatIcon, flourX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(0.95f, 0.85f, 0.5f, 1f));
        hudFont.draw(batch, String.valueOf(flour), flourX + 30, resourceY);
        
        // Sugar
        float sugarX = 520;
        if (useIcons && sugarIcon != null) {
            batch.draw(sugarIcon, sugarX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(1f, 0.7f, 0.9f, 1f));
        hudFont.draw(batch, String.valueOf(sugar), sugarX + 30, resourceY);
        
        // Money
        float moneyX = 610;
        if (useIcons && coinIcon != null) {
            batch.draw(coinIcon, moneyX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        hudFont.draw(batch, String.valueOf(money) + "$", moneyX + 30, resourceY);
        
        batch.end();
    }
    
    // === PUBLIC METHODS FOR GAME LOGIC ===
    
    public void setPlayerHealth(int health) {
        this.playerHealth = Math.max(0, Math.min(maxHealth, health));
    }
    
    public void damage(int amount) {
        this.playerHealth = Math.max(0, playerHealth - amount);
        System.out.println("💔 Dégâts : -" + amount + " HP (Vie : " + playerHealth + ")");
    }
    
    public void heal(int amount) {
        this.playerHealth = Math.min(maxHealth, playerHealth + amount);
        System.out.println("💚 Soins : +" + amount + " HP (Vie : " + playerHealth + ")");
    }
    
    public void addScore(int points) {
        this.score += points;
        System.out.println("🎯 Score : +" + points + " (Total : " + score + ")");
    }
    
    public void addFlour(int amount) { this.flour += amount; }
    public void addSugar(int amount) { this.sugar += amount; }
    public void addMoney(int amount) { this.money += amount; }
    
    public int getFlour() { return flour; }
    public int getSugar() { return sugar; }
    public int getMoney() { return money; }
    public int getPlayerHealth() { return playerHealth; }
    
    public boolean spendResources(int flourCost, int sugarCost, int moneyCost) {
        if (flour >= flourCost && sugar >= sugarCost && money >= moneyCost) {
            flour -= flourCost;
            sugar -= sugarCost;
            money -= moneyCost;
            System.out.println("💸 Dépensé : " + flourCost + " farine, " + sugarCost + " sucre, " + moneyCost + "$");
            return true;
        }
        System.out.println("❌ Pas assez de ressources !");
        return false;
    }
    
    // === GETTERS FOR MAP/CAMERA ===
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public CameraController getCameraController() {
        return cameraController;
    }
    
    public OrthographicCamera getMapCamera() {
        return mapCamera;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudCamera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void show() {
        System.out.println("📺 GameScreen affiché - Le jeu commence !");
        System.out.println("\n=== CONTROLS ===");
        System.out.println("🎮 Camera: WASD/Arrows, Mouse Wheel zoom, Right-click drag");
        System.out.println("🖱️ Units: Left-click to select, Left-click tile to move");
        System.out.println("⚔️ Combat: Left-click enemy to attack (when in range)");
        System.out.println("⌨️ T/Enter: End turn, Space/ESC: Deselect unit");
        
        // Re-register input processor when screen is shown
        if (cameraController != null && gameInputProcessor != null) {
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(gameInputProcessor);
            multiplexer.addProcessor(cameraController);
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    @Override
    public void hide() {
        // Arrêter la musique quand on quitte le GameScreen
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
            System.out.println("🔇 Musique du jeu arrêtée");
        }
        System.out.println("🔒 GameScreen caché");
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
        hudFont.dispose();
        shapeRenderer.dispose();
        
        if (heartIcon != null) heartIcon.dispose();
        if (starIcon != null) starIcon.dispose();
        if (wheatIcon != null) wheatIcon.dispose();
        if (sugarIcon != null) sugarIcon.dispose();
        if (coinIcon != null) coinIcon.dispose();
        
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        
        // Dispose map
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        
        // Dispose units
        if (unitManager != null) {
            unitManager.dispose();
        }
        
        System.out.println("🗑️ GameScreen nettoyé");
    }
}