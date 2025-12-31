package pastrydad.com.map;

import java.util.ArrayList;
import java.util.List;


public class GameMap {

    private int mapWidth;       
    private int mapHeight;       
    private int tileWidth;     
    private int tileHeight;     
    
 
    private Tile[][] tiles;                     
    private List<BuildingSpot> buildingSpots;    
    private List<SpawnPoint> spawnPoints;      
    
  
    public GameMap(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        
        this.tiles = new Tile[mapHeight][mapWidth];
        this.buildingSpots = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();
    }
    
   
    
    public Tile getTileAt(int x, int y) {
        if (isValidTilePosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }
    
   
    public boolean isValidTilePosition(int x, int y) {
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }
    
  
    public boolean isTileWalkable(int x, int y) {
        Tile tile = getTileAt(x, y);
        return tile != null && tile.isWalkable();
    }
    
   
    public int getTileMovementCost(int x, int y) {
        Tile tile = getTileAt(x, y);
        return tile != null ? tile.getMovementCost() : 99;
    }
    

   
    public List<BuildingSpot> getAllBuildingSpots() {
        return new ArrayList<>(buildingSpots);
    }
    
   
    public BuildingSpot getBuildingSpotAt(double x, double y) {
        for (BuildingSpot spot : buildingSpots) {
            if (spot.containsPoint(x, y)) {
                return spot;
            }
        }
        return null;
    }
    
   
    public BuildingSpot getBuildingSpotAtTile(int tileX, int tileY) {
        for (BuildingSpot spot : buildingSpots) {
            if (spot.containsTile(tileX, tileY)) {
                return spot;
            }
        }
        return null;
    }
   
    public List<BuildingSpot> getStartingBuildingSpots() {
        List<BuildingSpot> startingSpots = new ArrayList<>();
        for (BuildingSpot spot : buildingSpots) {
            if (spot.isStartingBuilding()) {
                startingSpots.add(spot);
            }
        }
        return startingSpots;
    }
   
    public List<BuildingSpot> getAvailableBuildingSpots() {
        List<BuildingSpot> availableSpots = new ArrayList<>();
        for (BuildingSpot spot : buildingSpots) {
            if (spot.canBuild() && !spot.isOccupied()) {
                availableSpots.add(spot);
            }
        }
        return availableSpots;
    }
    
    public void occupyBuildingSpot(int tileX, int tileY, String buildingType, Object building) {
        BuildingSpot spot = getBuildingSpotAtTile(tileX, tileY);
        if (spot != null) {
            spot.occupySpot(buildingType, building);
        }
    }
   
    public void clearBuildingSpot(int tileX, int tileY) {
        BuildingSpot spot = getBuildingSpotAtTile(tileX, tileY);
        if (spot != null) {
            spot.clearSpot();
        }
    }
   
    public List<SpawnPoint> getAllSpawnPoints() {
        return new ArrayList<>(spawnPoints);
    }
    
   
    public List<SpawnPoint> getActiveSpawnPoints() {
        List<SpawnPoint> activeSpawns = new ArrayList<>();
        for (SpawnPoint spawn : spawnPoints) {
            if (spawn.isActive()) {
                activeSpawns.add(spawn);
            }
        }
        return activeSpawns;
    }
    
    public void updateSpawnPoints() {
        for (SpawnPoint spawn : spawnPoints) {
            spawn.incrementCounter();
        }
    }
    
   
    public List<SpawnPoint> getSpawnPointsReadyToSpawn() {
        List<SpawnPoint> readySpawns = new ArrayList<>();
        for (SpawnPoint spawn : spawnPoints) {
            if (spawn.shouldSpawn()) {
                readySpawns.add(spawn);
            }
        }
        return readySpawns;
    }
    
    
    public int[] pixelToTile(double pixelX, double pixelY) {
        int tileX = (int)(pixelX / tileWidth);
        int tileY = (int)(pixelY / tileHeight);
        return new int[]{tileX, tileY};
    }
    
   
    public double[] tileToPixel(int tileX, int tileY) {
        double pixelX = tileX * tileWidth + (tileWidth / 2.0);
        double pixelY = tileY * tileHeight + (tileHeight / 2.0);
        return new double[]{pixelX, pixelY};
    }
    
  
    public double[] tileToPixelTopLeft(int tileX, int tileY) {
        double pixelX = tileX * tileWidth;
        double pixelY = tileY * tileHeight;
        return new double[]{pixelX, pixelY};
    }
    
    
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public int getMapHeight() {
        return mapHeight;
    }
    
    public int getTileWidth() {
        return tileWidth;
    }
    
    public int getTileHeight() {
        return tileHeight;
    }
    
    public Tile[][] getTiles() {
        return tiles;
    }
    
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }
    
    public List<BuildingSpot> getBuildingSpots() {
        return buildingSpots;
    }
    
    public void setBuildingSpots(List<BuildingSpot> buildingSpots) {
        this.buildingSpots = buildingSpots;
    }
    
    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
    
    public void setSpawnPoints(List<SpawnPoint> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
    
    public void printMapInfo() {
        System.out.println("\n=== MAP INFO ===");
        System.out.println("Size: " + mapWidth + "x" + mapHeight + " tiles");
        System.out.println("Tile size: " + tileWidth + "x" + tileHeight + " pixels");
        System.out.println("Building spots: " + buildingSpots.size());
        System.out.println("Spawn points: " + spawnPoints.size());
        
        System.out.println("\n=== BUILDING SPOTS ===");
        for (BuildingSpot spot : buildingSpots) {
            System.out.println(spot);
        }
        
        System.out.println("\n=== SPAWN POINTS ===");
        for (SpawnPoint spawn : spawnPoints) {
            System.out.println(spawn);
        }
    }
   
    public void printTileStatistics() {
        int walkableCount = 0;
        int obstacleCount = 0;
        
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Tile tile = tiles[y][x];
                if (tile != null) {
                    if (tile.isWalkable()) {
                        walkableCount++;
                    } else {
                        obstacleCount++;
                    }
                }
            }
        }
        
        int total = mapWidth * mapHeight;
        System.out.println("\n=== TILE STATISTICS ===");
        System.out.println("Total tiles: " + total);
        System.out.println("Walkable: " + walkableCount + " (" + 
                         (100 * walkableCount / total) + "%)");
        System.out.println("Obstacles: " + obstacleCount + " (" + 
                         (100 * obstacleCount / total) + "%)");
    }
}