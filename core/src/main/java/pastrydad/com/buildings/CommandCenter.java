package pastrydad.com.buildings;

import java.util.HashMap;
import java.util.Map;

import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public class CommandCenter extends Building {

    public CommandCenter() {
        super(
            "Command Center",
            createCost(),
            3 // build time in turns
        );
    }

    private static Map<ResourceType, Integer> createCost() {
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.WOOD, 100);
        cost.put(ResourceType.STONE, 100);
        return cost;
    }

    @Override
    protected void onConstructionComplete() {
        // Future: unlock buildings or units
    }

    @Override
    public void onTurn(ResourceManager resourceManager) {
        // Command center does not produce resources
    }
}
