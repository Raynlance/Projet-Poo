package pastrydad.com.buildings;

import java.util.HashMap;
import java.util.Map;

import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class Farm extends Building {

    private static final int PRODUCTION_PER_TURN = 15;

    public Farm(int tileX, int tileY) {
        super(
            "Farm",
            createCost(),
            2,
            tileX,
            tileY
        );
        this.texturePath = "buildings/farm.png";
    }

    private static Map<ResourceType, Integer> createCost() {
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.WOOD, 40);
        return cost;
    }

    @Override
    protected void onConstructionComplete() {
        System.out.println("Farm construction terminée à la position [" + tileX + "," + tileY + "]");
    }

    @Override
    public void onTurn(ResourceManager resourceManager) {
        if (!constructed) return;
        resourceManager.add(ResourceType.FOOD, PRODUCTION_PER_TURN);
    }
}