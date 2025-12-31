package pastrydad.com.buildings;

import java.util.Map;

import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public abstract class Building {

    protected String name;
    protected Map<ResourceType, Integer> cost;
    protected int buildTime;
    protected int remainingBuildTime;
    protected boolean constructed;

    public Building(String name,
                    Map<ResourceType, Integer> cost,
                    int buildTime) {

        this.name = name;
        this.cost = cost;
        this.buildTime = buildTime;
        this.remainingBuildTime = buildTime;
        this.constructed = false;
    }

    public void buildStep() {
        if (constructed) return;

        remainingBuildTime--;

        if (remainingBuildTime <= 0) {
            constructed = true;
            onConstructionComplete();
        }
    }

    protected abstract void onConstructionComplete();

    public abstract void onTurn(ResourceManager resourceManager);

    public boolean isConstructed() {
        return constructed;
    }

    public String getName() {
        return name;
    }

    public Map<ResourceType, Integer> getCost() {
        return cost;
    }
}
