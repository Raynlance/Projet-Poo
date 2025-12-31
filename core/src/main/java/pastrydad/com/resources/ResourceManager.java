package pastrydad.com.resources;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private final Map<ResourceType, Integer> resources;

    public ResourceManager() {
        resources = new HashMap<>();

        // Initialize all resource types with 0
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    /**
     * Add a positive amount of a resource.
     */
    public void add(ResourceType type, int amount) {
        if (amount <= 0) {
            return;
        }
        resources.put(type, resources.get(type) + amount);
    }

    /**
     * Get current amount of a resource.
     */
    public int get(ResourceType type) {
        return resources.get(type);
    }

    /**
     * Check if the player can afford a given cost.
     */
    public boolean canAfford(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType type = entry.getKey();
            int required = entry.getValue();

            if (resources.get(type) < required) {
                return false;
            }
        }
        return true;
    }

    /**
     * Spend resources if affordable.
     * @ return true if payment succeeded, false otherwise
     */
    public boolean spend(Map<ResourceType, Integer> cost) {
        if (!canAfford(cost)) {
            return false;
        }

        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();
            resources.put(type, resources.get(type) - amount);
        }
        return true;
    }

    /**
     * Debug / UI helper: returns a copy of resources.
     */
    public Map<ResourceType, Integer> getAll() {
        return new HashMap<>(resources);
    }
}
