package pastrydad.com.input;

import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import pastrydad.com.combat.CombatResult;
import pastrydad.com.combat.MovementSystem;
import pastrydad.com.combat.UnitManager;
import pastrydad.com.entities.Unit;
import pastrydad.com.map.GameMap;

/**
 * Handles all game input for unit selection, movement, and combat.
 * Works alongside CameraController for camera controls.
 */
public class GameInputProcessor implements InputProcessor {
    
    private OrthographicCamera camera;
    private GameMap gameMap;
    private UnitManager unitManager;
    private MovementSystem movementSystem;
    
    // Currently selected unit
    private Unit selectedUnit = null;
    
    // Visual feedback
    private List<int[]> reachableTiles = null;
    private List<int[]> attackableTiles = null;
    
    // Camera controller reference (for checking if it's panning)
    private CameraController cameraController;
    
    public GameInputProcessor(OrthographicCamera camera, GameMap gameMap, 
                             UnitManager unitManager, MovementSystem movementSystem,
                             CameraController cameraController) {
        this.camera = camera;
        this.gameMap = gameMap;
        this.unitManager = unitManager;
        this.movementSystem = movementSystem;
        this.cameraController = cameraController;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("🖱️ CLICK DETECTED - Button: " + button + " at screen [" + screenX + ", " + screenY + "]");
        
        // Let camera controller handle right/middle click
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) {
            System.out.println("   → Right/Middle click - passing to camera");
            return false; // Pass to camera controller
        }
        
        // Handle left click for unit selection/commands
        if (button == Input.Buttons.LEFT) {
            Vector2 tilePos = screenToTile(screenX, screenY);
            int tileX = (int) tilePos.x;
            int tileY = (int) tilePos.y;
            
            System.out.println("🖱️ Left click at tile [" + tileX + ", " + tileY + "]");
            System.out.println("   Camera zoom: " + camera.zoom);
            System.out.println("   Camera position: [" + camera.position.x + ", " + camera.position.y + "]");
            
            // Check if clicked on a unit
            Unit clickedUnit = unitManager.getUnitAt(tileX, tileY);
            
            if (clickedUnit != null) {
                System.out.println("   → Found unit: " + clickedUnit.getUnitType() + " (Player: " + clickedUnit.isPlayerUnit() + ")");
                handleUnitClick(clickedUnit);
                return true;
            } else {
                System.out.println("   → No unit at this tile");
            }
            
            // If no unit clicked, check if we have a selected unit
            if (selectedUnit != null) {
                System.out.println("   → Have selected unit, trying to move/attack");
                handleTileClick(tileX, tileY);
                return true;
            } else {
                System.out.println("   → No unit selected");
            }
        }
        
        return false;
    }
    
    /**
     * Handles clicking on a unit
     */
    private void handleUnitClick(Unit clickedUnit) {
        // If clicking on an enemy unit while we have a unit selected
        if (selectedUnit != null && 
            selectedUnit.isPlayerUnit() && 
            !clickedUnit.isPlayerUnit() &&
            selectedUnit.canAttack(clickedUnit)) {
            
            // Attack the enemy
            System.out.println("⚔️ Attacking enemy unit!");
            CombatResult result = unitManager.executeCombat(selectedUnit, clickedUnit);
            
            if (result != null) {
                System.out.println("💥 " + result.getMessage());
                
                // Clean up dead units
                unitManager.cleanupDeadUnits();
                
                // Deselect after attacking
                deselectUnit();
            }
            return;
        }
        
        // If clicking on a player unit, select it
        if (clickedUnit.isPlayerUnit()) {
            selectUnit(clickedUnit);
        }
    }
    
    /**
     * Handles clicking on an empty tile
     */
    private void handleTileClick(int tileX, int tileY) {
        if (selectedUnit == null || !selectedUnit.isPlayerUnit()) {
            System.out.println("   ❌ Cannot move: no player unit selected");
            return;
        }
        
        System.out.println("   🎯 Attempting to move to [" + tileX + ", " + tileY + "]");
        System.out.println("      Unit at: [" + selectedUnit.getTileX() + ", " + selectedUnit.getTileY() + "]");
        System.out.println("      Has moved: " + selectedUnit.hasMoved());
        System.out.println("      Move speed: " + selectedUnit.getMoveSpeed());
        
        // Try to move to this tile
        if (movementSystem.canMoveToPosition(selectedUnit, tileX, tileY)) {
            System.out.println("   ✅ CAN move to this position!");
            
            boolean moved = movementSystem.moveUnit(selectedUnit, tileX, tileY);
            
            if (moved) {
                System.out.println("   ✅ Unit moved successfully to [" + tileX + ", " + tileY + "]");
                // Update visual feedback
                updateReachableTiles();
            } else {
                System.out.println("   ❌ Move failed despite canMove returning true!");
            }
        } else {
            System.out.println("   ❌ Cannot move to [" + tileX + ", " + tileY + "]");
            
            // Debug why we can't move
            if (!selectedUnit.isAlive()) {
                System.out.println("      Reason: Unit is dead");
            } else if (selectedUnit.hasMoved()) {
                System.out.println("      Reason: Unit has already moved this turn");
            } else if (!gameMap.isValidTilePosition(tileX, tileY)) {
                System.out.println("      Reason: Invalid tile position");
            } else if (!gameMap.isTileWalkable(tileX, tileY)) {
                System.out.println("      Reason: Tile is not walkable");
            } else if (movementSystem.isPositionOccupied(tileX, tileY)) {
                System.out.println("      Reason: Position occupied by another unit");
            } else {
                int distance = movementSystem.getManhattanDistance(
                    selectedUnit.getTileX(), selectedUnit.getTileY(), tileX, tileY
                );
                System.out.println("      Reason: Distance too far (distance: " + distance + ", max: " + selectedUnit.getMoveSpeed() + ")");
            }
        }
    }
    
    /**
     * Selects a unit and calculates movement/attack ranges
     */
    private void selectUnit(Unit unit) {
        // Deselect previous unit
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
        }
        
        // Select new unit
        selectedUnit = unit;
        selectedUnit.setSelected(true);
        unitManager.selectUnit(unit);
        
        System.out.println("👉 Selected: " + unit.getUnitType() + 
                         " at [" + unit.getTileX() + ", " + unit.getTileY() + "]");
        System.out.println("   HP: " + unit.getHp() + "/" + unit.getMaxHp());
        System.out.println("   Can move: " + !unit.hasMoved());
        System.out.println("   Can attack: " + !unit.hasAttacked());
        
        // Calculate reachable tiles
        updateReachableTiles();
    }
    
    /**
     * Deselects the current unit
     */
    private void deselectUnit() {
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
            selectedUnit = null;
            unitManager.deselectUnit();
            reachableTiles = null;
            attackableTiles = null;
            System.out.println("🚫 Unit deselected");
        }
    }
    
    /**
     * Updates the reachable and attackable tiles for the selected unit
     */
    private void updateReachableTiles() {
        if (selectedUnit != null) {
            reachableTiles = movementSystem.getReachablePositions(selectedUnit);
            attackableTiles = movementSystem.getAttackablePositions(selectedUnit);
        } else {
            reachableTiles = null;
            attackableTiles = null;
        }
    }
    
    /**
     * Converts screen coordinates to tile coordinates
     * FIXED: Now properly updates camera before unprojecting
     */
    private Vector2 screenToTile(int screenX, int screenY) {
        // CRITICAL FIX: Update camera matrix before unprojecting
        camera.update();
        
        // Convert screen coordinates to world coordinates
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        
        // Convert world coordinates to tile coordinates
        int tileX = (int)(worldPos.x / gameMap.getTileWidth());
        int tileY = (int)(worldPos.y / gameMap.getTileHeight());
        
        return new Vector2(tileX, tileY);
    }
    
    @Override
    public boolean keyDown(int keycode) {
        // Deselect unit with ESC or SPACE
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.SPACE) {
            deselectUnit();
            return true;
        }
        
        // End turn with ENTER or T
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.T) {
            System.out.println("🔄 Ending turn - resetting all units");
            unitManager.resetAllUnitsForNewTurn();
            deselectUnit();
            return true;
        }
        
        return false;
    }
    
    // Getters for visual feedback (for rendering)
    public Unit getSelectedUnit() {
        return selectedUnit;
    }
    
    public List<int[]> getReachableTiles() {
        return reachableTiles;
    }
    
    public List<int[]> getAttackableTiles() {
        return attackableTiles;
    }
    
    // Unused InputProcessor methods
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}