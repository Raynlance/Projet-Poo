package pastrydad.com.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pastrydad.com.map.GameMap;
import pastrydad.com.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class BuildingManager {
    
    private List<Building> buildings;
    private GameMap gameMap;
    private ResourceManager resourceManager;
    
    public BuildingManager(GameMap gameMap, ResourceManager resourceManager) {
        this.gameMap = gameMap;
        this.resourceManager = resourceManager;
        this.buildings = new ArrayList<>();
        System.out.println("🏗️ BuildingManager créé");
    }
    
    /**
     * Add a building to the manager
     */
    public void addBuilding(Building building) {
        buildings.add(building);
        building.loadTexture();
        System.out.println("🏗️ Building added: " + building.getName() + " at [" + building.getTileX() + ", " + building.getTileY() + "]");
    }
    
    /**
     * Remove a building
     */
    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.dispose();
    }
    
    /**
     * Get building at a specific tile
     */
    public Building getBuildingAt(int tileX, int tileY) {
        for (Building building : buildings) {
            if (building.getTileX() == tileX && building.getTileY() == tileY) {
                return building;
            }
        }
        return null;
    }
    
    /**
     * Get all command centers
     */
    public List<CommandCenter> getCommandCenters() {
        List<CommandCenter> commandCenters = new ArrayList<>();
        for (Building building : buildings) {
            if (building instanceof CommandCenter) {
                commandCenters.add((CommandCenter) building);
            }
        }
        return commandCenters;
    }
    
    /**
     * Update all buildings (production, construction, etc.)
     */
    public void update(float delta) {
        for (Building building : buildings) {
            // Progress construction
            if (!building.isConstructed()) {
                building.buildStep();
            }
            
            // Production on turn (if needed)
            if (building.isConstructed()) {
                building.onTurn(resourceManager);
            }
        }
    }
    
    /**
     * Render all buildings
     */
    public void render(SpriteBatch batch) {
        for (Building building : buildings) {
            building.render(batch, gameMap);
        }
    }
    
    /**
     * Get all buildings
     */
    public List<Building> getAllBuildings() {
        return new ArrayList<>(buildings);
    }
    
    public void dispose() {
        for (Building building : buildings) {
            building.dispose();
        }
        buildings.clear();
    }
}