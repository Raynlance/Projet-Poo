package pastrydad.com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pastrydad.com.buildings.CommandCenter;
import pastrydad.com.combat.UnitFactory;
import pastrydad.com.entities.PanGiraffe;
import pastrydad.com.entities.RollingPinGiraffe;
import pastrydad.com.entities.WhiskGiraffe;
import pastrydad.com.resources.ResourceManager;

/**
 * UI Panel for the Command Center building
 * Allows players to spawn units by spending resources
 * NOW CONTROLLED WITH KEYBOARD: 1 = Whisk, 2 = Pan, 3 = Rolling Pin
 */
public class CommandCenterUI {
    
    private CommandCenter commandCenter;
    private ResourceManager resourceManager;
    private UnitFactory unitFactory;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    // UI State
    private boolean visible;
    private float panelX, panelY;
    private float panelWidth, panelHeight;
    
    // Button dimensions
    private static final float BUTTON_WIDTH = 120f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 10f;
    private static final float PADDING = 10f;
    
    // Colors
    private Color panelBgColor = new Color(0.95f, 0.9f, 0.85f, 0.98f);
    private Color panelBorderColor = new Color(0.6f, 0.4f, 0.2f, 1f);
    private Color buttonAvailableColor = new Color(0.4f, 0.8f, 0.4f, 1f);
    private Color buttonUnavailableColor = new Color(0.6f, 0.6f, 0.6f, 1f);
    private Color buttonHighlightColor = new Color(1f, 0.9f, 0.3f, 1f); // Yellow highlight for key press
    
    // Unit costs (temporary units for cost checking)
    private WhiskGiraffe whiskTemplate;
    private PanGiraffe panTemplate;
    private RollingPinGiraffe rollingPinTemplate;
    
    // Icons (optional)
    private Texture goldIcon;
    private Texture foodIcon;
    private Texture stoneIcon;
    private boolean useIcons = false;
    
    // Key press visual feedback
    private int pressedButton = -1; // -1 = none, 0 = whisk, 1 = pan, 2 = rolling pin
    private float pressTimer = 0f;
    private static final float PRESS_DURATION = 0.3f; // Visual feedback duration
    
    public CommandCenterUI(CommandCenter commandCenter, ResourceManager resourceManager, UnitFactory unitFactory) {
        this.commandCenter = commandCenter;
        this.resourceManager = resourceManager;
        this.unitFactory = unitFactory;
        
        this.visible = true;
        
        // Create fonts
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.0f);
        
        // Calculate panel size and position
        panelWidth = (BUTTON_WIDTH + BUTTON_SPACING) * 3 + PADDING * 2 - BUTTON_SPACING;
        panelHeight = 85f;
        panelX = (800f - panelWidth) / 2; // Center on screen (assuming 800px width)
        panelY = 70f - panelHeight / 2; // Center vertically
        
        // Create template units for cost checking
        whiskTemplate = new WhiskGiraffe(0, 0, true);
        panTemplate = new PanGiraffe(0, 0, true);
        rollingPinTemplate = new RollingPinGiraffe(0, 0, true);
        
        // Try to load icons
        loadIcons();
        
        System.out.println("🗂️ CommandCenterUI created");
        System.out.println("⌨️  Keyboard controls: 1 = Whisk, 2 = Pan, 3 = Rolling Pin");
    }
    
    private void loadIcons() {
        try {
            goldIcon = new Texture(Gdx.files.internal("ui/icons/coin.png"));
            foodIcon = new Texture(Gdx.files.internal("ui/icons/wheat.png"));
            stoneIcon = new Texture(Gdx.files.internal("ui/icons/sugar.png"));
            useIcons = true;
            System.out.println("✅ Command Center icons loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Icons not found, using text symbols");
            useIcons = false;
        }
    }
    
    public void show() {
        visible = true;
        System.out.println("🗂️ Command Center UI opened");
        System.out.println("⌨️  Press 1, 2, or 3 to spawn units");
    }
    
    public void hide() {
        visible = false;
        pressedButton = -1;
        System.out.println("🗂️ Command Center UI closed");
    }
    
    public void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Update method - checks for keyboard input and updates visual feedback
     */
    public void update(float delta) {
        if (!visible) return;
        
        // Update press timer for visual feedback
        if (pressTimer > 0) {
            pressTimer -= delta;
            if (pressTimer <= 0) {
                pressedButton = -1;
            }
        }
        
        // Check for keyboard input
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            handleSpawnWhisk();
            pressedButton = 0;
            pressTimer = PRESS_DURATION;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            handleSpawnPan();
            pressedButton = 1;
            pressTimer = PRESS_DURATION;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            handleSpawnRollingPin();
            pressedButton = 2;
            pressTimer = PRESS_DURATION;
        }
    }
    
    /**
     * Render the UI panel
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (!visible) return;
        
        // Draw panel background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Border
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.rect(panelX - 4, panelY - 4, panelWidth + 8, panelHeight + 8);
        
        // Background
        shapeRenderer.setColor(panelBgColor);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        
        shapeRenderer.end();
        
        // Draw buttons
        drawButtons(shapeRenderer);
        
        // Draw text and costs
        batch.begin();
        drawTexts(batch);
        batch.end();
    }
    
    private void drawButtons(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float buttonY = panelY + PADDING;
        float startX = panelX + PADDING;
        
        // Button 0: Whisk Giraffe (Key: 1)
        drawButton(shapeRenderer, 0, startX, buttonY, unitFactory.canAffordWhiskGiraffe());
        
        // Button 1: Pan Giraffe (Key: 2)
        drawButton(shapeRenderer, 1, startX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, unitFactory.canAffordPanGiraffe());
        
        // Button 2: Rolling Pin Giraffe (Key: 3)
        drawButton(shapeRenderer, 2, startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2, buttonY, unitFactory.canAffordRollingPinGiraffe());
        
        shapeRenderer.end();
    }
    
    private void drawButton(ShapeRenderer shapeRenderer, int buttonIndex, float x, float y, boolean canAfford) {
        // Choose color based on affordability and press state
        Color buttonColor;
        if (pressedButton == buttonIndex && pressTimer > 0) {
            // Button was just pressed - show highlight
            buttonColor = buttonHighlightColor;
        } else if (!canAfford) {
            buttonColor = buttonUnavailableColor;
        } else {
            buttonColor = buttonAvailableColor;
        }
        
        // Draw button border
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.rect(x - 2, y - 2, BUTTON_WIDTH + 4, BUTTON_HEIGHT + 4);
        
        // Draw button background
        shapeRenderer.setColor(buttonColor);
        shapeRenderer.rect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
    
    private void drawTexts(SpriteBatch batch) {
        // Title
        titleFont.getData().setScale(1.3f);
        titleFont.setColor(panelBorderColor);
        titleFont.draw(batch, "COMMAND CENTER", panelX + PADDING, panelY + panelHeight - 5);
        titleFont.getData().setScale(2.0f);
        
        float buttonY = panelY + PADDING;
        float startX = panelX + PADDING;
        
        // Button 0: Whisk Giraffe (Key: 1)
        drawButtonText(batch, "Whisk", whiskTemplate, startX, buttonY, unitFactory.canAffordWhiskGiraffe(), "1");
        
        // Button 1: Pan Giraffe (Key: 2)
        drawButtonText(batch, "Pan", panTemplate, startX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, unitFactory.canAffordPanGiraffe(), "2");
        
        // Button 2: Rolling Pin Giraffe (Key: 3)
        drawButtonText(batch, "Rolling Pin", rollingPinTemplate, startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2, buttonY, unitFactory.canAffordRollingPinGiraffe(), "3");
    }
    
    private void drawButtonText(SpriteBatch batch, String name, pastrydad.com.entities.Unit unit, float x, float y, boolean canAfford, String keyLabel) {
        // Unit name
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, name, x + 5, y + BUTTON_HEIGHT - 5);
        
        // Keyboard key indicator (in top-right corner)
        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "[" + keyLabel + "]", x + BUTTON_WIDTH - 28, y + BUTTON_HEIGHT - 3);
        
        // Costs
        font.getData().setScale(0.9f);
        Color costColor = canAfford ? new Color(1f, 1f, 1f, 1f) : new Color(1f, 0.3f, 0.3f, 1f);
        font.setColor(costColor);
        
        float costY = y + 20;
        float iconSize = 12f;
        
        // Gold
        if (useIcons && goldIcon != null) {
            batch.draw(goldIcon, x + 5, costY - iconSize/2, iconSize, iconSize);
        }
        String costText = (useIcons ? "" : "G") + unit.getGoldCost();
        font.draw(batch, costText, x + (useIcons ? 20 : 5), costY + 3);
        
        // Food
        if (useIcons && foodIcon != null) {
            batch.draw(foodIcon, x + 45, costY - iconSize/2, iconSize, iconSize);
        }
        costText = (useIcons ? "" : "F") + unit.getFoodCost();
        font.draw(batch, costText, x + (useIcons ? 60 : 45), costY + 3);
        
        // Stone
        if (useIcons && stoneIcon != null) {
            batch.draw(stoneIcon, x + 85, costY - iconSize/2, iconSize, iconSize);
        }
        costText = (useIcons ? "" : "S") + unit.getStoneCost();
        font.draw(batch, costText, x + (useIcons ? 100 : 85), costY + 3);
        
        font.getData().setScale(1.5f); // Reset
    }
    
    /**
     * Find an empty tile adjacent to the Command Center for spawning units
     */
    private int[] findSpawnPosition() {
        int centerX = commandCenter.getTileX();
        int centerY = commandCenter.getTileY();
        
        System.out.println("🔍 Searching for spawn position near Command Center at [" + centerX + ", " + centerY + "]");
        
        // Check adjacent tiles (up, down, left, right, then diagonals)
        int[][] offsets = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},          // Cardinal directions first
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1},        // Diagonals
            {0, 2}, {0, -2}, {2, 0}, {-2, 0},          // Extended range
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        
        for (int[] offset : offsets) {
            int checkX = centerX + offset[0];
            int checkY = centerY + offset[1];
            
            System.out.println("   Checking [" + checkX + ", " + checkY + "]...");
            
            // Use UnitFactory's canSpawnAt method to check if position is valid
            if (unitFactory.canSpawnAt(checkX, checkY)) {
                System.out.println("   ✅ Found valid spawn position at [" + checkX + ", " + checkY + "]");
                return new int[]{checkX, checkY};
            }
        }
        
        // If no adjacent tile is free, return center (will fail, but logged)
        System.out.println("⚠️ No free tile found near Command Center! Trying center position anyway...");
        return new int[]{centerX, centerY};
    }
    
    private void handleSpawnWhisk() {
        if (!visible) return;
        
        System.out.println("🦒 [KEY 1] Attempting to spawn Whisk Giraffe...");
        if (!unitFactory.canAffordWhiskGiraffe()) {
            System.out.println("❌ Cannot afford Whisk Giraffe!");
            return;
        }
        
        int[] spawnPos = findSpawnPosition();
        
        WhiskGiraffe unit = unitFactory.createWhiskGiraffe(
            spawnPos[0], 
            spawnPos[1], 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Whisk Giraffe spawned successfully at [" + spawnPos[0] + ", " + spawnPos[1] + "]!");
        } else {
            System.out.println("❌ Failed to spawn Whisk Giraffe at [" + spawnPos[0] + ", " + spawnPos[1] + "]");
        }
    }
    
    private void handleSpawnPan() {
        if (!visible) return;
        
        System.out.println("🦒 [KEY 2] Attempting to spawn Pan Giraffe...");
        if (!unitFactory.canAffordPanGiraffe()) {
            System.out.println("❌ Cannot afford Pan Giraffe!");
            return;
        }
        
        int[] spawnPos = findSpawnPosition();
        
        PanGiraffe unit = unitFactory.createPanGiraffe(
            spawnPos[0], 
            spawnPos[1], 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Pan Giraffe spawned successfully at [" + spawnPos[0] + ", " + spawnPos[1] + "]!");
        } else {
            System.out.println("❌ Failed to spawn Pan Giraffe at [" + spawnPos[0] + ", " + spawnPos[1] + "]");
        }
    }
    
    private void handleSpawnRollingPin() {
        if (!visible) return;
        
        System.out.println("🦒 [KEY 3] Attempting to spawn Rolling Pin Giraffe...");
        if (!unitFactory.canAffordRollingPinGiraffe()) {
            System.out.println("❌ Cannot afford Rolling Pin Giraffe!");
            return;
        }
        
        int[] spawnPos = findSpawnPosition();
        
        RollingPinGiraffe unit = unitFactory.createRollingPinGiraffe(
            spawnPos[0], 
            spawnPos[1], 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Rolling Pin Giraffe spawned successfully at [" + spawnPos[0] + ", " + spawnPos[1] + "]!");
        } else {
            System.out.println("❌ Failed to spawn Rolling Pin Giraffe at [" + spawnPos[0] + ", " + spawnPos[1] + "]");
        }
    }
    
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        
        if (goldIcon != null) goldIcon.dispose();
        if (foodIcon != null) foodIcon.dispose();
        if (stoneIcon != null) stoneIcon.dispose();
    }
}