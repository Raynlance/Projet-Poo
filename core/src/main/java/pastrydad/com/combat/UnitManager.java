package pastrydad.com.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pastrydad.com.entities.PanGiraffe;
import pastrydad.com.entities.RollingPinGiraffe;
import pastrydad.com.entities.Unit;
import pastrydad.com.entities.WhiskGiraffe;
import pastrydad.com.map.GameMap;

public class UnitManager {
    // Liste de toutes les unités en jeu 
    private List<Unit> allUnits;
    // Liste des unités du joueur
    private List<Unit> playerUnits;
    // Liste des unités ennemies 
    private List<Unit> enemyUnits;
    // Unité actuellement sélectionnée 
    private Unit selectedUnit;
    // Système de combat 
    private CombatSystem combatSystem;
    private GameMap gameMap;
    
    // ✅ SCORE SYSTEM
    private int playerScore;
    private static final int SCORE_PER_KILL = 50;  // Points par ennemi tué
    
    public UnitManager(GameMap gameMap) {
        this.gameMap = gameMap;
        this.allUnits = new ArrayList<>();
        this.playerUnits = new ArrayList<>();
        this.enemyUnits = new ArrayList<>();
        this.combatSystem = new CombatSystem();
        this.selectedUnit = null;
        this.playerScore = 0;  // ✅ Initialiser le score
    }
    
    //creation des unite

    //cree une girafe fouet
    public WhiskGiraffe createWhiskGiraffe(int tileX, int tileY, boolean isPlayer) {
        WhiskGiraffe unit = new WhiskGiraffe(tileX, tileY, isPlayer);
        addUnit(unit);
        return unit;
    }
    
    //cree une giraffe caserole
    public PanGiraffe createPanGiraffe(int tileX, int tileY, boolean isPlayer) {
        PanGiraffe unit = new PanGiraffe(tileX, tileY, isPlayer);
        addUnit(unit);
        return unit;
    }
    
    //Crée une girafe rouleau a patisserie
    public RollingPinGiraffe createRollingPinGiraffe(int tileX, int tileY, boolean isPlayer) {
        RollingPinGiraffe unit = new RollingPinGiraffe(tileX, tileY, isPlayer);
        addUnit(unit);
        return unit;
    }
    
    // Ajoute une unité aux listes
    private void addUnit(Unit unit) {
        allUnits.add(unit);
        
        if (unit.isPlayerUnit()) {
            playerUnits.add(unit);
        } else {
            enemyUnits.add(unit);
        }
        
        // Charger la texture
        unit.loadTexture();
    }
    
    // suprimme unite
    
    // Retire une unité du jeu
    public void removeUnit(Unit unit) {
        allUnits.remove(unit);
        playerUnits.remove(unit);
        enemyUnits.remove(unit);
        
        if (selectedUnit == unit) {
            selectedUnit = null;
        }
        
        unit.dispose();
    }
    
    // Nettoie toutes les unités mortes
    public void cleanupDeadUnits() {
        Iterator<Unit> iterator = allUnits.iterator();
        while (iterator.hasNext()) {
            Unit unit = iterator.next();
            if (!unit.isAlive()) {
                // ✅ AJOUTER DES POINTS SI C'EST UN ENNEMI TUÉ
                if (!unit.isPlayerUnit()) {
                    playerScore += SCORE_PER_KILL;
                    System.out.println("🎯 Ennemi tué! +" + SCORE_PER_KILL + " points (Total: " + playerScore + ")");
                }
                
                iterator.remove();
                playerUnits.remove(unit);
                enemyUnits.remove(unit);
                
                if (selectedUnit == unit) {
                    selectedUnit = null;
                }
                
                unit.dispose();
            }
        }
    }
        
    //Sélectionne une unité
    public void selectUnit(Unit unit) {
        // Désélectionner l'ancienne unité
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
        }
        
        // Sélectionner la nouvelle
        selectedUnit = unit;
        if (selectedUnit != null) {
            selectedUnit.setSelected(true);
        }
    }
    
    //Désélectionne l'unité actuelle
    public void deselectUnit() {
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
            selectedUnit = null;
        }
    }
    
    // Trouve l'unité à une position donnée 
    public Unit getUnitAt(int tileX, int tileY) {
        for (Unit unit : allUnits) {
            if (unit.isAlive() && unit.getTileX() == tileX && unit.getTileY() == tileY) {
                return unit;
            }
        }
        return null;
    }
    // Trouve l'unité à une position en pixels
    public Unit getUnitAtPixel(double pixelX, double pixelY) {
        int[] tilePos = gameMap.pixelToTile(pixelX, pixelY);
        return getUnitAt(tilePos[0], tilePos[1]);
    }
        
    // Fait attaquer l'unité sélectionnée sur une cible
    public CombatResult attackWithSelected(Unit target) {
        if (selectedUnit == null || !selectedUnit.canAttack(target)) {
            return null;
        }
        
        return combatSystem.executeCombat(selectedUnit, target);
    }
    
    // Exécute un combat entre deux unités 
    public CombatResult executeCombat(Unit attacker, Unit target) {
        return combatSystem.executeCombat(attacker, target);
    }
        
    // Réinitialise toutes les unités pour un nouveau tour 
    public void resetAllUnitsForNewTurn() {
        for (Unit unit : allUnits) {
            unit.resetTurn();
        }
    }
    
    // Réinitialise les unités du joueur
    public void resetPlayerUnits() {
        for (Unit unit : playerUnits) {
            unit.resetTurn();
        }
    }
    
    // Réinitialise les unités ennemies
    public void resetEnemyUnits() {
        for (Unit unit : enemyUnits) {
            unit.resetTurn();
        }
    }
    
    
    // Dessine toutes les unités
    public void renderAll(SpriteBatch batch) {
        // Dessiner d'abord les unités non sélectionnées
        for (Unit unit : allUnits) {
            if (!unit.isSelected()) {
                unit.render(batch, gameMap);
            }
        }
        
        // Dessiner l'unité sélectionnée en dernier (au-dessus)
        if (selectedUnit != null && selectedUnit.isAlive()) {
            selectedUnit.render(batch, gameMap);
        }
    }
    
    // Libère toutes les ressources
    public void dispose() {
        for (Unit unit : allUnits) {
            unit.dispose();
        }
        allUnits.clear();
        playerUnits.clear();
        enemyUnits.clear();
    }
    
    // ✅ SCORE GETTERS
    public int getPlayerScore() {
        return playerScore;
    }
    
    public void addScore(int points) {
        playerScore += points;
        System.out.println("🎯 +" + points + " points! (Total: " + playerScore + ")");
    }
    
    public void resetScore() {
        playerScore = 0;
    }
    
    //getters
    
    public List<Unit> getAllUnits() {
        return new ArrayList<>(allUnits);
    }
    public List<Unit> getPlayerUnits() {
        return new ArrayList<>(playerUnits);
    }
    public List<Unit> getEnemyUnits() {
        return new ArrayList<>(enemyUnits);
    }
    public List<Unit> getAlivePlayerUnits() {
        return playerUnits.stream()
            .filter(Unit::isAlive)
            .collect(Collectors.toList());
    }
    public List<Unit> getAliveEnemyUnits() {
        return enemyUnits.stream()
            .filter(Unit::isAlive)
            .collect(Collectors.toList());
    }
    public Unit getSelectedUnit() {
        return selectedUnit;
    }
    public CombatSystem getCombatSystem() {
        return combatSystem;
    }
    public GameMap getGameMap() {
        return gameMap;
    }
    
    // Compte le nombre d'unités vivantes du joueur
    public int getPlayerUnitCount() {
        return (int) playerUnits.stream().filter(Unit::isAlive).count();
    }
    
    // Compte le nombre d'unités vivantes ennemies
    public int getEnemyUnitCount() {
        return (int) enemyUnits.stream().filter(Unit::isAlive).count();
    }
    
    // Vérifie si le joueur a gagné (tous les ennemis morts)
    public boolean hasPlayerWon() {
        return getEnemyUnitCount() == 0 && getPlayerUnitCount() > 0;
    }
    
    // Vérifie si le joueur a perdu (toutes ses unités mortes)
    public boolean hasPlayerLost() {
        return getPlayerUnitCount() == 0 && getEnemyUnitCount() > 0;
    }
}