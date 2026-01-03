package pastrydad.com.ui;



import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import pastrydad.com.combat.CombatSystem;
import pastrydad.com.entities.PanGiraffe;
import pastrydad.com.entities.RollingPinGiraffe;
import pastrydad.com.entities.Unit;
import pastrydad.com.entities.WhiskGiraffe;
import pastrydad.com.map.GameMap;
import pastrydad.com.map.MapLoader;
import pastrydad.com.map.SpawnPoint;
import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class GameScreen implements Screen {

  // Rendu de la carte
  private TiledMap tiledMap;
  private OrthogonalTiledMapRenderer mapRenderer;

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

// === SYSTÈMES DE JEU ===
    private GameMap gameMap;
    private ResourceManager resourceManager;
    private CombatSystem combatSystem;

  

// === LISTES D'UNITÉS ===
    private java.util.List<Unit> playerUnits;
    private java.util.List<Unit> enemyUnits;
    
   
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
            
            // Register input processor
            Gdx.input.setInputProcessor(cameraController);
            
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
        initializeGameSystems();
        System.out.println("✅ GameScreen - Initialisé avec succès !");
        System.out.println("   Controls:");
        System.out.println("   - WASD / Arrow Keys: Pan camera");
        System.out.println("   - Mouse Wheel: Zoom in/out");
        System.out.println("   - Right Mouse / Middle Mouse: Drag to pan");
        System.out.println("   - ESC: Return to menu");
    }

    
private void initializeGameSystems() {
    System.out.println("🎮 Initialisation des systèmes de jeu...");
    

    // 1. Initialiser la carte
    try {
        gameMap = new GameMap(40, 70, 16, 16);
        System.out.println("✅ GameMap initialisée (40x70 tuiles, 16x16px)");
        
        System.out.println("🔍 Tentative de chargement de game_map.tmx...");
        tiledMap = new TmxMapLoader().load("game_map.tmx");
        System.out.println("✅ Map loaded: " + (tiledMap != null));
        
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("✅ Carte Tiled chargée pour le rendu");
        }
        
    } catch (Exception e) {
        System.out.println("❌ ERREUR COMPLÈTE: " + e.getMessage());
        e.printStackTrace();  // Affiche toute l'erreur
    }
    
    // 2. Initialiser le gestionnaire de ressources
    try {
        resourceManager = new ResourceManager();
        // Donner des ressources de départ au joueur
        // Donner des ressources de départ au joueur
        resourceManager.add(ResourceType.GOLD, 200);
        resourceManager.add(ResourceType.WOOD, 100);
        resourceManager.add(ResourceType.STONE, 50);
        resourceManager.add(ResourceType.FOOD, 75);
        System.out.println("✅ ResourceManager initialisé");
    } catch (Exception e) {
        System.out.println("❌ Erreur ResourceManager: " + e.getMessage());
        e.printStackTrace();
    }
    
    // 3. Initialiser le système de combat
    try {
        combatSystem = new CombatSystem();
        System.out.println("✅ CombatSystem initialisé");
    } catch (Exception e) {
        System.out.println("❌ Erreur CombatSystem: " + e.getMessage());
        e.printStackTrace();
    }
    
    // 4. Initialiser les listes d'unités
    playerUnits = new ArrayList<>();
    enemyUnits = new ArrayList<>();
    System.out.println("✅ Listes d'unités initialisées");
    
    
    spawnInitialPlayerUnits(); 
    
    System.out.println("🎮 Tous les systèmes sont prêts!");
}

     private void spawnInitialPlayerUnits() {
    try {
        System.out.println("🦒 Création des unités initiales du joueur...");
        
        // Créer 2 unités de départ pour le joueur
        PanGiraffe unit1 = new PanGiraffe(5, 5, true);
        unit1.loadTexture();
        playerUnits.add(unit1);
        System.out.println("✅ PanGiraffe joueur créée à (5, 5)");
        
        WhiskGiraffe unit2 = new WhiskGiraffe(6, 5, true);
        unit2.loadTexture();
        playerUnits.add(unit2);
        System.out.println("✅ WhiskGiraffe joueur créée à (6, 5)");
        
    } catch (Exception e) {
        System.err.println("❌ Erreur création unités joueur: " + e.getMessage());
        e.printStackTrace();
    }
}
     private void updateSpawnPoints(float deltaTime) {
    if (gameMap == null || gameMap.getSpawnPoints() == null) {
        return;
    }
    
    for (SpawnPoint spawnPoint : gameMap.getSpawnPoints()) {
        // Incrémenter le compteur
        spawnPoint.incrementCounter();
        
        // Vérifier s'il faut spawn
        if (spawnPoint.shouldSpawn()) {
            // Créer l'ennemi selon le type
            Unit enemy = createEnemyFromSpawnPoint(spawnPoint);
            
            if (enemy != null) {
                enemyUnits.add(enemy);
                enemy.loadTexture();
                spawnPoint.recordSpawn();
                spawnPoint.resetCounter();
                
                System.out.println("✅ Ennemi spawné: " + enemy.getUnitType() + 
                                 " à [" + spawnPoint.getTileX() + "," + spawnPoint.getTileY() + "]");
            }
        }
    }
}    
private Unit createEnemyFromSpawnPoint(SpawnPoint spawnPoint) {
    int x = spawnPoint.getTileX();
    int y = spawnPoint.getTileY();
    String enemyType = spawnPoint.getEnemyType();
    
    // Créer l'ennemi selon son type (false = c'est un ennemi)
    return switch (enemyType) {
        case "pan" -> new PanGiraffe(x, y, false);
        case "rolling" -> new RollingPinGiraffe(x, y, false);
        case "whisk" -> new WhiskGiraffe(x, y, false);
        default -> new PanGiraffe(x, y, false); // Type par défaut
    };
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
            backgroundMusic.setVolume(1.0f);  // 100% de volume
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
        updateSpawnPoints(delta);
        
        // Fond de jeu
        Gdx.gl.glClearColor(gameBackgroundColor.r, gameBackgroundColor.g, gameBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.position.set(640 / 2f, 1120 / 2f, 0);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
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
    
    private void drawGameArea() {
        batch.begin();
        
        // Message temporaire au centre
        titleFont.setColor(new Color(1f, 0.4f, 0.7f, 1f));
        String message = "~ ZONE DE JEU ~";
        titleFont.draw(batch, message, VIRTUAL_WIDTH / 2 - 180, VIRTUAL_HEIGHT / 2 + 80);
        
        font.setColor(new Color(0.6f, 0.3f, 0.7f, 1f));
        font.draw(batch, "Votre equipe ajoutera le gameplay ici :", 180, VIRTUAL_HEIGHT / 2 + 20);
        
        font.setColor(new Color(1f, 0.5f, 0.6f, 1f));
        font.draw(batch, "• Carte du jeu", 230, VIRTUAL_HEIGHT / 2 - 20);
        
        font.setColor(new Color(1f, 0.7f, 0.3f, 1f));
        font.draw(batch, "• Girafes combattantes", 230, VIRTUAL_HEIGHT / 2 - 55);
        
        font.setColor(new Color(0.9f, 0.6f, 0.9f, 1f));
        font.draw(batch, "• Batiments (boulangerie)", 230, VIRTUAL_HEIGHT / 2 - 90);
        
        font.setColor(new Color(1f, 0.4f, 0.5f, 1f));
        font.draw(batch, "• Systeme de combat", 230, VIRTUAL_HEIGHT / 2 - 125);
        
        // === MESSAGE EN BAS ===
        font.setColor(new Color(1f, 0.3f, 0.4f, 1f));
        font.draw(batch, "Appuyez sur ECHAP pour retourner au menu", 180, 50);
        
        batch.end();
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
            hudFont.draw(batch, "♥", healthBarX - 45, healthBarY + 20);
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
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void show() {
        System.out.println("📺 GameScreen affiché - Le jeu commence !");
        
        // Re-register input processor when screen is shown
        if (cameraController != null) {
            Gdx.input.setInputProcessor(cameraController);
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
        if (tiledMap != null) tiledMap.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        
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
        
        System.out.println("🗑️ GameScreen nettoyé");
    }
}
