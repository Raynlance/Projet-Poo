package pastrydad.com.combat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import pastrydad.com.entities.Unit;
import pastrydad.com.map.GameMap;

public class MovementSystem {
     /** Référence à la carte du jeu */
    private GameMap gameMap;
    
    /** Gestionnaire d'unités */
    private UnitManager unitManager;
    
    public MovementSystem(GameMap gameMap, UnitManager unitManager) {
        this.gameMap = gameMap;
        this.unitManager = unitManager;
    }
    
    /* Déplace une unité vers une position cible
    retourne true si le déplacement a réussi */
    public boolean moveUnit(Unit unit, int targetX, int targetY) {
        if (!canMoveToPosition(unit, targetX, targetY)) {
            return false;
        }
        
        // Effectuer le déplacement
        return unit.moveTo(targetX, targetY, gameMap);
    }
    
    // Vérifie si une unité peut se déplacer vers une position
     
    public boolean canMoveToPosition(Unit unit, int targetX, int targetY) {

        if (!unit.isAlive() || unit.hasMoved()) {
            return false;
        }
        // Vérifier que la position est dans les limites de la carte
        if (!gameMap.isValidTilePosition(targetX, targetY)) {
            return false;
        }
        if (!gameMap.isTileWalkable(targetX, targetY)) {
            return false;
        }
        
        // Vérifier qu'il n'y a pas déjà une giraffe à cet endroit
        if (isPositionOccupied(targetX, targetY)) {
            return false;
        }
        
        // Vérifier la distance
        int distance = getManhattanDistance(unit.getTileX(), unit.getTileY(), targetX, targetY);
        return distance <= unit.getMoveSpeed();
    }
    
    //Vérifie si une position est occupée par une unité
    public boolean isPositionOccupied(int x, int y) {
        for (Unit unit : unitManager.getAllUnits()) {
            if (unit.isAlive() && unit.getTileX() == x && unit.getTileY() == y) {
                return true;
            }
        }
        return false;
    }
    
    /* calcule toutes les positions où une unité peut se déplacer
      prend en compte le coût de mouvement des tiles
      retourne une Liste des positions accessibles [x, y]
     */
    public List<int[]> getReachablePositions(Unit unit) {
        List<int[]> reachable = new ArrayList<>();
        
        if (!unit.isAlive() || unit.hasMoved()) {
            return reachable;
        }
        
        int unitX = unit.getTileX();
        int unitY = unit.getTileY();
        int moveRange = unit.getMoveSpeed();
        
        // Parcourir toutes les positions dans le rayon de déplacement
        for (int dx = -moveRange; dx <= moveRange; dx++) {
            for (int dy = -moveRange; dy <= moveRange; dy++) {
                int targetX = unitX + dx;
                int targetY = unitY + dy;
                
                // Vérifier la distance Manhattan
                if (Math.abs(dx) + Math.abs(dy) <= moveRange) {
                    if (canMoveToPosition(unit, targetX, targetY)) {
                        reachable.add(new int[]{targetX, targetY});
                    }
                }
            }
        }
        
        return reachable;
    }
    
    // Calcule toutes les positions qu'une unité peut attaquer
    public List<int[]> getAttackablePositions(Unit unit) {
        List<int[]> attackable = new ArrayList<>();
        
        if (!unit.isAlive() || unit.hasAttacked()) {
            return attackable;
        }
        
        int unitX = unit.getTileX();
        int unitY = unit.getTileY();
        int range = unit.getRange();
        
        // Parcourir toutes les positions dans la portée d'attaque
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int targetX = unitX + dx;
                int targetY = unitY + dy;
                
                // Vérifier la distance
                int distance = Math.abs(dx) + Math.abs(dy);
                if (distance <= range && distance > 0) {
                    if (gameMap.isValidTilePosition(targetX, targetY)) {
                        attackable.add(new int[]{targetX, targetY});
                    }
                }
            }
        }
        
        return attackable;
    }
    
    // Trouve le chemin le plus court entre deux points (pathfinding simple)
     
    public List<int[]> findPath(int startX, int startY, int endX, int endY) {
        // File pour le BFS
        Queue<PathNode> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        // Nœud de départ
        PathNode start = new PathNode(startX, startY, null);
        queue.offer(start);
        visited.add(startX + "," + startY);
        
        // Directions: haut, bas, gauche, droite
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            
            // Destination atteinte
            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }
            
            // Explorer les voisins
            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                String key = newX + "," + newY;
                
                if (!visited.contains(key) && 
                    gameMap.isValidTilePosition(newX, newY) &&
                    gameMap.isTileWalkable(newX, newY) &&
                    !isPositionOccupied(newX, newY)) {
                    
                    visited.add(key);
                    queue.offer(new PathNode(newX, newY, current));
                }
            }
        }
        
        // Aucun chemin trouvé
        return null;
    }
    
    // Reconstruit le chemin à partir du nœud final
    private List<int[]> reconstructPath(PathNode endNode) {
        List<int[]> path = new ArrayList<>();
        PathNode current = endNode;
        
        while (current != null) {
            path.add(0, new int[]{current.x, current.y});
            current = current.parent;
        }
        
        return path;
    }
    
    // Calcule la distance Manhattan entre deux points
    public int getManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
    
    //Calcule la distance euclidienne entre deux points
    public double getEuclideanDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    
    //Nœud pour l'algorithme de pathfinding
    private static class PathNode {
        int x, y;
        PathNode parent;
        
        PathNode(int x, int y, PathNode parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }
}
