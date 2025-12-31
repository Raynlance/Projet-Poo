package pastrydad.com.ui;

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

public class GameScreen implements Screen {
    private GameMain game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont hudFont;
    private ShapeRenderer shapeRenderer;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    
    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 600f;
    
    // HUD - Variables de jeu
    private int playerHealth = 100;
    private int maxHealth = 100;
    private int score = 0;
    private int flour = 50;      
    private int sugar = 30;      
    private int money = 100;     
    
    // Icônes HUD
    private Texture heartIcon;
    private Texture starIcon;
    private Texture wheatIcon;
    private Texture sugarIcon;
    private Texture coinIcon;
    private boolean useIcons = false;
    
    // Musique
    private Music backgroundMusic;
    
    // Couleurs - STYLE CARTOON MIGNON
    private Color healthColor = new Color(1f, 0.3f, 0.5f, 1f);          // Rose vif
    private Color healthBgColor = new Color(1f, 0.8f, 0.9f, 1f);        // Rose très clair
    private Color hudBgColor = new Color(1f, 0.85f, 0.95f, 0.95f);     // Rose pastel
    private Color hudBorderColor = new Color(1f, 0.6f, 0.8f, 1f);      // Rose moyen
    private Color gameBackgroundColor = new Color(0.95f, 0.85f, 1f, 1f); // Violet très clair
    
    public GameScreen(GameMain game) {
        this.game = game;
        
        System.out.println("🎮 GameScreen - Initialisation...");
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.6f);  // Réduit
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);  // Réduit
        
        hudFont = new BitmapFont();
        hudFont.getData().setScale(1.8f);  // Réduit pour HUD plus petit
        
        // Charger les icônes
        loadIcons();
        
        // Charger la musique
        loadMusic();
        
        System.out.println("✅ GameScreen - Initialisé avec succès !");
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
        
        // Fond de jeu
        Gdx.gl.glClearColor(gameBackgroundColor.r, gameBackgroundColor.g, gameBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Zone de jeu
        drawGameArea();
        
        // HUD par-dessus
        drawHUD();
    }
    
    private void update(float delta) {
        // === ICI VOTRE ÉQUIPE AJOUTERA LA LOGIQUE DU JEU ===
        // Exemple :
        // mapManager.update(delta);
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
        // === HUD PLUS PETIT (80px au lieu de 130px) ===
        float hudHeight = 80f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Bordure rose
        shapeRenderer.setColor(hudBorderColor);
        shapeRenderer.rect(0, VIRTUAL_HEIGHT - hudHeight, VIRTUAL_WIDTH, hudHeight);
        
        // Fond rose pastel
        shapeRenderer.setColor(hudBgColor);
        shapeRenderer.rect(4, VIRTUAL_HEIGHT - (hudHeight - 4), VIRTUAL_WIDTH - 8, hudHeight - 8);
        
        shapeRenderer.end();
        
        
        float healthBarX = 90;
        float healthBarY = VIRTUAL_HEIGHT - 50;
        float healthBarWidth = 180;
        float healthBarHeight = 20;
        
        batch.begin();
        
        // Icône cœur ou symbole
        if (useIcons && heartIcon != null) {
            batch.draw(heartIcon, healthBarX - 35, healthBarY - 5, 32, 32);
        } else {
            hudFont.setColor(new Color(1f, 0.2f, 0.4f, 1f));
            hudFont.draw(batch, "♥", healthBarX - 45, healthBarY + 22);
        }
        
        batch.end();
        
        // Barre de vie
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Bordure
        shapeRenderer.setColor(hudBorderColor);
        shapeRenderer.rect(healthBarX - 2, healthBarY - 2, healthBarWidth + 4, healthBarHeight + 4);
        
        // Fond
        shapeRenderer.setColor(healthBgColor);
        shapeRenderer.rect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        
        // Barre rose
        float healthPercent = (float) playerHealth / maxHealth;
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(healthBarX, healthBarY, healthBarWidth * healthPercent, healthBarHeight);
        
        shapeRenderer.end();
        
        batch.begin();
        
        // Texte vie
        hudFont.setColor(Color.WHITE);
        String healthText = playerHealth + "/" + maxHealth;
        hudFont.draw(batch, healthText, healthBarX + 45, healthBarY + 18);
        
        // === SCORE ===
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
        
        // === RESSOURCES - BIEN ESPACÉES ===
        float iconSize = 24f;
        float resourceY = VIRTUAL_HEIGHT - 30;
        
        // Farine
        float flourX = 430;
        if (useIcons && wheatIcon != null) {
            batch.draw(wheatIcon, flourX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(0.95f, 0.85f, 0.5f, 1f));
        hudFont.draw(batch, String.valueOf(flour), flourX + 30, resourceY);
        
        // Sucre
        float sugarX = 520;
        if (useIcons && sugarIcon != null) {
            batch.draw(sugarIcon, sugarX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(1f, 0.7f, 0.9f, 1f));
        hudFont.draw(batch, String.valueOf(sugar), sugarX + 30, resourceY);
        
        // Argent
        float moneyX = 610;
        if (useIcons && coinIcon != null) {
            batch.draw(coinIcon, moneyX, resourceY - 18, iconSize, iconSize);
        }
        hudFont.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        hudFont.draw(batch, String.valueOf(money) + "$", moneyX + 30, resourceY);
        
        batch.end();
    }
    
    private void handleInput() {
        // Retour au menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("🔙 Retour au menu...");
            game.setScreen(new MenuScreen(game));
        }
        
        // === TOUCHES DE TEST (À ENLEVER PLUS TARD) ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            playerHealth = Math.max(0, playerHealth - 10);
            System.out.println("❤️ Vie : " + playerHealth);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            playerHealth = Math.min(maxHealth, playerHealth + 10);
            System.out.println("💚 Vie : " + playerHealth);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            score += 100;
            System.out.println("🎯 Score : " + score);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            flour += 10;
            System.out.println("🌾 Farine : " + flour);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            sugar += 10;
            System.out.println("🍬 Sucre : " + sugar);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            money += 50;
            System.out.println("💰 Argent : " + money);
        }
    }
    

    // Ces méthodes permettent de modifier le HUD automatiquement depuis le jeu
    
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
    
    public void addFlour(int amount) {
        this.flour += amount;
    }
    
    public void addSugar(int amount) {
        this.sugar += amount;
    }
    
    public void addMoney(int amount) {
        this.money += amount;
    }
    
    public int getFlour() {
        return flour;
    }
    
    public int getSugar() {
        return sugar;
    }
    
    public int getMoney() {
        return money;
    }
    
    public int getPlayerHealth() {
        return playerHealth;
    }
    
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void show() {
        System.out.println("📺 GameScreen affiché - Le jeu commence !");
    }

    @Override
    public void hide() {
        System.out.println("⏸️ GameScreen caché");
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
        
        // Nettoyer les icônes
        if (heartIcon != null) heartIcon.dispose();
        if (starIcon != null) starIcon.dispose();
        if (wheatIcon != null) wheatIcon.dispose();
        if (sugarIcon != null) sugarIcon.dispose();
        if (coinIcon != null) coinIcon.dispose();
        
        // Arrêter la musique
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        
        System.out.println("🗑️ GameScreen nettoyé");
    }
}