package pastrydad.com.buildings;

import java.util.HashMap;
import java.util.Map;

import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class Mine extends Building {

    private static final int PRODUCTION_PER_TURN = 10;

    public Mine() {
        super(
            "Mine",
            createCost(),
            2
        );
    }

    private static Map<ResourceType, Integer> createCost() {
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.WOOD, 50);
        return cost;
    }

    @Override
    protected void onConstructionComplete() {
        // Optional: log, notify, or bonus
    }

    @Override
    public void onTurn(ResourceManager resourceManager) {
        if (!constructed) return;
        resourceManager.add(ResourceType.STONE, PRODUCTION_PER_TURN);
    }
}
