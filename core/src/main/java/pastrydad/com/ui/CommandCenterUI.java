package pastrydad.com.ui;

import com.badlogic.gdx.Gdx;
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
    private Color buttonHoverColor = new Color(0.5f, 0.9f, 0.5f, 1f);
    
    // Unit costs (temporary units for cost checking)
    private WhiskGiraffe whiskTemplate;
    private PanGiraffe panTemplate;
    private RollingPinGiraffe rollingPinTemplate;
    
    // Icons (optional)
    private Texture goldIcon;
    private Texture foodIcon;
    private Texture stoneIcon;
    private boolean useIcons = false;
    
    // Hover state
    private int hoveredButton = -1; // -1 = none, 0 = whisk, 1 = pan, 2 = rolling pin
    
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
        panelHeight =85f;
        panelX = (800f - panelWidth) / 2; // Center on screen (assuming 800px width)
        panelY = 70f - panelHeight / 2; // Center vertically
        
        // Create template units for cost checking
        whiskTemplate = new WhiskGiraffe(0, 0, true);
        panTemplate = new PanGiraffe(0, 0, true);
        rollingPinTemplate = new RollingPinGiraffe(0, 0, true);
        
        // Try to load icons
        loadIcons();
        
        System.out.println("🏗️ CommandCenterUI created");
    }
    
    private void loadIcons() {
        try {
            goldIcon = new Texture(Gdx.files.internal("ui/icons/coin.png"));
            foodIcon = new Texture(Gdx.files.internal("ui/icons/wheat.png"));
            stoneIcon = new Texture(Gdx.files.internal("ui/icons/sugar.png")); // Using sugar as stone placeholder
            useIcons = true;
            System.out.println("✅ Command Center icons loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Icons not found, using text symbols");
            useIcons = false;
        }
    }
    
    public void show() {
        visible = true;
        System.out.println("🏗️ Command Center UI opened");
    }
    
    public void hide() {
        visible = false;
        hoveredButton = -1;
        System.out.println("🏗️ Command Center UI closed");
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
        
        // Button 0: Whisk Giraffe
        drawButton(shapeRenderer, 0, startX, buttonY, unitFactory.canAffordWhiskGiraffe());
        
        // Button 1: Pan Giraffe
        drawButton(shapeRenderer, 1, startX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, unitFactory.canAffordPanGiraffe());
        
        // Button 2: Rolling Pin Giraffe
        drawButton(shapeRenderer, 2, startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2, buttonY, unitFactory.canAffordRollingPinGiraffe());
        
        shapeRenderer.end();
    }
    
    private void drawButton(ShapeRenderer shapeRenderer, int buttonIndex, float x, float y, boolean canAfford) {
        // Choose color based on affordability and hover
        Color buttonColor;
        if (!canAfford) {
            buttonColor = buttonUnavailableColor;
        } else if (hoveredButton == buttonIndex) {
            buttonColor = buttonHoverColor;
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
        titleFont.getData().setScale(1.3f);  // Plus petit
        titleFont.setColor(panelBorderColor);
        titleFont.draw(batch, "COMMAND CENTER", panelX + PADDING, panelY + panelHeight - 5);
        titleFont.getData().setScale(2.0f);  // Reset
        float buttonY = panelY + PADDING;
        float startX = panelX + PADDING;
        
        // Button 0: Whisk Giraffe
        drawButtonText(batch, "Whisk\nGiraffe", whiskTemplate, startX, buttonY, unitFactory.canAffordWhiskGiraffe());
        
        // Button 1: Pan Giraffe
        drawButtonText(batch, "Pan\nGiraffe", panTemplate, startX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, unitFactory.canAffordPanGiraffe());
        
        // Button 2: Rolling Pin Giraffe
        drawButtonText(batch, "Rolling Pin\nGiraffe", rollingPinTemplate, startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2, buttonY, unitFactory.canAffordRollingPinGiraffe());
    }
    
    private void drawButtonText(SpriteBatch batch, String name, pastrydad.com.entities.Unit unit, float x, float y, boolean canAfford) {
        // Unit name - plus petit
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        
        // Nom simplifié (enlever "Giraffe")
        String shortName = name.replace("\nGiraffe", "").replace("Giraffe", "");
        font.draw(batch, shortName, x + 5, y + BUTTON_HEIGHT - 5);
        
        // Costs - tout sur une ligne, petit
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
     * Handle mouse click
     * Returns true if click was handled by UI
     */
    public boolean handleClick(int screenX, int screenY, float virtualHeight) {
        if (!visible) return false;
        
        // Convert screen Y to world Y (LibGDX has inverted Y)
        float worldY = virtualHeight - screenY;
        
        // Check if click is within panel
        if (screenX < panelX || screenX > panelX + panelWidth ||
            worldY < panelY || worldY > panelY + panelHeight) {
            // Click outside panel - close it
            
            return true;
        }
        
        // Check button clicks
        float buttonY = panelY + PADDING;
        float startX = panelX + PADDING;
        
        if (worldY >= buttonY && worldY <= buttonY + BUTTON_HEIGHT) {
            // Button 0: Whisk Giraffe
            if (screenX >= startX && screenX <= startX + BUTTON_WIDTH) {
                return handleSpawnWhisk();
            }
            
            // Button 1: Pan Giraffe
            float button1X = startX + BUTTON_WIDTH + BUTTON_SPACING;
            if (screenX >= button1X && screenX <= button1X + BUTTON_WIDTH) {
                return handleSpawnPan();
            }
            
            // Button 2: Rolling Pin Giraffe
            float button2X = startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2;
            if (screenX >= button2X && screenX <= button2X + BUTTON_WIDTH) {
                return handleSpawnRollingPin();
            }
        }
        
        return true; // Click was inside panel, consume it
    }
    
    /**
     * Update hover state based on mouse position
     */
    public void updateHover(int screenX, int screenY, float virtualHeight) {
        if (!visible) {
            hoveredButton = -1;
            return;
        }
        
        float worldY = virtualHeight - screenY;
        
        // Check if mouse is within panel
        if (screenX < panelX || screenX > panelX + panelWidth ||
            worldY < panelY || worldY > panelY + panelHeight) {
            hoveredButton = -1;
            return;
        }
        
        float buttonY = panelY + PADDING;
        float startX = panelX + PADDING;
        
        if (worldY >= buttonY && worldY <= buttonY + BUTTON_HEIGHT) {
            // Check which button is hovered
            if (screenX >= startX && screenX <= startX + BUTTON_WIDTH) {
                hoveredButton = 0;
            } else if (screenX >= startX + BUTTON_WIDTH + BUTTON_SPACING && 
                       screenX <= startX + BUTTON_WIDTH + BUTTON_SPACING + BUTTON_WIDTH) {
                hoveredButton = 1;
            } else if (screenX >= startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2 && 
                       screenX <= startX + (BUTTON_WIDTH + BUTTON_SPACING) * 2 + BUTTON_WIDTH) {
                hoveredButton = 2;
            } else {
                hoveredButton = -1;
            }
        } else {
            hoveredButton = -1;
        }
    }
    
    private boolean handleSpawnWhisk() {
        System.out.println("🦒 Attempting to spawn Whisk Giraffe...");
        if (!unitFactory.canAffordWhiskGiraffe()) {
            System.out.println("❌ Cannot afford Whisk Giraffe!");
            return true;
        }
        
        // Spawn at command center position
        WhiskGiraffe unit = unitFactory.createWhiskGiraffe(
            commandCenter.getTileX(), 
            commandCenter.getTileY(), 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Whisk Giraffe spawned successfully!");
        }
        
        return true;
    }
    
    private boolean handleSpawnPan() {
        System.out.println("🦒 Attempting to spawn Pan Giraffe...");
        if (!unitFactory.canAffordPanGiraffe()) {
            System.out.println("❌ Cannot afford Pan Giraffe!");
            return true;
        }
        
        PanGiraffe unit = unitFactory.createPanGiraffe(
            commandCenter.getTileX(), 
            commandCenter.getTileY(), 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Pan Giraffe spawned successfully!");
        }
        
        return true;
    }
    
    private boolean handleSpawnRollingPin() {
        System.out.println("🦒 Attempting to spawn Rolling Pin Giraffe...");
        if (!unitFactory.canAffordRollingPinGiraffe()) {
            System.out.println("❌ Cannot afford Rolling Pin Giraffe!");
            return true;
        }
        
        RollingPinGiraffe unit = unitFactory.createRollingPinGiraffe(
            commandCenter.getTileX(), 
            commandCenter.getTileY(), 
            true
        );
        
        if (unit != null) {
            System.out.println("✅ Rolling Pin Giraffe spawned successfully!");
        }
        
        return true;
    }
    
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        
        if (goldIcon != null) goldIcon.dispose();
        if (foodIcon != null) foodIcon.dispose();
        if (stoneIcon != null) stoneIcon.dispose();
    }
}