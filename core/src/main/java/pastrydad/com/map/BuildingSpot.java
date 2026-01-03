package pastrydad.com.map;

public class BuildingSpot {
  
    private double x;
    private double y;
    private double width;
    private double height;
    
    private int tileX;
    private int tileY;
    private int tileSize;

    private String buildingType;        
    private boolean isStartingBuilding; 
    private boolean canBuild;         
    private boolean isOccupied;      
    
    private Object currentBuilding;    
    

    public BuildingSpot(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileSize = 32; // Default, will be set properly later
        
        this.tileX = (int)(x / this.tileSize);
        this.tileY = (int)(y / this.tileSize);
        
        this.buildingType = "empty";
        this.isStartingBuilding = false;
        this.canBuild = true;
        this.isOccupied = false;
        this.currentBuilding = null;
    }
    
   
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public int getTileX() {
        return tileX;
    }
    
    public int getTileY() {
        return tileY;
    }
    
    public String getBuildingType() {
        return buildingType;
    }
    
    public boolean isStartingBuilding() {
        return isStartingBuilding;
    }
    

    public boolean canBuild() {
        return canBuild && !isOccupied;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public Object getCurrentBuilding() {
        return currentBuilding;
    }
    

    
    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }
    
    public void setStartingBuilding(boolean isStartingBuilding) {
        this.isStartingBuilding = isStartingBuilding;
    }
    
    public void setCanBuild(boolean canBuild) {
        this.canBuild = canBuild;
    }
    

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
        // Recalculate tile coordinates with correct tile size
        this.tileX = (int)(x / tileSize);
        this.tileY = (int)(y / tileSize);
    }
    
   
    public void occupySpot(String buildingType, Object building) {
        this.buildingType = buildingType;
        this.currentBuilding = building;
        this.isOccupied = true;
    }
  
    public void clearSpot() {
        this.buildingType = "empty";
        this.currentBuilding = null;
        this.isOccupied = false;
    }
    

   
    public boolean containsPoint(double pointX, double pointY) {
        return pointX >= x && pointX < (x + width) &&
               pointY >= y && pointY < (y + height);
    }
    
 
    public boolean containsTile(int tileX, int tileY) {
        // Calculate how many tiles this spot covers
        int spotTileWidth = (int)Math.ceil(width / tileSize);
        int spotTileHeight = (int)Math.ceil(height / tileSize);
        
        // Check if the given tile is within this spot's tile range
        boolean inRange = tileX >= this.tileX && tileX < (this.tileX + spotTileWidth) &&
                          tileY >= this.tileY && tileY < (this.tileY + spotTileHeight);
        
        // DEBUG OUTPUT
        if (inRange) {
            System.out.println("DEBUG BuildingSpot.containsTile: Tile [" + tileX + "," + tileY + 
                             "] IS in spot [" + this.tileX + "," + this.tileY + "] -> [" + 
                             (this.tileX + spotTileWidth - 1) + "," + (this.tileY + spotTileHeight - 1) + "]");
        }
        
        return inRange;
    }
    

    @Override
    public String toString() {
        return "BuildingSpot[" + tileX + "," + tileY + "] " +
               "pixel[" + (int)x + "," + (int)y + "] " +
               "size[" + (int)width + "x" + (int)height + "] " +
               "type=" + buildingType + 
               " occupied=" + isOccupied + 
               " canBuild=" + canBuild;
    }
}