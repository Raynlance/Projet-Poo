package pastrydad.com.combat;

import java.util.HashMap;
import java.util.Map;

import pastrydad.com.entities.PanGiraffe;
import pastrydad.com.entities.RollingPinGiraffe;
import pastrydad.com.entities.Unit;
import pastrydad.com.entities.WhiskGiraffe;
import pastrydad.com.map.GameMap;
import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

// Factory pour créer des unités avec vérification des ressources
public class UnitFactory {
    
    private UnitManager unitManager;
    private ResourceManager resourceManager;
    private GameMap gameMap;
    
    public UnitFactory(UnitManager unitManager, ResourceManager resourceManager, GameMap gameMap) {
        this.unitManager = unitManager;
        this.resourceManager = resourceManager;
        this.gameMap = gameMap;
    }
    
    /* Tente de créer une Whisk Girafe
     retourne L'unité créée, ou null si pas assez de ressources */
    public WhiskGiraffe createWhiskGiraffe(int tileX, int tileY, boolean isPlayer) {
        // Vérifier que la position est valide et libre
        if (!canSpawnAt(tileX, tileY)) {
            System.out.println("❌ Impossible de créer l'unité ici");
            return null;
        }
        
        // Créer temporairement pour obtenir les coûts
        WhiskGiraffe temp = new WhiskGiraffe(tileX, tileY, isPlayer);
        Map<ResourceType, Integer> cost = getUnitCost(temp);
        
        // Vérifier les ressources (seulement pour le joueur)
        if (isPlayer && !resourceManager.canAfford(cost)) {
            System.out.println("❌ Pas assez de ressources pour Whisk Girafe");
            System.out.println("   Coût: " + formatCost(cost));
            System.out.println("   Ressources actuelles: " + formatResources());
            return null;
        }
        
        // Dépenser les ressources (seulement pour le joueur)
        if (isPlayer) {
            resourceManager.spend(cost);
            System.out.println("✅ Whisk Girafe créée! Ressources dépensées: " + formatCost(cost));
        }
        
        // Créer l'unité via le UnitManager
        return unitManager.createWhiskGiraffe(tileX, tileY, isPlayer);
    }
    
    // Tente de créer une Pan Girafe
    public PanGiraffe createPanGiraffe(int tileX, int tileY, boolean isPlayer) {
        if (!canSpawnAt(tileX, tileY)) {
            System.out.println("❌ Impossible de créer l'unité ici");
            return null;
        }
        
        PanGiraffe temp = new PanGiraffe(tileX, tileY, isPlayer);
        Map<ResourceType, Integer> cost = getUnitCost(temp);
        
        if (isPlayer && !resourceManager.canAfford(cost)) {
            System.out.println("❌ Pas assez de ressources pour Pan Girafe");
            System.out.println("   Coût: " + formatCost(cost));
            System.out.println("   Ressources actuelles: " + formatResources());
            return null;
        }
        
        if (isPlayer) {
            resourceManager.spend(cost);
            System.out.println("✅ Pan Girafe créée! Ressources dépensées: " + formatCost(cost));
        }
        
        return unitManager.createPanGiraffe(tileX, tileY, isPlayer);
    }
    
    //Tente de créer une RollingPin Girafe
    public RollingPinGiraffe createRollingPinGiraffe(int tileX, int tileY, boolean isPlayer) {
        if (!canSpawnAt(tileX, tileY)) {
            System.out.println("❌ Impossible de créer l'unité ici");
            return null;
        }
        
        RollingPinGiraffe temp = new RollingPinGiraffe(tileX, tileY, isPlayer);
        Map<ResourceType, Integer> cost = getUnitCost(temp);
        
        if (isPlayer && !resourceManager.canAfford(cost)) {
            System.out.println("❌ Pas assez de ressources pour RollingPin Girafe");
            System.out.println("   Coût: " + formatCost(cost));
            System.out.println("   Ressources actuelles: " + formatResources());
            return null;
        }
        
        if (isPlayer) {
            resourceManager.spend(cost);
            System.out.println("✅ RollingPin Girafe créée! Ressources dépensées: " + formatCost(cost));
        }
        
        return unitManager.createRollingPinGiraffe(tileX, tileY, isPlayer);
    }
    
    // Vérifie si on peut spawn une unité à une position
    private boolean canSpawnAt(int tileX, int tileY) {
        // Vérifier que la position est valide
        if (!gameMap.isValidTilePosition(tileX, tileY)) {
            return false;
        }
        
        // Vérifier que le tile est marchable
        if (!gameMap.isTileWalkable(tileX, tileY)) {
            return false;
        }
        
        // Vérifier qu'il n'y a pas déjà une unité
        if (unitManager.getUnitAt(tileX, tileY) != null) {
            return false;
        }
        
        return true;
    }
    
    // Convertit les coûts d'une unité en Map pour ResourceManager
    private Map<ResourceType, Integer> getUnitCost(Unit unit) {
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.GOLD, unit.getGoldCost());
        cost.put(ResourceType.FOOD, unit.getFoodCost());
        cost.put(ResourceType.STONE, unit.getStoneCost());
        return cost;
    }
    
    //Vérifie si le joueur peut se permettre une unité
    public boolean canAffordWhiskGiraffe() {
        WhiskGiraffe temp = new WhiskGiraffe(0, 0, true);
        return resourceManager.canAfford(getUnitCost(temp));
    }
    
    public boolean canAffordPanGiraffe() {
        PanGiraffe temp = new PanGiraffe(0, 0, true);
        return resourceManager.canAfford(getUnitCost(temp));
    }
    
    public boolean canAffordRollingPinGiraffe() {
        RollingPinGiraffe temp = new RollingPinGiraffe(0, 0, true);
        return resourceManager.canAfford(getUnitCost(temp));
    }
    
    // Formate les coûts pour affichage
    private String formatCost(Map<ResourceType, Integer> cost) {
        return String.format("%d Gold, %d Food, %d Stone",
            cost.get(ResourceType.GOLD),
            cost.get(ResourceType.FOOD),
            cost.get(ResourceType.STONE)
        );
    }
    
    //Formate les ressources actuelles pour affichage
    private String formatResources() {
        Map<ResourceType, Integer> resources = resourceManager.getAll();
        return String.format("%d Gold, %d Food, %d Stone",
            resources.get(ResourceType.GOLD),
            resources.get(ResourceType.FOOD),
            resources.get(ResourceType.STONE)
        );
    }
    
    // Affiche les coûts de toutes les unités
    public void printUnitCosts() {
        System.out.println("\n💰 COÛTS DES UNITÉS:");
        System.out.println("─".repeat(50));
        
        WhiskGiraffe whisk = new WhiskGiraffe(0, 0, true);
        System.out.printf("%-20s : %s%n", "Whisk Girafe", formatCost(getUnitCost(whisk)));
        
        PanGiraffe pan = new PanGiraffe(0, 0, true);
        System.out.printf("%-20s : %s%n", "Pan Girafe", formatCost(getUnitCost(pan)));
        
        RollingPinGiraffe rolling = new RollingPinGiraffe(0, 0, true);
        System.out.printf("%-20s : %s%n", "RollingPin Girafe", formatCost(getUnitCost(rolling)));
    }
}
