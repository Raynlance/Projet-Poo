package pastrydad.com.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.List; 

public class MapRenderer {
    private GameMap gameMap;
    private TilesetManager tilesetManager;
    
    public MapRenderer(GameMap gameMap) {
        this.gameMap = gameMap;
        this.tilesetManager = gameMap.getTilesetManager();
    }
    
    public void render(SpriteBatch batch, OrthographicCamera camera) {
       
        int startX = Math.max(0, (int)(camera.position.x - camera.viewportWidth / 2) / gameMap.getTileWidth());
        int endX = Math.min(gameMap.getMapWidth(), (int)(camera.position.x + camera.viewportWidth / 2) / gameMap.getTileWidth() + 1);
        int startY = Math.max(0, (int)(camera.position.y - camera.viewportHeight / 2) / gameMap.getTileHeight());
        int endY = Math.min(gameMap.getMapHeight(), (int)(camera.position.y + camera.viewportHeight / 2) / gameMap.getTileHeight() + 1);
        
        batch.begin();
        
        // Render all  tiles
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                
                List<Tile> tileList = gameMap.getTilesAt(x, y);
                for (Tile tile : tileList) {
                    if (tile != null && tile.getTileId() != 0) {
                        TextureRegion region = tilesetManager.getTextureRegion(tile.getTileId());
                        
                        if (region != null) {
                            float drawX = x * gameMap.getTileWidth();
                            float drawY = y * gameMap.getTileHeight();
                            
                            batch.draw(region, drawX, drawY, 
                                     gameMap.getTileWidth(), 
                                     gameMap.getTileHeight());
                        }
                    }
                }
            }
        }
        
        batch.end();
    }
    
   
    public void renderAll(SpriteBatch batch) {
        batch.begin();
        
        for (int y = 0; y < gameMap.getMapHeight(); y++) {
            for (int x = 0; x < gameMap.getMapWidth(); x++) {
                
                List<Tile> tileList = gameMap.getTilesAt(x, y);
                
                for (Tile tile : tileList) {
                    if (tile != null && tile.getTileId() != 0) {
                        TextureRegion region = tilesetManager.getTextureRegion(tile.getTileId());
                        
                        if (region != null) {
                            float drawX = x * gameMap.getTileWidth();
                            float drawY = y * gameMap.getTileHeight();
                            
                            batch.draw(region, drawX, drawY, 
                                     gameMap.getTileWidth(), 
                                     gameMap.getTileHeight());
                        }
                    }
                }
            }
        }
        
        batch.end();
    }
    



    
    public void dispose() {
        if (tilesetManager != null) {
            tilesetManager.dispose();
        }
    }
}