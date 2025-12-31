package pastrydad.com.buildings;

import java.util.HashMap;
import java.util.Map;

import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class Sawmill extends Building {

    private static final int PRODUCTION_PER_TURN = 12;

    public Sawmill() {
        super(
            "Sawmill",
            createCost(),
            2
        );
    }

    private static Map<ResourceType, Integer> createCost() {
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.GOLD, 30);
        return cost;
    }

    @Override
    protected void onConstructionComplete() {
    }

    @Override
    public void onTurn(ResourceManager resourceManager) {
        if (!constructed) return;
        resourceManager.add(ResourceType.WOOD, PRODUCTION_PER_TURN);
    }
}
