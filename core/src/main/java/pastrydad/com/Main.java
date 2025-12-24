package pastrydad.com;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    
    // Giraffe Position
    private float giraffeX = 200;
    private float giraffeY = 200;
    private float speed = 200f; // Pixels per second

    // Custom Colors
    private Color giraffeYellow;
    private Color giraffeBrown;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        
        // create a nice "Giraffe Yellow" and "Spot Brown"
        giraffeYellow = new Color(0.95f, 0.85f, 0.1f, 1); 
        giraffeBrown = new Color(0.6f, 0.3f, 0.1f, 1);
    }

    @Override
    public void render() {
        // 1. Logic: Handle Input to move the giraffe
        handleInput();

        // 2. Clear Screen (Sky Blue)
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 3. Draw the Giraffe
        shapeRenderer.begin(ShapeType.Filled);
        drawGiraffe(giraffeX, giraffeY);
        shapeRenderer.end();
    }

    private void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
            giraffeY += speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
            giraffeY -= speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            giraffeX -= speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            giraffeX += speed * deltaTime;
        }
    }

    private void drawGiraffe(float x, float y) {
        // We draw everything relative to 'x' and 'y'
        
        // --- BASE LAYER: LEGS ---
        shapeRenderer.setColor(giraffeYellow);
        // Leg 1 (Back Left)
        shapeRenderer.rect(x + 5, y, 10, 40);
        // Leg 2 (Back Right)
        shapeRenderer.rect(x + 55, y, 10, 40);
        
        // --- BODY ---
        shapeRenderer.rect(x, y + 40, 70, 40);

        // --- NECK (The most important part!) ---
        shapeRenderer.rect(x + 50, y + 80, 15, 80);

        // --- HEAD ---
        shapeRenderer.rect(x + 45, y + 150, 40, 25);

        // --- DETAILS: SPOTS (Brown) ---
        shapeRenderer.setColor(giraffeBrown);
        shapeRenderer.circle(x + 20, y + 60, 8); // Body Spot 1
        shapeRenderer.circle(x + 45, y + 55, 6); // Body Spot 2
        shapeRenderer.circle(x + 57, y + 100, 5); // Neck Spot 1
        shapeRenderer.circle(x + 57, y + 130, 4); // Neck Spot 2

        // --- DETAILS: FACE ---
        // Eye (Black)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(x + 75, y + 165, 3);
        
        // Horns/Ossicones (Small lines on top)
        shapeRenderer.rectLine(x + 55, y + 175, x + 55, y + 185, 3);
        shapeRenderer.rectLine(x + 65, y + 175, x + 65, y + 185, 3);
        // Horn tips
        shapeRenderer.circle(x + 55, y + 187, 3);
        shapeRenderer.circle(x + 65, y + 187, 3);
        
        // Tail
        shapeRenderer.setColor(giraffeBrown);
        shapeRenderer.rectLine(x, y + 70, x - 10, y + 50, 3);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}