package pastrydad.com.combat;

import pastrydad.com.map.SpawnManager;
import pastrydad.com.buildings.BuildingManager;

public class TurnManager {
    
    private boolean isPlayerTurn;
    private int turnNumber;
    private UnitManager unitManager;
    private EnemyAI enemyAI;
    private SpawnManager spawnManager;
    private BuildingManager buildingManager;  
    
    public TurnManager(UnitManager unitManager, EnemyAI enemyAI) {
        this.unitManager = unitManager;
        this.enemyAI = enemyAI;
        this.isPlayerTurn = true;
        this.turnNumber = 1;
    }
    
    public void setSpawnManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }
    
    // ✅ NOUVELLE MÉTHODE
    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }
    
    public void endPlayerTurn() {
        if (!isPlayerTurn) return;
        
        System.out.println("\n=== FIN DU TOUR JOUEUR ===");
        
        // ✅ GÉNÉRER LES RESSOURCES DES BUILDINGS
        if (buildingManager != null) {
            buildingManager.processTurn();
            System.out.println("🏭 Buildings ont produit des ressources!");
        }
        
        isPlayerTurn = false;
        
        // Réinitialiser les unités joueur pour le prochain tour
        unitManager.resetPlayerUnits();
        
        // Lancer le tour ennemi
        startEnemyTurn();
    }
    
    private void startEnemyTurn() {
        System.out.println("\n=== TOUR ENNEMI " + turnNumber + " ===");
        
        // Réinitialiser les unités ennemies
        unitManager.resetEnemyUnits();
        
        // Spawner de nouveaux ennemis
        if (spawnManager != null) {
            spawnManager.onEnemyTurn(turnNumber);
        }
        
        // L'IA joue
        enemyAI.executeTurn();
        
        // Nettoyer les unités mortes
        unitManager.cleanupDeadUnits();
        
        // Retour au tour joueur
        endEnemyTurn();
    }
    
    private void endEnemyTurn() {
        System.out.println("=== FIN DU TOUR ENNEMI ===\n");
        
        isPlayerTurn = true;
        turnNumber++;
        
        System.out.println("=== TOUR JOUEUR " + turnNumber + " ===");
    }
    
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
}