package pastrydad.com.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import pastrydad.com.combat.UnitManager;
import pastrydad.com.entities.Unit;
import pastrydad.com.map.GameMap;
import pastrydad.com.combat.MovementSystem;

public class GameInputProcessor extends InputAdapter {
    
    private UnitManager unitManager;
    private GameMap gameMap;
    private OrthographicCamera camera;
    private MovementSystem movementSystem;
    
    public GameInputProcessor(UnitManager unitManager, GameMap gameMap, 
                             OrthographicCamera camera, MovementSystem movementSystem) {
        this.unitManager = unitManager;
        this.gameMap = gameMap;
        this.camera = camera;
        this.movementSystem = movementSystem;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Convertir les coordonnées écran en coordonnées monde
        System.out.println("🔧 DEBUG: touchDown appelé ! screenX=" + screenX + " screenY=" + screenY);
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        
        // Convertir en coordonnées de tuiles
        int[] tilePos = gameMap.pixelToTile(worldCoords.x, worldCoords.y);
        int tileX = tilePos[0];
        int tileY = tilePos[1];
        
        System.out.println("🖱️ Clic sur la tuile [" + tileX + ", " + tileY + "]");
        
        // Chercher une unité à cette position
        Unit clickedUnit = unitManager.getUnitAt(tileX, tileY);
        
        if (clickedUnit != null && clickedUnit.isPlayerUnit()) {
            // Sélectionner l'unité du joueur
            unitManager.selectUnit(clickedUnit);
            System.out.println("✅ Unité sélectionnée : " + clickedUnit.getUnitType());
        } else if (unitManager.getSelectedUnit() != null) {
            // Déplacer l'unité sélectionnée
            Unit selected = unitManager.getSelectedUnit();
            
            if (clickedUnit == null && gameMap.isTileWalkable(tileX, tileY)) {
                // Déplacer vers une case vide
                boolean moved = movementSystem.moveUnit(selected, tileX, tileY);
                if (moved) {
                    System.out.println("🚶 Unité déplacée vers [" + tileX + ", " + tileY + "]");
                } else {
                    System.out.println("❌ Impossible de se déplacer là");
                }
            }
        }
        
        return true;
    }
}