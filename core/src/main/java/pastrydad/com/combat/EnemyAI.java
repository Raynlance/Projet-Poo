package pastrydad.com.combat;

import java.util.List;
import java.util.Random;

import pastrydad.com.entities.Unit;

public class EnemyAI {
    // Gestionnaire d'unités 
    private UnitManager unitManager;
    // Système de mouvement 
    private MovementSystem movementSystem;
    // Système de combat 
    private CombatSystem combatSystem;
    // Générateur aléatoire 
    private Random random;
    
    // Agressivité de l'IA (0.0 = défensif, 1.0 = très agressif) 
    private float aggressiveness;
    
    public EnemyAI(UnitManager unitManager, MovementSystem movementSystem, CombatSystem combatSystem) {
        this.unitManager = unitManager;
        this.movementSystem = movementSystem;
        this.combatSystem = combatSystem;
        this.random = new Random();
        this.aggressiveness = 0.7f; // Par défaut: plutôt agressif
    }
    
    // Exécute le tour de l'IA
    public void executeTurn() {
       // System.out.println(" TOUR DE L'IA ");
        
        List<Unit> enemies = unitManager.getAliveEnemyUnits();
        
        for (Unit enemy : enemies) {
            processEnemyUnit(enemy);
        }
        
      //  System.out.println("FIN DU TOUR IA ");
    }
    
    //Traite une unité ennemie
    private void processEnemyUnit(Unit enemy) {
        if (!enemy.isAlive()) {
            return;
        }
        
        // 1. Trouver la cible la plus intéressante
        Unit target = findBestTarget(enemy);
        
        if (target == null) {
            System.out.println(enemy.getUnitType() + " ne trouve aucune cible.");
            return;
        }
        
        // 2. Décider de l'action
        if (canAttackTarget(enemy, target)) {
            // À portée -> attaquer
            attackTarget(enemy, target);
        } else if (!enemy.hasMoved()) {
            // Pas à portée -> se rapprocher
            moveTowardsTarget(enemy, target);
            
            // Réessayer d'attaquer après le déplacement
            if (canAttackTarget(enemy, target)) {
                attackTarget(enemy, target);
            }
        }
    }
    
    //Trouve la meilleure cible pour une unité ennemie (Distance, HP restants,Dangerosité)
    private Unit findBestTarget(Unit enemy) {
        List<Unit> playerUnits = unitManager.getAlivePlayerUnits();
        
        if (playerUnits.isEmpty()) {
            return null;
        }
        
        Unit bestTarget = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        
        for (Unit player : playerUnits) {
            float score = calculateTargetScore(enemy, player);
            
            if (score > bestScore) {
                bestScore = score;
                bestTarget = player;
            }
        }
        
        return bestTarget;
    }
    
    /* Calcule un score pour chaque cible potentielle
      Plus le score est élevé, plus la cible est intéressante */
    private float calculateTargetScore(Unit enemy, Unit target) {
        float score = 0;
        
        // 1. Distance (plus proche = mieux)
        int distance = movementSystem.getManhattanDistance(
            enemy.getTileX(), enemy.getTileY(),
            target.getTileX(), target.getTileY()
        );
        score += (20 - distance) * 10; // Bonus de proximité
        
        // 2. HP faibles (finir les ennemis blessés)
        float hpPercent = (float) target.getHp() / target.getMaxHp();
        score += (1.0f - hpPercent) * 50; // Bonus pour cible faible
        
        // 3. Dangerosité (prioriser les attaquants forts)
        score += target.getAttack() * 2;
        
        // 4. Possibilité de kill
        if (combatSystem.canOneShot(enemy, target)) {
            score += 100; // Gros bonus si on peut tuer en un coup
        }
        
        // 5. Facteur d'agressivité
        score *= aggressiveness;
        
        return score;
    }
    
    // Vérifie si l'ennemi peut attaquer la cible
    private boolean canAttackTarget(Unit enemy, Unit target) {
        return enemy.canAttack(target);
    }
    
    //Attaque une cible
     
    private void attackTarget(Unit enemy, Unit target) {
        if (!enemy.hasAttacked()) {
            CombatResult result = combatSystem.executeCombat(enemy, target);
            
            if (result != null) {
                System.out.println("IA: " + result.getMessage());
            }
        }
    }
    
    // Déplace l'ennemi vers la cible
    private void moveTowardsTarget(Unit enemy, Unit target) {
        if (enemy.hasMoved()) {
            return;
        }
        
        // Trouver la meilleure position de déplacement
        int[] bestMove = findBestMovePosition(enemy, target);
        
        if (bestMove != null) {
            boolean moved = movementSystem.moveUnit(enemy, bestMove[0], bestMove[1]);
            
            if (moved) {
                System.out.println("IA: " + enemy.getUnitType() + 
                                 " se déplace vers " + target.getUnitType() +
                                 " en (" + bestMove[0] + ", " + bestMove[1] + ")");
            }
        }
    }
    
    //Trouve la meilleure position de déplacement pour se rapprocher d'une cible
    private int[] findBestMovePosition(Unit enemy, Unit target) {
        List<int[]> reachablePositions = movementSystem.getReachablePositions(enemy);
        
        if (reachablePositions.isEmpty()) {
            return null;
        }
        
        int[] bestPosition = null;
        int shortestDistance = Integer.MAX_VALUE;
        
        // Trouver la position qui nous rapproche le plus de la cible
        for (int[] pos : reachablePositions) {
            int distance = movementSystem.getManhattanDistance(
                pos[0], pos[1],
                target.getTileX(), target.getTileY()
            );
            
            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestPosition = pos;
            }
        }
        
        return bestPosition;
    }
    
    // IA plus défensive: reste en arrière et attaque à distance
    
    public void executeDefensiveTurn() {
       // System.out.println(" TOUR DÉFENSIF DE L'IA ");
        
        List<Unit> enemies = unitManager.getAliveEnemyUnits();
        
        for (Unit enemy : enemies) {
            Unit target = findBestTarget(enemy);
            
            if (target != null && canAttackTarget(enemy, target)) {
                // Attaquer seulement si à portée
                attackTarget(enemy, target);
            } else if (!enemy.hasMoved() && target != null) {
                // Se déplacer uniquement si on est trop proche
                int distance = movementSystem.getManhattanDistance(
                    enemy.getTileX(), enemy.getTileY(),
                    target.getTileX(), target.getTileY()
                );
                
                if (distance < 3 && enemy.getRange() > 2) {
                    // S'éloigner si on est une unité à distance
                    moveAwayFromTarget(enemy, target);
                }
            }
        }
        
     //   System.out.println(" FIN DU TOUR DÉFENSIF ");
    }
    
    //Déplace l'unité pour s'éloigner d'une cible
    private void moveAwayFromTarget(Unit enemy, Unit target) {
        List<int[]> reachablePositions = movementSystem.getReachablePositions(enemy);
        
        int[] bestPosition = null;
        int longestDistance = Integer.MIN_VALUE;
        
        for (int[] pos : reachablePositions) {
            int distance = movementSystem.getManhattanDistance(
                pos[0], pos[1],
                target.getTileX(), target.getTileY()
            );
            
            if (distance > longestDistance) {
                longestDistance = distance;
                bestPosition = pos;
            }
        }
        
        if (bestPosition != null) {
            movementSystem.moveUnit(enemy, bestPosition[0], bestPosition[1]);
        }
    }
    
    
    // Définit le niveau d'agressivité de l'IA
    public void setAggressiveness(float aggressiveness) {
        this.aggressiveness = Math.max(0.0f, Math.min(1.0f, aggressiveness));
    }
    
    public float getAggressiveness() {
        return aggressiveness;
    }
}
