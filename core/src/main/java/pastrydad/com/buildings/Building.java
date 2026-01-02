package pastrydad.com.buildings;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pastrydad.com.map.GameMap;
import pastrydad.com.resources.ResourceManager;
import pastrydad.com.resources.ResourceType;

public abstract class Building {

    protected String name;
    protected Map<ResourceType, Integer> cost;
    protected int buildTime;
    protected int remainingBuildTime;
    protected boolean constructed;
    
    // Position du bâtiment sur la carte
    protected int tileX;
    protected int tileY;
    
    // Graphique
    protected Texture texture;
    protected String texturePath;

    public Building(String name,
                    Map<ResourceType, Integer> cost,
                    int buildTime,
                    int tileX,
                    int tileY) {

        this.name = name;
        this.cost = cost;
        this.buildTime = buildTime;
        this.remainingBuildTime = buildTime;
        this.constructed = false;
        this.tileX = tileX;
        this.tileY = tileY;
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

    // Charge la texture du bâtiment
    @SuppressWarnings("CallToPrintStackTrace")
    public void loadTexture() {
        if (texturePath != null && !texturePath.isEmpty()) {
            try {
                this.texture = new Texture(texturePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de la texture: " + texturePath);
                e.printStackTrace();
            }
        }
    }
    
    // Dessine le bâtiment à l'écran
    public void render(SpriteBatch batch, GameMap gameMap) {
        if (texture != null) {
            double[] pixelPos = gameMap.tileToPixelTopLeft(tileX, tileY);
            float x = (float) pixelPos[0];
            float y = (float) pixelPos[1];
            
            batch.draw(texture, x, y, gameMap.getTileWidth(), gameMap.getTileHeight());
        }
    }
    
    // Libère les ressources graphiques
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    // Getters
    public boolean isConstructed() {
        return constructed;
    }

    public String getName() {
        return name;
    }

    public Map<ResourceType, Integer> getCost() {
        return cost;
    }
    
    public int getTileX() {
        return tileX;
    }
    
    public int getTileY() {
        return tileY;
    }
    
    public int getRemainingBuildTime() {
        return remainingBuildTime;
    }
    
    public String getTexturePath() {
        return texturePath;
    }
}