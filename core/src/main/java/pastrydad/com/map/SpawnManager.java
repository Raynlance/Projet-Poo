package pastrydad.com.map;

import java.util.List;
import java.util.Random;

import pastrydad.com.combat.UnitManager;

public class SpawnManager {
    
    private GameMap gameMap;
    private UnitManager unitManager;
    private Random random;
    
    private int spawnInterval; // Spawn tous les X tours
    private int currentTurn;
    
    public SpawnManager(GameMap gameMap, UnitManager unitManager) {
        this.gameMap = gameMap;
        this.unitManager = unitManager;
        this.random = new Random();
        this.spawnInterval = 3; // Spawn tous les 3 tours par défaut
        this.currentTurn = 0;
    }
    
    // Appelé à chaque tour ennemi
    public void onEnemyTurn(int turnNumber) {
        this.currentTurn = turnNumber;
        
        // Mettre à jour les compteurs des spawn points
        gameMap.updateSpawnPoints();
        
        // Récupérer les spawn points prêts
        List<SpawnPoint> readySpawns = gameMap.getSpawnPointsReadyToSpawn();
        
        System.out.println("🎯 " + readySpawns.size() + " spawn points prêts");
        
        // Spawner des ennemis
        for (SpawnPoint spawn : readySpawns) {
            spawnEnemyAt(spawn);
            spawn.resetCounter();
            spawn.recordSpawn();
        }
    }
    
    // Spawn un ennemi à un spawn point
    private void spawnEnemyAt(SpawnPoint spawn) {
        int tileX = spawn.getTileX();
        int tileY = spawn.getTileY();
        
        // Vérifier que la position est libre
        if (unitManager.getUnitAt(tileX, tileY) != null) {
            System.out.println("⚠️ Spawn point occupé à [" + tileX + ", " + tileY + "]");
            return;
        }
        
        // Créer un type d'ennemi aléatoire
        int enemyType = random.nextInt(3);
        
        switch (enemyType) {
            case 0:
                unitManager.createWhiskGiraffe(tileX, tileY, false);
                System.out.println("👹 WhiskGiraffe spawné à [" + tileX + ", " + tileY + "]");
                break;
            case 1:
                unitManager.createPanGiraffe(tileX, tileY, false);
                System.out.println("👹 PanGiraffe spawné à [" + tileX + ", " + tileY + "]");
                break;
            case 2:
                unitManager.createRollingPinGiraffe(tileX, tileY, false);
                System.out.println("👹 RollingPinGiraffe spawné à [" + tileX + ", " + tileY + "]");
                break;
        }
    }
    
    public void setSpawnInterval(int interval) {
        this.spawnInterval = interval;
    }
    
    public int getSpawnInterval() {
        return spawnInterval;
    }
}