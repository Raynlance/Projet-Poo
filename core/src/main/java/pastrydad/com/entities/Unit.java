package pastrydad.com.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pastrydad.com.map.GameMap;

/*base pour tout type de girafe
statistiques de combat (attaque, défense, portée)
 déplacement sur la carte
 l'état de l'unité (vivante/morte)
  Rendu visuel*/
 
public abstract class Unit {
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int range;
    protected int moveSpeed;
    
    // cout selon les resource donc a modifier    
    protected int goldCost;
    protected int foodCost;
    protected int stoneCost; 
    
    //position 
    protected int tileX;
    protected int tileY;
    //destination du deplacement 
    protected int targetX;
    protected int targetY;

    
    //etat de l'unité
    protected boolean isAlive;
    protected boolean hasMoved;
    protected boolean hasAttacked;
    protected boolean isSelected;
    protected boolean isPlayerUnit;
    
    //type d'arme utiliser par la girafe
    protected WeaponType weaponType;
    
    //graphique
    protected Texture texture;
    protected String texturePath;
    
    
    // Constructeur de base pour une unité
    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public Unit(int tileX, int tileY, boolean isPlayer) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.targetX = tileX;
        this.targetY = tileY;
        this.isPlayerUnit = isPlayer;
        this.isAlive = true;
        this.hasMoved = false;
        this.hasAttacked = false;
        this.isSelected = false;
        
        // Les valeurs spécifiques seront définies dans les sous-classes
        initializeStats();
    }
        
    //initialize les stats de chaque type de giraffe
    protected abstract void initializeStats();
    
    //retourne le type de giraffe
    public abstract String getUnitType();
    
    //attaque specifique a chaque unite
    public abstract boolean useSpecialAbility(Unit target);
        
    // attaque l'enemie
    public int attack(Unit target) {
        if (!canAttack(target)) {
            return 0;
        }
        
        // Formule de dégâts
        int baseDamage = this.attack - (target.defense / 2);
        double randomFactor = 0.8 + (Math.random() * 0.4); 
        int finalDamage = (int)(baseDamage * randomFactor);
        finalDamage = Math.max(1, finalDamage);
       // Bonus d'arme selon le type
        finalDamage += getWeaponBonus();
        
        // Infliger les dégâts
        target.takeDamage(finalDamage);
        
        // Marquer que l'unité a attaqué
        this.hasAttacked = true;
        
        return finalDamage;
    }
    
    //impact des dega reçu
    public void takeDamage(int damage) {
        this.hp -= damage;
        
        if (this.hp <= 0) {
            this.hp = 0;
            this.isAlive = false;
            onDeath();
        }
    }
    
    public boolean canAttack(Unit target) {
        // Vérifications de base
        if (!this.isAlive || !target.isAlive) {
            return false;
        }
        
        if (this.hasAttacked) {
            return false;
        }
        
        if (this.isPlayerUnit == target.isPlayerUnit) {
            return false; // Pas d'attaque alliée
        }
        
        // Vérifier la portée
        int distance = getManhattanDistance(this.tileX, this.tileY, target.tileX, target.tileY);
        return distance <= this.range;
    }
    
    //Calcule le bonus de dégâts selon l'arme
    protected int getWeaponBonus() {
        if (weaponType == null) {
            return 0;
        }
        
        return switch (weaponType) {
            case WHISK -> 3;
            case PAN -> 5;
            case ROLLING_PIN -> 4;
            default -> 0;
        };
    }
    
    
    // Déplace l'unité vers une nouvelle position
    public boolean moveTo(int targetX, int targetY, GameMap gameMap) {
        if (!canMoveTo(targetX, targetY, gameMap)) {
            return false;
        }
        
        this.tileX = targetX;
        this.tileY = targetY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.hasMoved = true;
        
        return true;
    }
    
    // Vérifie si l'unité peut se déplacer vers une position
      public boolean canMoveTo(int targetX, int targetY, GameMap gameMap) {
        if (!this.isAlive || this.hasMoved) {
            return false;
        }
        
       // Vérifier que la position est valide
        if (!gameMap.isValidTilePosition(targetX, targetY)) {
            return false;
        }
        
        // Vérifier que le tile est marchable (utilise le système du Member 1)
        if (!gameMap.isTileWalkable(targetX, targetY)) {
            return false;
        }
        // Vérifier la distance (Manhattan pour mouvement en grille)
        int distance = getManhattanDistance(this.tileX, this.tileY, targetX, targetY);
        return distance <= this.moveSpeed;
    }

    // calcule la distance de Manhattan entre deux points
    private int getManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    // Calcule la distance euclidienne (pour portée d'attaque)
    public double getEuclideanDistance(Unit other) {
        int dx = other.tileX - this.tileX;
        int dy = other.tileY - this.tileY;
        return Math.sqrt(dx * dx + dy * dy);
    }
      
    
        
    // Réinitialise l'état de l'unité pour un nouveau tour
    public void resetTurn() {
        this.hasMoved = false;
        this.hasAttacked = false;
    }
    
    
    //Charge la texture de l'unité

    @SuppressWarnings("CallToPrintStackTrace")
    public void loadTexture() {
        if (texturePath != null && !texturePath.isEmpty()) {
            try {
                this.texture = new Texture(texturePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de la texture: " + texturePath);
                e.printStackTrace();
            }
        }
    }
    
    // Dessine l'unité à l'écran
    public void render(SpriteBatch batch, GameMap gameMap) {
        if (!this.isAlive) {
            return;
        }
        
        if (texture != null) {
         double[] pixelPos = gameMap.tileToPixelTopLeft(tileX, tileY);
            float x = (float) pixelPos[0];
            float y = (float) pixelPos[1];
            
            batch.draw(texture, x, y, gameMap.getTileWidth(), gameMap.getTileHeight());
            
        }
    }
    
    //libère les ressources graphiques
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
        
    // Appelé quand l'unité meurt
    protected void onDeath() {
        System.out.println(getUnitType() + " est morte à la position [" + tileX + "," + tileY + "]");
    }
    
    // getters et setters 
    public int getHp() {
        return hp;
    }
    public int getMaxHp() {
        return maxHp;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getRange() {
        return range;
    }
    public int getMoveSpeed() {
        return moveSpeed;
    }
    public int getTileX() {
        return tileX;
    }
    public int getTileY() {
        return tileY;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public boolean hasMoved() {
        return hasMoved;
    }
    public boolean hasAttacked() {
        return hasAttacked;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    public boolean isPlayerUnit() {
        return isPlayerUnit;
    }
    public int getGoldCost() {
        return goldCost;
    }
    public int getFoodCost() {
        return foodCost;
    }
    public int getStoneCost() {
        return stoneCost;
    }
    public WeaponType getWeaponType() {
        return weaponType;
    }
    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }
    public String getTexturePath() {
        return texturePath;
    }
    
    // Retourne une description complète de l'unité
    public String getDescription() {
        return String.format(
            "%s\nHP: %d/%d\nAttaque: %d\nDéfense: %d\nPortée: %d\nVitesse: %d",
            getUnitType(), hp, maxHp, attack, defense, range, moveSpeed
        );
    }

}