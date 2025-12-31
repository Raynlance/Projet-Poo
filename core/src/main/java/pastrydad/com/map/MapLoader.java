package pastrydad.com.map;


import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;


import com.badlogic.gdx.maps.MapProperties;


public class MapLoader {
    
  
    public static GameMap loadMap(String tmxFilePath) {
        try {
            
            File file = new File(tmxFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            
           
            Element mapElement = doc.getDocumentElement();
            int mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            int mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
            int tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));
            
            System.out.println("Loading map: " + mapWidth + "x" + mapHeight + 
                             " tiles (" + tileWidth + "x" + tileHeight + "px)");
            
           
            Map<Integer, TileProperties> tilePropertiesMap = loadTileProperties(doc);
            
            
            Tile[][] tiles = loadAllLayers(doc, mapWidth, mapHeight, tilePropertiesMap);
            
            
            List<BuildingSpot> buildingSpots = loadBuildingSpots(doc, tileWidth);
            
           
            List<SpawnPoint> spawnPoints = loadSpawnPoints(doc, tileWidth);
            
            
            GameMap gameMap = new GameMap(mapWidth, mapHeight, tileWidth, tileHeight);
            gameMap.setTiles(tiles);
            gameMap.setBuildingSpots(buildingSpots);
            gameMap.setSpawnPoints(spawnPoints);
            
            System.out.println("Map loaded successfully!");
            System.out.println("- Building spots: " + buildingSpots.size());
            System.out.println("- Spawn points: " + spawnPoints.size());
            
            return gameMap;
            
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    
    private static Map<Integer, TileProperties> loadTileProperties(Document doc) {
        Map<Integer, TileProperties> tilePropertiesMap = new HashMap<>();
        
        NodeList tilesetNodes = doc.getElementsByTagName("tileset");
        if (tilesetNodes.getLength() == 0) {
            System.out.println("No tileset found in map");
            return tilePropertiesMap;
        }
        
        Element tilesetElement = (Element) tilesetNodes.item(0);
        int firstGid = Integer.parseInt(tilesetElement.getAttribute("firstgid"));
        
        NodeList tileNodes = tilesetElement.getElementsByTagName("tile");
        
        for (int i = 0; i < tileNodes.getLength(); i++) {
            Element tileElement = (Element) tileNodes.item(i);
            int tileId = Integer.parseInt(tileElement.getAttribute("id")) + firstGid;
            
            TileProperties props = new TileProperties();
            
            String type = tileElement.getAttribute("type");
            if (!type.isEmpty()) {
                props.type = type;
            }
            
            NodeList propertiesNodes = tileElement.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                Element propertiesElement = (Element) propertiesNodes.item(0);
                NodeList propertyNodes = propertiesElement.getElementsByTagName("property");
                
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Element propertyElement = (Element) propertyNodes.item(j);
                    String name = propertyElement.getAttribute("name");
                    String value = propertyElement.getAttribute("value");
                    
                    switch (name) {
                        case "walkable":
                            props.walkable = value.equals("true");
                            break;
                        case "movement_cost":
                            props.movementCost = Integer.parseInt(value);
                            break;
                        case "type":
                            props.type = value;
                            break;
                    }
                }
            }
            
            tilePropertiesMap.put(tileId, props);
        }
        
        System.out.println("Loaded properties for " + tilePropertiesMap.size() + " tile types");
        return tilePropertiesMap;
    }
    
  
    private static Tile[][] loadAllLayers(Document doc, int mapWidth, int mapHeight,
                                         Map<Integer, TileProperties> tilePropertiesMap) {
        Tile[][] tiles = new Tile[mapHeight][mapWidth];
        
        NodeList layerNodes = doc.getElementsByTagName("layer");
        List<Element> tileLayers = new ArrayList<>();
        
        for (int i = 0; i < layerNodes.getLength(); i++) {
            tileLayers.add((Element) layerNodes.item(i));
        }
        
        if (tileLayers.isEmpty()) {
            System.err.println("No tile layers found!");
            return tiles;
        }
        
        String[] layerPriority = {"ground", "lhidhabl3oulya", "hajat"};
        List<Element> orderedLayers = new ArrayList<>();
        
        for (String layerName : layerPriority) {
            for (Element layer : tileLayers) {
                if (layer.getAttribute("name").equals(layerName)) {
                    orderedLayers.add(layer);
                    System.out.println("Found layer: " + layerName);
                    break;
                }
            }
        }
        
        for (Element layer : tileLayers) {
            if (!orderedLayers.contains(layer)) {
                String name = layer.getAttribute("name");
                System.out.println("Found additional layer: " + name);
                orderedLayers.add(layer);
            }
        }
        
        if (orderedLayers.isEmpty()) {
            System.err.println("No valid layers found!");
            return tiles;
        }
        
        Element baseLayer = orderedLayers.get(0);
        System.out.println("Loading base layer: " + baseLayer.getAttribute("name"));
        tiles = parseLayerData(baseLayer, mapWidth, mapHeight, tilePropertiesMap);
        
        for (int i = 1; i < orderedLayers.size(); i++) {
            Element layer = orderedLayers.get(i);
            String layerName = layer.getAttribute("name");
            
            System.out.println("Processing layer " + (i+1) + "/" + orderedLayers.size() + ": " + layerName);
            
            Tile[][] layerTiles = parseLayerData(layer, mapWidth, mapHeight, tilePropertiesMap);
            
            boolean isDecorationLayer = layerName.equals("hajat");
            boolean isBridgeLayer = layerName.equals("lhidhabl3oulya");
            
            if (isDecorationLayer) {
                System.out.println("  → Decoration layer (only walkable tiles override)");
            } else if (isBridgeLayer) {
                System.out.println("  → Bridge/structural layer (all tiles override)");
            }
            
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    Tile layerTile = layerTiles[y][x];
                    
                    if (layerTile != null && layerTile.getTileId() != 0) {
                        
                        if (isDecorationLayer) {
                            if (layerTile.isWalkable()) {
                                tiles[y][x] = layerTile;
                            }
                        } else {
                            tiles[y][x] = layerTile;
                        }
                    }
                }
            }
        }
        
        System.out.println("All " + orderedLayers.size() + " layers merged successfully");
        return tiles;
    }
    
  
    private static Tile[][] parseLayerData(Element layerElement, int mapWidth, int mapHeight,
                                          Map<Integer, TileProperties> tilePropertiesMap) {
        Tile[][] tiles = new Tile[mapHeight][mapWidth];
        
        NodeList dataNodes = layerElement.getElementsByTagName("data");
        if (dataNodes.getLength() == 0) {
            System.err.println("No data in layer: " + layerElement.getAttribute("name"));
            return tiles;
        }
        
        Element dataElement = (Element) dataNodes.item(0);
        String encoding = dataElement.getAttribute("encoding");
        
        if (!encoding.equals("csv")) {
            System.err.println("Only CSV encoding supported! Current: " + encoding);
            return tiles;
        }
        
        String csvData = dataElement.getTextContent().trim();
        String[] rows = csvData.split("\n");
        
        for (int y = 0; y < Math.min(rows.length, mapHeight); y++) {
            String[] tileIds = rows[y].trim().split(",");
            for (int x = 0; x < Math.min(tileIds.length, mapWidth); x++) {
                int tileId = Integer.parseInt(tileIds[x].trim());
                
                
                MapProperties libgdxProps = new MapProperties();
                
                if (tilePropertiesMap.containsKey(tileId)) {
                   
                    TileProperties parsedProps = tilePropertiesMap.get(tileId);
                    
                  
                    libgdxProps.put("walkability", parsedProps.walkable);
                    libgdxProps.put("type", parsedProps.type);
                    
                   
                    libgdxProps.put("movementCost", String.valueOf(parsedProps.movementCost));
                    libgdxProps.put("defenseBonus", String.valueOf(parsedProps.defenseBonus));
                } else {
                    
                    libgdxProps.put("walkability", true);
                    libgdxProps.put("type", "unknown");
                    libgdxProps.put("movementCost", "1");
                    libgdxProps.put("defenseBonus", "0");
                }
                
              
                Tile tile = new Tile(x, y, tileId, libgdxProps);
              
                
                tiles[y][x] = tile;
            }
        }
        
        return tiles;
    }
    
 
    private static List<BuildingSpot> loadBuildingSpots(Document doc, int tileSize) {
        List<BuildingSpot> buildingSpots = new ArrayList<>();
        
        NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");
        Element buildingsLayer = null;
        
        for (int i = 0; i < objectGroupNodes.getLength(); i++) {
            Element objectGroupElement = (Element) objectGroupNodes.item(i);
            if (objectGroupElement.getAttribute("name").equals("buildings")) {
                buildingsLayer = objectGroupElement;
                break;
            }
        }
        
        if (buildingsLayer == null) {
            System.out.println("No buildings layer found");
            return buildingSpots;
        }
        
        NodeList objectNodes = buildingsLayer.getElementsByTagName("object");
        
        for (int i = 0; i < objectNodes.getLength(); i++) {
            Element objectElement = (Element) objectNodes.item(i);
            
            double x = Double.parseDouble(objectElement.getAttribute("x"));
            double y = Double.parseDouble(objectElement.getAttribute("y"));
            double width = Double.parseDouble(objectElement.getAttribute("width"));
            double height = Double.parseDouble(objectElement.getAttribute("height"));
            
            BuildingSpot spot = new BuildingSpot(x, y, width, height);
            spot.setTileSize(tileSize);
            
            NodeList propertiesNodes = objectElement.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                Element propertiesElement = (Element) propertiesNodes.item(0);
                NodeList propertyNodes = propertiesElement.getElementsByTagName("property");
                
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Element propertyElement = (Element) propertyNodes.item(j);
                    String name = propertyElement.getAttribute("name");
                    String value = propertyElement.getAttribute("value");
                    
                    switch (name) {
                        case "building_type":
                            spot.setBuildingType(value);
                            break;
                        case "is_starting_building":
                            spot.setStartingBuilding(value.equals("true"));
                            break;
                        case "can_build":
                            spot.setCanBuild(value.equals("true"));
                            break;
                    }
                }
            }
            
            buildingSpots.add(spot);
        }
        
        return buildingSpots;
    }
    
 
    private static List<SpawnPoint> loadSpawnPoints(Document doc, int tileSize) {
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        
        NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");
        Element spawnLayer = null;
        
        for (int i = 0; i < objectGroupNodes.getLength(); i++) {
            Element objectGroupElement = (Element) objectGroupNodes.item(i);
            if (objectGroupElement.getAttribute("name").equals("spawn_points")) {
                spawnLayer = objectGroupElement;
                break;
            }
        }
        
        if (spawnLayer == null) {
            System.out.println("No spawn_points layer found");
            return spawnPoints;
        }
        
        NodeList objectNodes = spawnLayer.getElementsByTagName("object");
        
        for (int i = 0; i < objectNodes.getLength(); i++) {
            Element objectElement = (Element) objectNodes.item(i);
            
            double x = Double.parseDouble(objectElement.getAttribute("x"));
            double y = Double.parseDouble(objectElement.getAttribute("y"));
            
            SpawnPoint spawn = new SpawnPoint(x, y);
            spawn.setTileSize(tileSize);
            
            NodeList propertiesNodes = objectElement.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                Element propertiesElement = (Element) propertiesNodes.item(0);
                NodeList propertyNodes = propertiesElement.getElementsByTagName("property");
                
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Element propertyElement = (Element) propertyNodes.item(j);
                    String name = propertyElement.getAttribute("name");
                    String value = propertyElement.getAttribute("value");
                    
                    switch (name) {
                        case "spawn_type":
                            spawn.setSpawnType(value);
                            break;
                        case "spawn_rate":
                            spawn.setSpawnRate(Integer.parseInt(value));
                            break;
                        case "enemy_type":
                            spawn.setEnemyType(value);
                            break;
                        case "max_spawns":
                            spawn.setMaxSpawns(Integer.parseInt(value));
                            break;
                    }
                }
            }
            
            spawnPoints.add(spawn);
        }
        
        return spawnPoints;
    }
    

    private static class TileProperties {
        String type = "plain";
        boolean walkable = true;
        int movementCost = 1;
        int defenseBonus = 0;
    }
}