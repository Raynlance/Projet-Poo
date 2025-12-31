package pastrydad.com.map;

import com.badlogic.gdx.maps.MapProperties;


public class Tile {
    
  
    private int gridX;               
    private int gridY;              
    

    private int tileId;
    private String type;
    
    
    private boolean walkable;
    private int movementCost;

   
    public Tile(int gridX, int gridY, int tileId, MapProperties props) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.tileId = tileId;
      
        this.type = props.get("type", "unknown", String.class);

        this.walkable = props.get("walkability", true, Boolean.class);

       
        String costStr = props.get("movementCost", "1", String.class);
        try {
            this.movementCost = Integer.parseInt(costStr);
        } catch (NumberFormatException e) {
            this.movementCost = 1; 
        }

    }
    

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
    
    public int getTileId() {
        return tileId;
    }

    public String getType() {
        return type;
    }
    
    public boolean isWalkable() {
        return walkable;
    }
    
    public int getMovementCost() {
        return movementCost;
    }
    
    


    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Tile[" + gridX + "," + gridY + "] " +
               "type=" + type + 
               " walkable=" + walkable + 
               " cost=" + movementCost;
    }
}