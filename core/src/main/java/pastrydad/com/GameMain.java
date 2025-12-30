package pastrydad.com;

import com.badlogic.gdx.Game;
import pastrydad.com.ui.MenuScreen;

public class GameMain extends Game {
    
    @Override
    public void create() {
        System.out.println("🚀 GameMain - Démarrage du jeu...");
        setScreen(new MenuScreen(this));
        System.out.println("✅ MenuScreen lancé !");
    }
    
    @Override
    public void dispose() {
        super.dispose();
        System.out.println("👋 GameMain - Arrêt du jeu");
    }
}