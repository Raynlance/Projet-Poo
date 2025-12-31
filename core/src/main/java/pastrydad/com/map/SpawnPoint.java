package pastrydad.com.map;

public class SpawnPoint {
 
    private double x;
    private double y;
    
  
    private int tileX;
    private int tileY;
    

    private String spawnType;  
    private int spawnRate;
    private String enemyType;    
    private int maxSpawns; 
    
   
    private int currentCounter;   
    private int spawnedCount; 
    private boolean isActive;
  
    public SpawnPoint(double x, double y) {
        this.x = x;
        this.y = y;
        
       
        this.tileX = (int)(x / 32);
        this.tileY = (int)(y / 32);
        
       
        this.spawnType = "continuous";
        this.spawnRate = 3;
        this.enemyType = "basic";
        this.maxSpawns = -1;  
        
      
        this.currentCounter = 0;
        this.spawnedCount = 0;
        this.isActive = true;
    }
    
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getTileX() {
        return tileX;
    }
    
    public int getTileY() {
        return tileY;
    }
    
    public String getSpawnType() {
        return spawnType;
    }
    
    public int getSpawnRate() {
        return spawnRate;
    }
    
    public String getEnemyType() {
        return enemyType;
    }
    
    public int getMaxSpawns() {
        return maxSpawns;
    }
    
    public int getCurrentCounter() {
        return currentCounter;
    }
    
    public int getSpawnedCount() {
        return spawnedCount;
    }
    
    public boolean isActive() {
        return isActive;
    }
    

    
    
    public void setSpawnType(String spawnType) {
        this.spawnType = spawnType;
    }
    
    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }
    
    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }
    
    public void setMaxSpawns(int maxSpawns) {
        this.maxSpawns = maxSpawns;
    }
  
    public void setTileSize(int tileSize) {
        this.tileX = (int)(x / tileSize);
        this.tileY = (int)(y / tileSize);
    }
    
  
    public void incrementCounter() {
        if (isActive) {
            currentCounter++;
        }
    }
    
   
    public boolean shouldSpawn() {
       
        if (!isActive) {
            return false;
        }
        
     
        if (maxSpawns > 0 && spawnedCount >= maxSpawns) {
            isActive = false;
            return false;
        }
        
      
        switch (spawnType) {
            case "continuous":
                
                return currentCounter >= spawnRate;
                
            case "random":
                
                return currentCounter >= spawnRate && Math.random() < 0.5;
                
            case "wave":
            
                return currentCounter >= spawnRate;
                
            default:
               
                return currentCounter >= spawnRate;
        }
    }
    
  
    public void resetCounter() {
        currentCounter = 0;
    }
    

    public void recordSpawn() {
        spawnedCount++;
        
     
        if (maxSpawns > 0 && spawnedCount >= maxSpawns) {
            isActive = false;
            System.out.println("Spawn point at [" + tileX + "," + tileY + 
                             "] deactivated (max spawns reached)");
        }
    }
    
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
   
    public void reset() {
        currentCounter = 0;
        spawnedCount = 0;
        isActive = true;
    }
    
   
    @Override
    public String toString() {
        return "SpawnPoint[" + tileX + "," + tileY + "] " +
               "type=" + enemyType + 
               " rate=" + spawnRate + 
               " spawned=" + spawnedCount + "/" + 
               (maxSpawns == -1 ? "∞" : maxSpawns) +
               " active=" + isActive;
    }
}