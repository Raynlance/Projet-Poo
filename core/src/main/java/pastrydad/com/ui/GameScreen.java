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
import pastrydad.com.buildings.BuildingManager;
import pastrydad.com.buildings.BuildingPlacementSystem;
import pastrydad.com.buildings.CommandCenter;
import pastrydad.com.map.GameMap;
import pastrydad.com.map.MapLoader;
import pastrydad.com.map.MapRenderer;
import pastrydad.com.map.SpawnManager;
import pastrydad.com.input.CameraController;
import pastrydad.com.combat.UnitManager;
import pastrydad.com.combat.UnitFactory;
import pastrydad.com.combat.CombatSystem;
import pastrydad.com.combat.EnemyAI;
import pastrydad.com.combat.MovementSystem;
import pastrydad.com.combat.TurnManager;
import pastrydad.com.input.GameInputProcessor;
import pastrydad.com.entities.Unit;
import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;
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
    
    // System for the map
    private GameMap gameMap;
    private MapRenderer mapRenderer;
    private OrthographicCamera mapCamera;
    private CameraController cameraController;
    
    // Combat systems
    private UnitManager unitManager;
    private MovementSystem movementSystem;
    private GameInputProcessor gameInputProcessor;
    private CombatSystem combatSystem;
    private EnemyAI enemyAI;
    private BuildingManager buildingManager;
    private TurnManager turnManager;
    private SpawnManager spawnManager;
    
    // Resource and UI systems
    private ResourceManager resourceManager;
    private UnitFactory unitFactory;
    private CommandCenterUI commandCenterUI;
    
    // Building placement system
    private BuildingPlacementSystem buildingPlacementSystem;
    private BuildingPlacementUI buildingPlacementUI;
    
    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 600f;
    
   
      
    
    // HUD icons
    private Texture starIcon;
    private Texture wheatIcon;
    private Texture sugarIcon;
    private Texture coinIcon;
    private Texture woodIcon;    
    private Texture stoneIcon;
    private boolean useIcons = false;
    
    // Music
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
        
        // LOAD MAP 
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
            combatSystem = new CombatSystem();
            enemyAI = new EnemyAI(unitManager, movementSystem, combatSystem);
            
            // Initialize resource manager
            resourceManager = new ResourceManager();
            
            // Give player starting resources
            resourceManager.add(ResourceType.GOLD, 500);
            resourceManager.add(ResourceType.FOOD, 300);
            resourceManager.add(ResourceType.STONE, 200);
            resourceManager.add(ResourceType.WOOD, 400);
            
            // Create building manager with dependencies
            buildingManager = new BuildingManager(gameMap, resourceManager);
            
            // Initialize building placement system
            buildingPlacementSystem = new BuildingPlacementSystem(
                gameMap, 
                buildingManager, 
                resourceManager,
                mapCamera
            );
            
            buildingPlacementUI = new BuildingPlacementUI(
                resourceManager,
                buildingPlacementSystem
            );
            
            // Create unit factory
            unitFactory = new UnitFactory(unitManager, resourceManager, gameMap);
            
            // Create a test Command Center at tile position (10, 10)
            CommandCenter testCommandCenter = new CommandCenter(10, 10);
            buildingManager.addBuilding(testCommandCenter);
            
            // Create Command Center UI
            commandCenterUI = new CommandCenterUI(testCommandCenter, resourceManager, unitFactory);
            
            System.out.println("✅ Resource and building systems initialized!");
            
            spawnManager = new SpawnManager(gameMap, unitManager);
            turnManager = new TurnManager(unitManager, enemyAI);
            turnManager.setSpawnManager(spawnManager);
            turnManager.setBuildingManager(buildingManager); 
            
            gameInputProcessor = new GameInputProcessor(mapCamera, gameMap, unitManager, movementSystem, cameraController) {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    System.out.println("CLICK DETECTED - Button: " + button + " at screen [" + screenX + ", " + screenY + "]");
                    
                    // PRIORITY 1: Building placement mode
                    if (buildingPlacementSystem.isInPlacementMode()) {
                        if (button == Input.Buttons.LEFT) {
                            buildingPlacementSystem.handlePlacementClick(screenX, screenY);
                            return true;
                        } else if (button == Input.Buttons.RIGHT) {
                            buildingPlacementSystem.cancelPlacement();
                            return true;
                        }
                    }
                    
                    // PRIORITY 2: Normal game input (unit selection/movement)
                    return super.touchDown(screenX, screenY, pointer, button);
                }
                
                @Override
                public boolean mouseMoved(int screenX, int screenY) {
                    // Update building placement preview
                    buildingPlacementSystem.updatePreviewPosition(screenX, screenY);
                    return super.mouseMoved(screenX, screenY);
                }
            };
            gameInputProcessor.setBuildingPlacement(buildingPlacementSystem, buildingPlacementUI);
            // Setup input multiplexer to handle both camera and game input
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(gameInputProcessor);
            multiplexer.addProcessor(cameraController);
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
        System.out.println("   - C: Toggle Command Center UI");
        System.out.println("   - B: Toggle Building Menu");
        System.out.println("   - 1/2/3: Spawn units (when Command Center UI is open)");
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
    
            starIcon = new Texture(Gdx.files.internal("ui/icons/star.png"));
            wheatIcon = new Texture(Gdx.files.internal("ui/icons/wheat.png"));
            sugarIcon = new Texture(Gdx.files.internal("ui/icons/sugar.png"));
            coinIcon = new Texture(Gdx.files.internal("ui/icons/coin.png"));
            woodIcon = new Texture(Gdx.files.internal("ui/icons/wood.png"));    
            stoneIcon = new Texture(Gdx.files.internal("ui/icons/stone.png"));  
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
            backgroundMusic.setVolume(0.3f);
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
            
            // Render buildings
            batch.begin();
            buildingManager.render(batch);
            batch.end();
            
            // Render building placement preview and available spots
            if (buildingPlacementSystem.isInPlacementMode()) {
                batch.setProjectionMatrix(mapCamera.combined);
                shapeRenderer.setProjectionMatrix(mapCamera.combined);
                
                buildingPlacementSystem.renderAvailableSpots(shapeRenderer);
                buildingPlacementSystem.renderPreview(batch, shapeRenderer);
            }
            
            // Render selection highlight
            renderSelectionHighlight();
        }
        
        // RENDER HUD ON TOP
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        
        drawHUD();
        
        // Render Command Center UI (on top of everything)
        commandCenterUI.render(batch, shapeRenderer);
        
        // Render Building Placement UI (on top of everything)
        buildingPlacementUI.render(batch, shapeRenderer);
    }
    
    

    private void update(float delta) {
        // Update Command Center UI (handles keyboard input for unit spawning)
        if (commandCenterUI != null) {
            commandCenterUI.update(delta);
        }
        
        // Other game logic updates can go here
    }
    
    private void handleInput() {
        // Toggle Building Menu with B key
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            buildingPlacementUI.toggle();
            System.out.println("🗂️ Building menu toggled - visible: " + buildingPlacementUI.isVisible());
        }
        
        // Toggle Command Center UI with C key
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            commandCenterUI.toggle();
            System.out.println("🗂️ Command Center UI toggled");
        }
        
        // ESC - Updated to handle placement cancellation first
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (buildingPlacementSystem.isInPlacementMode()) {
                buildingPlacementSystem.cancelPlacement();
            }
            else if (gameInputProcessor != null && gameInputProcessor.getSelectedUnit() == null) {
                System.out.println("🔙 Retour au menu...");
                game.setScreen(new MenuScreen(game));
            }
        }
        
    
        
        // Reset camera (R key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && cameraController != null) {
            cameraController.resetCamera();
            System.out.println("📷 Camera reset to center");
        }
        
        // End turn (T or Enter key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (turnManager != null && turnManager.isPlayerTurn()) {
                System.out.println("\n⭐️ Fin du tour joueur !");
                turnManager.endPlayerTurn();
            }
        }
    }
    
    private void renderMovementRanges() {
        if (gameInputProcessor == null) {
            return;
        }
        
        shapeRenderer.setProjectionMatrix(mapCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        List<int[]> reachable = gameInputProcessor.getReachableTiles();
        if (reachable != null) {
            shapeRenderer.setColor(0.2f, 1f, 0.2f, 0.3f);
            for (int[] tile : reachable) {
                float x = tile[0] * gameMap.getTileWidth();
                float y = tile[1] * gameMap.getTileHeight();
                shapeRenderer.rect(x, y, gameMap.getTileWidth(), gameMap.getTileHeight());
            }
        }
        
        List<int[]> attackable = gameInputProcessor.getAttackableTiles();
        if (attackable != null) {
            shapeRenderer.setColor(1f, 0.2f, 0.2f, 0.3f);
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
            
            shapeRenderer.setColor(1f, 1f, 0f, 1f);
            float x = selected.getTileX() * gameMap.getTileWidth();
            float y = selected.getTileY() * gameMap.getTileHeight();
            float width = gameMap.getTileWidth();
            float height = gameMap.getTileHeight();
            
            for (int i = 0; i < 3; i++) {
                shapeRenderer.rect(x - i, y - i, width + i * 2, height + i * 2);
            }
            
            shapeRenderer.end();
        }
    }
    private void drawHUD() {
        float hudHeight = 60f;
        
        // Draw HUD background panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(hudBorderColor);
        shapeRenderer.rect(0, VIRTUAL_HEIGHT - hudHeight, VIRTUAL_WIDTH, hudHeight);
        shapeRenderer.setColor(hudBgColor);
        shapeRenderer.rect(4, VIRTUAL_HEIGHT - (hudHeight - 4), VIRTUAL_WIDTH - 8, hudHeight - 8);
        shapeRenderer.end();
        
        batch.begin();
        
        float resourceY = VIRTUAL_HEIGHT - 30;
        float iconSize = 28f;
        float spacing = 120f; // Espacement entre les ressources
        
        // Calculer la position de départ pour centrer les 4 ressources
        float totalWidth = spacing * 3; // 4 ressources = 3 espacements
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;
        
        // GOLD (Or) - coin.png
        float goldX = startX;
        if (useIcons && coinIcon != null) {
            batch.draw(coinIcon, goldX, resourceY - 12, iconSize, iconSize);
            hudFont.setColor(new Color(1f, 0.84f, 0f, 1f));
            String goldText = String.valueOf(resourceManager.get(ResourceType.GOLD));
            hudFont.draw(batch, goldText, goldX + 32, resourceY + 8);
        } else {
            hudFont.setColor(new Color(1f, 0.84f, 0f, 1f));
            String goldText = "💰 " + resourceManager.get(ResourceType.GOLD);
            hudFont.draw(batch, goldText, goldX, resourceY + 8);
        }
        
        // WOOD (Bois) - wood.png ✅
        float woodX = startX + spacing;
        if (useIcons && woodIcon != null) {
            batch.draw(woodIcon, woodX, resourceY - 12, iconSize, iconSize);
            hudFont.setColor(new Color(0.6f, 0.3f, 0.1f, 1f));
            String woodText = String.valueOf(resourceManager.get(ResourceType.WOOD));
            hudFont.draw(batch, woodText, woodX + 32, resourceY + 8);
        } else {
            hudFont.setColor(new Color(0.6f, 0.3f, 0.1f, 1f));
            String woodText = "🪵 " + resourceManager.get(ResourceType.WOOD);
            hudFont.draw(batch, woodText, woodX, resourceY + 8);
        }
        
        // FOOD (Nourriture) - sugar.png (puisque c'est un jeu de pâtisserie!) ✅
        float foodX = startX + spacing * 2;
        if (useIcons && sugarIcon != null) {
            batch.draw(sugarIcon, foodX, resourceY - 12, iconSize, iconSize);
            hudFont.setColor(new Color(1f, 0.7f, 0.9f, 1f));
            String foodText = String.valueOf(resourceManager.get(ResourceType.FOOD));
            hudFont.draw(batch, foodText, foodX + 32, resourceY + 8);
        } else {
            hudFont.setColor(new Color(0.95f, 0.85f, 0.5f, 1f));
            String foodText = "🌾 " + resourceManager.get(ResourceType.FOOD);
            hudFont.draw(batch, foodText, foodX, resourceY + 8);
        }
        
        // STONE (Pierre) - stone.png ✅
        float stoneX = startX + spacing * 3;
        if (useIcons && stoneIcon != null) {
            batch.draw(stoneIcon, stoneX, resourceY - 12, iconSize, iconSize);
            hudFont.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
            String stoneText = String.valueOf(resourceManager.get(ResourceType.STONE));
            hudFont.draw(batch, stoneText, stoneX + 32, resourceY + 8);
        } else {
            hudFont.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
            String stoneText = "🪨 " + resourceManager.get(ResourceType.STONE);
            hudFont.draw(batch, stoneText, stoneX, resourceY + 8);
        }
        
        // Score dans le coin gauche - star.png ✅
        float scoreX = 20;
        if (useIcons && starIcon != null) {
            batch.draw(starIcon, scoreX, resourceY - 12, 24, 24);
            hudFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
            hudFont.draw(batch, String.valueOf(unitManager.getPlayerScore()), scoreX + 28, resourceY + 8);
        } else {
            hudFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
            hudFont.draw(batch, "⭐ " + unitManager.getPlayerScore(), scoreX, resourceY + 8);
        }
        
        batch.end();
    }
    
    
    
    
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    
    
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
        System.out.println("🗂️ C: Toggle Command Center UI");
        System.out.println("🗂️ B: Toggle Building Menu");
        System.out.println("⌨️ 1/2/3: Spawn Whisk/Pan/Rolling Pin (when Command Center open)");
        
        if (cameraController != null && gameInputProcessor != null) {
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(gameInputProcessor);
            multiplexer.addProcessor(cameraController);
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    @Override
    public void hide() {
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
        
        if (starIcon != null) starIcon.dispose();
        if (wheatIcon != null) wheatIcon.dispose();
        if (sugarIcon != null) sugarIcon.dispose();
        if (coinIcon != null) coinIcon.dispose();
        if (woodIcon != null) woodIcon.dispose();   
        if (stoneIcon != null) stoneIcon.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        
        if (unitManager != null) {
            unitManager.dispose();
        }
        
        if (commandCenterUI != null) {
            commandCenterUI.dispose();
        }
        
        if (buildingPlacementUI != null) {
            buildingPlacementUI.dispose();
        }
        
        System.out.println("🗑️ GameScreen nettoyé");
    }
}