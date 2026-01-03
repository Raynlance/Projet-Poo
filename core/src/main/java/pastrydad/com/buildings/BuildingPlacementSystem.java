package pastrydad.com.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import pastrydad.com.map.GameMap;
import pastrydad.com.map.BuildingSpot;
import pastrydad.com.resources.ResourceManager;

public class BuildingPlacementSystem {
    
    private GameMap gameMap;
    private BuildingManager buildingManager;
    private ResourceManager resourceManager;
    private OrthographicCamera camera;
    
    // Placement state
    private boolean placementMode = false;
    private String selectedBuildingType = null;
    
    // Preview position (follows mouse)
    private int previewTileX = -1;
    private int previewTileY = -1;
    private boolean validPlacement = false;
    
    public BuildingPlacementSystem(GameMap gameMap, 
                                   BuildingManager buildingManager,
                                   ResourceManager resourceManager,
                                   OrthographicCamera camera) {
        this.gameMap = gameMap;
        this.buildingManager = buildingManager;
        this.resourceManager = resourceManager;
        this.camera = camera;
    }
    
    /**
     * Start placement mode for a specific building type
     */
    public void startPlacement(String buildingType) {
        this.selectedBuildingType = buildingType;
        this.placementMode = true;
        System.out.println("🏗️ Started placement mode for: " + buildingType);
    }
    
    /**
     * Cancel current placement
     */
    public void cancelPlacement() {
        this.placementMode = false;
        this.selectedBuildingType = null;
        this.previewTileX = -1;
        this.previewTileY = -1;
        System.out.println("❌ Placement cancelled");
    }
    
    /**
     * Check if currently in placement mode
     */
    public boolean isInPlacementMode() {
        return placementMode;
    }
    
    /**
     * Update preview position based on mouse coordinates
     */
    public void updatePreviewPosition(int screenX, int screenY) {
        if (!placementMode) return;
        
        // Convert screen coordinates to world coordinates
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        
        // Convert world to tile coordinates
        previewTileX = (int)(worldCoords.x / gameMap.getTileWidth());
        previewTileY = (int)(worldCoords.y / gameMap.getTileHeight());
        
        // Check if this is a valid placement location
        validPlacement = canPlaceAt(previewTileX, previewTileY);
    }
    
    /**
     * Check if a building can be placed at given tile coordinates
     */
    public boolean canPlaceAt(int tileX, int tileY) {
        // Check if within map bounds
        if (!gameMap.isValidTilePosition(tileX, tileY)) {
            return false;
        }
        
        // Check if there's a building spot at this location
        BuildingSpot spot = gameMap.getBuildingSpotAtTile(tileX, tileY);
        if (spot == null) {
            return false;
        }
        
        // Check if spot is available
        if (spot.isOccupied() || !spot.canBuild()) {
            return false;
        }
        
        // Check if player has enough resources
        if (selectedBuildingType != null) {
            java.util.Map<pastrydad.com.resources.ResourceType, Integer> cost = getCostForBuilding(selectedBuildingType);
            if (!resourceManager.canAfford(cost)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Attempt to place building at clicked location
     */
    public boolean handlePlacementClick(int screenX, int screenY) {
        if (!placementMode || selectedBuildingType == null) {
            return false;
        }
        
        // Convert screen to tile coordinates
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        int tileX = (int)(worldCoords.x / gameMap.getTileWidth());
        int tileY = (int)(worldCoords.y / gameMap.getTileHeight());
        
        // Try to place the building
        if (canPlaceAt(tileX, tileY)) {
            Building newBuilding = createBuilding(selectedBuildingType, tileX, tileY);
            
            if (newBuilding != null) {
                // Deduct resources
                if (resourceManager.spend(newBuilding.getCost())) {
                    // Add building to manager
                    buildingManager.addBuilding(newBuilding);
                    
                    // Mark spot as occupied
                    gameMap.occupyBuildingSpot(tileX, tileY, selectedBuildingType, newBuilding);
                    
                    System.out.println("✅ Building placed: " + selectedBuildingType + " at [" + tileX + "," + tileY + "]");
                    
                    // Exit placement mode
                    cancelPlacement();
                    return true;
                }
            }
        }
        
        System.out.println("❌ Cannot place building at [" + tileX + "," + tileY + "]");
        return false;
    }
    
    /**
     * Render preview of building being placed
     */
    public void renderPreview(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (!placementMode || previewTileX < 0 || previewTileY < 0) {
            return;
        }
        
        float x = previewTileX * gameMap.getTileWidth();
        float y = previewTileY * gameMap.getTileHeight();
        float width = gameMap.getTileWidth();
        float height = gameMap.getTileHeight();
        
        // Draw preview rectangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        if (validPlacement) {
            // Green transparent for valid placement
            shapeRenderer.setColor(0.2f, 1f, 0.2f, 0.4f);
        } else {
            // Red transparent for invalid placement
            shapeRenderer.setColor(1f, 0.2f, 0.2f, 0.4f);
        }
        
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        if (validPlacement) {
            shapeRenderer.setColor(0.2f, 1f, 0.2f, 1f);
        } else {
            shapeRenderer.setColor(1f, 0.2f, 0.2f, 1f);
        }
        
        Gdx.gl.glLineWidth(3);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    
    /**
     * Highlight all available building spots
     */
    public void renderAvailableSpots(ShapeRenderer shapeRenderer) {
        if (!placementMode) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 0.6f); // Yellow
        
        for (BuildingSpot spot : gameMap.getAvailableBuildingSpots()) {
            double[] pixelPos = gameMap.tileToPixelTopLeft(spot.getTileX(), spot.getTileY());
            shapeRenderer.rect(
                (float)pixelPos[0], 
                (float)pixelPos[1], 
                (float)spot.getWidth(), 
                (float)spot.getHeight()
            );
        }
        
        shapeRenderer.end();
    }
    
    public String getSelectedBuildingType() {
        return selectedBuildingType;
    }
    
    /**
     * Helper method to get building cost
     */
    private java.util.Map<pastrydad.com.resources.ResourceType, Integer> getCostForBuilding(String type) {
        java.util.Map<pastrydad.com.resources.ResourceType, Integer> cost = new java.util.HashMap<>();
        
        switch(type) {
            case "Farm":
                cost.put(pastrydad.com.resources.ResourceType.GOLD, 50);
                cost.put(pastrydad.com.resources.ResourceType.WOOD, 30);
                break;
            case "Mine":
                cost.put(pastrydad.com.resources.ResourceType.GOLD, 80);
                cost.put(pastrydad.com.resources.ResourceType.WOOD, 50);
                break;
            case "Sawmill":
                cost.put(pastrydad.com.resources.ResourceType.GOLD, 30);
                break;
            case "CommandCenter":
                cost.put(pastrydad.com.resources.ResourceType.GOLD, 200);
                cost.put(pastrydad.com.resources.ResourceType.WOOD, 100);
                cost.put(pastrydad.com.resources.ResourceType.STONE, 100);
                break;
        }
        
        return cost;
    }
    
    /**
     * Helper method to create buildings
     */
    private Building createBuilding(String type, int tileX, int tileY) {
        switch(type) {
            case "Farm":
                return new Farm(tileX, tileY);
            case "Mine":
                return new Mine(tileX, tileY);
            case "Sawmill":
                return new Sawmill(tileX, tileY);
            case "CommandCenter":
                return new CommandCenter(tileX, tileY);
            default:
                System.err.println("Unknown building type: " + type);
                return null;
        }
    }
}