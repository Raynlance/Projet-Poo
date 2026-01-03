package pastrydad.com.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.w3c.dom.*;
import java.util.*;

public class TilesetManager {
    private Map<Integer, TextureRegion> tileTextures;
    private int tileWidth;
    private int tileHeight;
    
    public TilesetManager(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileTextures = new HashMap<>();
    }
    

   public void loadTileset(Document doc) {
    NodeList tilesetNodes = doc.getElementsByTagName("tileset");
    
    for (int i = 0; i < tilesetNodes.getLength(); i++) {
        Element tilesetElement = (Element) tilesetNodes.item(i);
        loadSingleTileset(tilesetElement);
    }
    
    System.out.println("Loaded " + tileTextures.size() + " tile textures");
}
    
    private void loadSingleTileset(Element tilesetElement) {
        int firstGid = Integer.parseInt(tilesetElement.getAttribute("firstgid"));
        int tileCount = Integer.parseInt(tilesetElement.getAttribute("tilecount"));
        int columns = Integer.parseInt(tilesetElement.getAttribute("columns"));
        
        NodeList imageNodes = tilesetElement.getElementsByTagName("image");
        if (imageNodes.getLength() == 0) {
            System.err.println("No image found in tileset!");
            return;
        }
        
        Element imageElement = (Element) imageNodes.item(0);
        String imagePath = imageElement.getAttribute("source");
      
        // Clean up path
        imagePath = imagePath.replace("../", "").replace("\\", "/");
        
        System.out.println("Loading tileset: " + imagePath);
        System.out.println("  First GID: " + firstGid + ", Tiles: " + tileCount + ", Columns: " + columns);
        
        try {
            Texture tilesetTexture = new Texture(imagePath);
            
            TextureRegion[][] regions = TextureRegion.split(
                tilesetTexture,
                tileWidth,
                tileHeight
            );
           
            int tileId = firstGid;
            for (int row = 0; row < regions.length; row++) {
                for (int col = 0; col < regions[row].length; col++) {
                    if (tileId - firstGid < tileCount) {
                        regions[row][col].flip(false, true);
                        
                        tileTextures.put(tileId, regions[row][col]);
                        tileId++;
                    }
                }
            }
            
            System.out.println("  Successfully loaded " + (tileId - firstGid) + " tiles");
            
        } catch (Exception e) {
            System.err.println("Error loading tileset image: " + imagePath);
            e.printStackTrace();
        }
    }
    
   
    public TextureRegion getTextureRegion(int tileId) {
        return tileTextures.get(tileId);
    }
    
    
    public boolean hasTexture(int tileId) {
        return tileTextures.containsKey(tileId);
    }
    
    
    public void dispose() {
        Set<Texture> disposedTextures = new HashSet<>();
        for (TextureRegion region : tileTextures.values()) {
            Texture texture = region.getTexture();
            if (!disposedTextures.contains(texture)) {
                texture.dispose();
                disposedTextures.add(texture);
            }
        }
        tileTextures.clear();
    }
}