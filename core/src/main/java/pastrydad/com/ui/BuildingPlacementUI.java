package pastrydad.com.ui;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import pastrydad.com.buildings.BuildingPlacementSystem;
import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class BuildingPlacementUI {
    
    private BuildingPlacementSystem placementSystem;
    private ResourceManager resourceManager;
    
    private boolean visible = false;
    private BitmapFont font;
    private BitmapFont smallFont;
    
    // UI Layout
    private static final float PANEL_WIDTH = 300f;
    private static final float PANEL_HEIGHT = 500f;
    private static final float PANEL_X = 10f;
    private static final float PANEL_Y = 100f;
    
    private static final float BUTTON_HEIGHT = 80f;
    private static final float BUTTON_MARGIN = 10f;
    
    // Building buttons
    private Rectangle[] buildingButtons;
    private String[] buildingTypes;
    private int hoveredButton = -1;
    
    // Colors
    private Color panelBgColor = new Color(0.2f, 0.15f, 0.25f, 0.95f);
    private Color panelBorderColor = new Color(0.8f, 0.6f, 0.9f, 1f);
    private Color buttonColor = new Color(0.4f, 0.3f, 0.5f, 1f);
    private Color buttonHoverColor = new Color(0.6f, 0.4f, 0.7f, 1f);
    private Color buttonDisabledColor = new Color(0.3f, 0.25f, 0.35f, 1f);
    
    public BuildingPlacementUI(ResourceManager resourceManager, 
                               BuildingPlacementSystem placementSystem) {
        this.resourceManager = resourceManager;
        this.placementSystem = placementSystem;
        
        // Create fonts
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.2f);
        
        // Get building types - hardcoded for now
        buildingTypes = new String[]{"Farm", "Mine", "Sawmill"};
        
        // Create buttons
        buildingButtons = new Rectangle[buildingTypes.length];
        for (int i = 0; i < buildingTypes.length; i++) {
            buildingButtons[i] = new Rectangle(
                PANEL_X + BUTTON_MARGIN,
                PANEL_Y + PANEL_HEIGHT - 80 - (i * (BUTTON_HEIGHT + BUTTON_MARGIN)),
                PANEL_WIDTH - (BUTTON_MARGIN * 2),
                BUTTON_HEIGHT
            );
        }
    }
    
    public void toggle() {
        visible = !visible;
    }
    
    public void show() {
        visible = true;
    }
    
    public void hide() {
        visible = false;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Update hover state based on mouse position
     */
    public void updateHover(int screenX, int screenY, float screenHeight) {
        if (!visible) {
            hoveredButton = -1;
            return;
        }
        
        // Convert screen Y to UI coordinates
        float mouseY = screenHeight - screenY;
        
        hoveredButton = -1;
        for (int i = 0; i < buildingButtons.length; i++) {
            if (buildingButtons[i].contains(screenX, mouseY)) {
                hoveredButton = i;
                break;
            }
        }
    }
    
    /**
     * Handle click on UI
     * @return true if click was handled by UI
     */
    public boolean handleClick(int screenX, int screenY, float screenHeight) {
        if (!visible) return false;
        
        // Convert screen Y to UI coordinates
        float mouseY = screenHeight - screenY;
        
        // Check building buttons
        for (int i = 0; i < buildingButtons.length; i++) {
            if (buildingButtons[i].contains(screenX, mouseY)) {
                String buildingType = buildingTypes[i];
                
                // Check if player can afford
                if (canAffordBuilding(buildingType)) {
                    placementSystem.startPlacement(buildingType);
                    System.out.println("🏗️ Selected building: " + buildingType);
                } else {
                    System.out.println("❌ Cannot afford " + buildingType);
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if player can afford a building
     */
    private boolean canAffordBuilding(String buildingType) {
        Map<ResourceType, Integer> cost = getBuildingCost(buildingType);
        return resourceManager.canAfford(cost);
    }
    
    /**
     * Get building cost
     */
    private Map<ResourceType, Integer> getBuildingCost(String type) {
        Map<ResourceType, Integer> cost = new java.util.HashMap<>();
        
        switch(type) {
            case "Farm":
                cost.put(ResourceType.GOLD, 50);
                cost.put(ResourceType.WOOD, 30);
                break;
            case "Mine":
                cost.put(ResourceType.GOLD, 80);
                cost.put(ResourceType.WOOD, 50);
                break;
            case "Sawmill":
                cost.put(ResourceType.GOLD, 30);
                break;
        }
        
        return cost;
    }
    
    /**
     * Render the UI
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (!visible) return;
        
        // Draw panel background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Border
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.rect(PANEL_X - 2, PANEL_Y - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4);
        
        // Background
        shapeRenderer.setColor(panelBgColor);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
        
        shapeRenderer.end();
        
        // Draw buttons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int i = 0; i < buildingButtons.length; i++) {
            Rectangle button = buildingButtons[i];
            String buildingType = buildingTypes[i];
            boolean canAfford = canAffordBuilding(buildingType);
            
            // Button color based on state
            if (!canAfford) {
                shapeRenderer.setColor(buttonDisabledColor);
            } else if (hoveredButton == i) {
                shapeRenderer.setColor(buttonHoverColor);
            } else {
                shapeRenderer.setColor(buttonColor);
            }
            
            shapeRenderer.rect(button.x, button.y, button.width, button.height);
        }
        
        shapeRenderer.end();
        
        // Draw text
        batch.begin();
        
        // Panel title
        font.setColor(Color.WHITE);
        font.draw(batch, "BUILD MENU", PANEL_X + 20, PANEL_Y + PANEL_HEIGHT - 20);
        
        // Building buttons text
        for (int i = 0; i < buildingButtons.length; i++) {
            Rectangle button = buildingButtons[i];
            String buildingType = buildingTypes[i];
            Map<ResourceType, Integer> cost = getBuildingCost(buildingType);
            boolean canAfford = canAffordBuilding(buildingType);
            
            // Building name
            if (canAfford) {
                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.GRAY);
            }
            font.draw(batch, buildingType, button.x + 10, button.y + button.height - 15);
            
            // Cost display
            float costY = button.y + button.height - 40;
            for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
                ResourceType resource = entry.getKey();
                int amount = entry.getValue();
                int playerAmount = resourceManager.get(resource);
                
                // Color based on affordability
                if (playerAmount >= amount) {
                    smallFont.setColor(new Color(0.4f, 1f, 0.4f, 1f)); // Green
                } else {
                    smallFont.setColor(new Color(1f, 0.4f, 0.4f, 1f)); // Red
                }
                
                String costText = resource.name() + ": " + amount;
                smallFont.draw(batch, costText, button.x + 10, costY);
                costY -= 18;
            }
        }
        
        // Instructions at bottom
        smallFont.setColor(Color.LIGHT_GRAY);
        smallFont.draw(batch, "Press B to toggle", PANEL_X + 10, PANEL_Y + 30);
        smallFont.draw(batch, "ESC to cancel placement", PANEL_X + 10, PANEL_Y + 10);
        
        batch.end();
    }
    
    public void dispose() {
        font.dispose();
        smallFont.dispose();
    }
}