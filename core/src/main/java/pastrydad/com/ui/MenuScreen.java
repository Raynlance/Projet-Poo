package pastrydad.com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pastrydad.com.GameMain;

public class MenuScreen implements Screen {
    private GameMain game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private ShapeRenderer shapeRenderer;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    
    private static final float VIRTUAL_WIDTH = 800f;
    private static final float VIRTUAL_HEIGHT = 600f;
    
    private Rectangle newGameButton;
    private Rectangle quitButton;
    
    private Color pinkColor = new Color(1f, 0.4f, 0.7f, 1f);
    private Color yellowColor = new Color(1f, 0.9f, 0.2f, 1f);
    private Color purpleColor = new Color(0.6f, 0.4f, 0.9f, 1f);
    private Color blueColor = new Color(0.4f, 0.7f, 1f, 1f);
    private Color backgroundColor = new Color(1f, 0.85f, 0.95f, 1f);
    
    private Texture[] decorationTextures;
    private boolean useTextures = false;
    
    private Vector2[] decorationPositions;
    private Vector2[] decorationBasePositions;
    private float[] decorationSizes;
    private float[] decorationFloatOffsets;
    
    private GlyphLayout layout;
    
    private float time = 0f;
    
    // Musique de fond
    private Music backgroundMusic;

    public MenuScreen(GameMain game) {
        this.game = game;
        
        System.out.println("✅ MenuScreen - Initialisation...");
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(2f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        
        layout = new GlyphLayout();
        
        float buttonWidth = 460f;
        float buttonHeight = 80f;
        float centerX = VIRTUAL_WIDTH / 2;
        float buttonSpacing = 20f;
        
        newGameButton = new Rectangle(
            centerX - buttonWidth / 2,
            VIRTUAL_HEIGHT / 2 + 20,
            buttonWidth,
            buttonHeight
        );
        
        quitButton = new Rectangle(
            centerX - buttonWidth / 2,
            newGameButton.y - buttonHeight - buttonSpacing,
            buttonWidth,
            buttonHeight
        );
        
        loadDecorationTextures();
        initializeDecorations();
        
        // Charger et lancer la musique du menu
        loadMenuMusic();
        
        System.out.println("✅ MenuScreen - Initialisé avec succès !");
    }
    
    private void loadMenuMusic() {
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
            System.out.println("🎵 Musique du menu lancée !");
        } catch (Exception e) {
            System.out.println("⚠️ Musique du menu non trouvée : " + e.getMessage());
        }
    }
    
    private void loadDecorationTextures() {
        try {
            decorationTextures = new Texture[15];
            
            String[] textureFiles = {
                "giraffe1.png",
                "giraffe2.png",
                "giraffe3.png",
                "giraffe4.png",
                "giraffe1.png",
                "giraffe2.png",
                "giraffe3.png",
                "giraffe4.png",
                "giraffe1.png",
                "giraffe2.png",
                "giraffe3.png",
                "giraffe4.png",
                "giraffe1.png",
                "giraffe2.png",
                "giraffe3.png"
            };
            
            int loadedCount = 0;
            for (int i = 0; i < textureFiles.length && i < decorationTextures.length; i++) {
                if (Gdx.files.internal(textureFiles[i]).exists()) {
                    decorationTextures[i] = new Texture(Gdx.files.internal(textureFiles[i]));
                    loadedCount++;
                    System.out.println("✅ Image chargée : " + textureFiles[i]);
                }
            }
            
            if (loadedCount > 0) {
                useTextures = true;
                System.out.println("✅ " + loadedCount + " images de girafes chargées !");
            } else {
                System.out.println("⚠️ Aucune image trouvée - utilisation des cercles colorés");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Erreur chargement textures : " + e.getMessage());
            useTextures = false;
        }
    }
    
    private void initializeDecorations() {
        decorationPositions = new Vector2[15];
        decorationBasePositions = new Vector2[15];
        decorationSizes = new float[15];
        decorationFloatOffsets = new float[15];
        
        decorationBasePositions[0] = new Vector2(80, 540);
        decorationSizes[0] = 60f;
        
        decorationBasePositions[1] = new Vector2(250, 530);
        decorationSizes[1] = 55f;
        
        decorationBasePositions[2] = new Vector2(550, 535);
        decorationSizes[2] = 50f;
        
        decorationBasePositions[3] = new Vector2(720, 525);
        decorationSizes[3] = 65f;
        
        decorationBasePositions[4] = new Vector2(60, 450);
        decorationSizes[4] = 55f;
        
        decorationBasePositions[5] = new Vector2(740, 440);
        decorationSizes[5] = 60f;
        
        decorationBasePositions[6] = new Vector2(400, 460);
        decorationSizes[6] = 50f;
        
        decorationBasePositions[7] = new Vector2(70, 320);
        decorationSizes[7] = 55f;
        
        decorationBasePositions[8] = new Vector2(730, 310);
        decorationSizes[8] = 60f;
        
        decorationBasePositions[9] = new Vector2(80, 210);
        decorationSizes[9] = 50f;
        
        decorationBasePositions[10] = new Vector2(720, 200);
        decorationSizes[10] = 65f;
        
        decorationBasePositions[11] = new Vector2(400, 220);
        decorationSizes[11] = 55f;
        
        decorationBasePositions[12] = new Vector2(150, 130);
        decorationSizes[12] = 50f;
        
        decorationBasePositions[13] = new Vector2(400, 110);
        decorationSizes[13] = 60f;
        
        decorationBasePositions[14] = new Vector2(650, 120);
        decorationSizes[14] = 65f;
        
        for (int i = 0; i < decorationPositions.length; i++) {
            decorationPositions[i] = new Vector2(decorationBasePositions[i]);
            decorationFloatOffsets[i] = MathUtils.random(0f, MathUtils.PI2);
        }
    }

    @Override
    public void render(float delta) {
        time += delta;
        updateAnimations(delta);
        handleInput();
        
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        drawDecorations();
        drawButtons();
        drawText();
    }
    
    private void updateAnimations(float delta) {
        for (int i = 0; i < decorationPositions.length; i++) {
            float offset = MathUtils.sin(time * 1.5f + decorationFloatOffsets[i]) * 10f;
            decorationPositions[i].y = decorationBasePositions[i].y + offset;
        }
    }
    
    private void drawDecorations() {
        if (useTextures && decorationTextures != null) {
            batch.begin();
            for (int i = 0; i < decorationPositions.length; i++) {
                if (i < decorationTextures.length && decorationTextures[i] != null) {
                    float size = decorationSizes[i];
                    batch.draw(
                        decorationTextures[i],
                        decorationPositions[i].x - size / 2,
                        decorationPositions[i].y - size / 2,
                        size,
                        size
                    );
                }
            }
            batch.end();
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color[] colors = {yellowColor, pinkColor, blueColor, purpleColor, yellowColor, blueColor, purpleColor};
            
            for (int i = 0; i < decorationPositions.length; i++) {
                shapeRenderer.setColor(colors[i % colors.length]);
                shapeRenderer.circle(
                    decorationPositions[i].x,
                    decorationPositions[i].y,
                    decorationSizes[i] / 2
                );
            }
            shapeRenderer.end();
        }
    }
    
    private void drawButtons() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(pinkColor);
        shapeRenderer.rect(newGameButton.x, newGameButton.y, newGameButton.width, newGameButton.height);
        
        shapeRenderer.setColor(yellowColor);
        shapeRenderer.rect(quitButton.x, quitButton.y, quitButton.width, quitButton.height);
        
        shapeRenderer.end();
    }
    
    private void drawText() {
        batch.begin();
        
        float titleY = VIRTUAL_HEIGHT - 60;
        String[] titleWords = {"GIRAFFE", "BAKERY", "WAR!"};
        Color[] titleColors = {pinkColor, purpleColor, yellowColor};
        
        float totalTitleWidth = 0;
        for (String word : titleWords) {
            layout.setText(titleFont, word + " ");
            totalTitleWidth += layout.width;
        }
        
        float titleX = (VIRTUAL_WIDTH - totalTitleWidth) / 2;
        
        for (int i = 0; i < titleWords.length; i++) {
            titleFont.setColor(titleColors[i]);
            titleFont.draw(batch, titleWords[i], titleX, titleY);
            layout.setText(titleFont, titleWords[i] + " ");
            titleX += layout.width;
        }
        
        font.setColor(blueColor);
        String subtitle = "La bataille la plus delicieuse!";
        layout.setText(font, subtitle);
        font.draw(batch, subtitle, (VIRTUAL_WIDTH - layout.width) / 2, titleY - 60);
        
        font.setColor(Color.WHITE);
        String newGameText = "NOUVELLE PARTIE";
        layout.setText(font, newGameText);
        float newGameTextX = newGameButton.x + (newGameButton.width - layout.width) / 2;
        float newGameTextY = newGameButton.y + (newGameButton.height + layout.height) / 2;
        font.draw(batch, newGameText, newGameTextX, newGameTextY);
        
        font.setColor(Color.BLACK);
        String quitText = "QUITTER";
        layout.setText(font, quitText);
        float quitTextX = quitButton.x + (quitButton.width - layout.width) / 2;
        float quitTextY = quitButton.y + (quitButton.height + layout.height) / 2;
        font.draw(batch, quitText, quitTextX, quitTextY);
        
        batch.end();
    }
    
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            
            if (newGameButton.contains(touchPos)) {
                System.out.println("🎮 Nouvelle Partie cliquée !");
                game.setScreen(new GameScreen(game));
            }
            
            if (quitButton.contains(touchPos)) {
                System.out.println("👋 Au revoir !");
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void show() {
        System.out.println("📺 MenuScreen affiché !");
    }

    @Override
    public void hide() {
    // Arrêter la musique quand on quitte le menu
    if (backgroundMusic != null) {
        backgroundMusic.stop();
        System.out.println("🎵 Musique du menu arrêtée");
    }
    System.out.println("⏸️ MenuScreen caché");
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
        shapeRenderer.dispose();
        
        if (decorationTextures != null) {
            for (Texture texture : decorationTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        
        // Arrêter et nettoyer la musique
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            System.out.println("🎵 Musique du menu arrêtée");
        }
        
        System.out.println("🗑️ MenuScreen nettoyé");
    }
}