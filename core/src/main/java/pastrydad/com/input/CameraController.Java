package pastrydad.com.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import pastrydad.com.map.GameMap;


public class CameraController extends InputAdapter {
    private OrthographicCamera camera;
    private GameMap map;
    
    private float minZoom = 0.5f;   
    private float maxZoom = 3.0f;    
    private float zoomSpeed = 0.1f;  
    
    private boolean isPanning = false;
    private Vector2 lastTouchPos = new Vector2();
    
    private float keyboardPanSpeed = 300f; 
    
  
    private float mapWidthPixels;
    private float mapHeightPixels;
    
 
    public CameraController(OrthographicCamera camera, GameMap map) {
        this.camera = camera;
        this.map = map;
        
        this.mapWidthPixels = map.getMapWidth() * map.getTileWidth();
        this.mapHeightPixels = map.getMapHeight() * map.getTileHeight();
    }
    

    public void update(float deltaTime) {
    
        handleKeyboardPanning(deltaTime);
        
        clampCameraToMap();
        
        camera.update();
    }
    


    private void handleKeyboardPanning(float deltaTime) {
        float speed = keyboardPanSpeed * deltaTime * camera.zoom;
        
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += speed;
        }
    }
    

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("🔧 CameraController.touchDown: button=" + button);
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) {
            isPanning = true;
            lastTouchPos.set(screenX, screenY);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) {
            isPanning = false;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isPanning) {
          
            float deltaX = (screenX - lastTouchPos.x) * camera.zoom;
            float deltaY = (screenY - lastTouchPos.y) * camera.zoom;
            
          
            camera.position.x -= deltaX;
            camera.position.y += deltaY;
            
          
            lastTouchPos.set(screenX, screenY);
            
            return true;
        }
        
        return false;
    }
    
  
    @Override
    public boolean scrolled(float amountX, float amountY) {
      
        float zoomAmount = amountY * zoomSpeed;
        camera.zoom += zoomAmount;
        
       
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, camera.zoom));
        
        return true;
    }
    
  
    private void clampCameraToMap() {
       
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;
        
      
        float minX = effectiveViewportWidth / 2f;
        float maxX = mapWidthPixels - effectiveViewportWidth / 2f;
        float minY = effectiveViewportHeight / 2f;
        float maxY = mapHeightPixels - effectiveViewportHeight / 2f;
        
    
        camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
        camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));
    }
    
   
    public Vector2 screenToWorld(int screenX, int screenY) {
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(worldPos.x, worldPos.y);
    }
 
    public Vector2 screenToTile(int screenX, int screenY) {
        Vector2 worldPos = screenToWorld(screenX, screenY);
        int tileX = (int)(worldPos.x / map.getTileWidth());
        int tileY = (int)(worldPos.y / map.getTileHeight());
        return new Vector2(tileX, tileY);
    }
    
 
    public void centerOnTile(int tileX, int tileY) {
        float pixelX = (tileX + 0.5f) * map.getTileWidth();
        float pixelY = (tileY + 0.5f) * map.getTileHeight();
        camera.position.set(pixelX, pixelY, 0);
    }
  
    public void resetCamera() {
        camera.position.set(mapWidthPixels / 2f, mapHeightPixels / 2f, 0);
        camera.zoom = 1.0f;
    }
    
    
    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }
    
    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }
    
    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }
    
    public void setKeyboardPanSpeed(float keyboardPanSpeed) {
        this.keyboardPanSpeed = keyboardPanSpeed;
    }
    
    public boolean isPanning() {
        return isPanning;
    }
}