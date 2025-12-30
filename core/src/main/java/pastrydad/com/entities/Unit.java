package pastrydad.com.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
//import pastrydad.com.map.Tile;

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
   /*
    protected int goldCost;
    protected int flourCost;
    protected int sugarCost; */
    
    //position 
    protected Vector2 position;
    //destination du deplacement 
    protected Vector2 targetPosition;
    
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
    public Unit(float startX, float startY, boolean isPlayer) {
        this.position = new Vector2(startX, startY);
        this.targetPosition = new Vector2(startX, startY);
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
        float distance = this.position.dst(target.position);
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
    public boolean moveTo(float targetX, float targetY) {
        if (!canMoveTo(targetX, targetY)) {
            return false;
        }
        
        this.targetPosition.set(targetX, targetY);
        this.position.set(targetX, targetY);
        this.hasMoved = true;
        
        return true;
    }
    
    // Vérifie si l'unité peut se déplacer vers une position
      public boolean canMoveTo(float targetX, float targetY) {
        if (!this.isAlive || this.hasMoved) {
            return false;
        }
        
        // Vérifier la distance
        float distance = this.position.dst(targetX, targetY);
        return distance <= this.moveSpeed;
    }
    
    // Vérifie si l'unité peut se déplacer vers un tile spécifique besoin de la carte pour le faire
    
    /*public boolean canMoveToTile(Tile tile) {
        if (tile == null || !tile.isWalkable()) {
            return false;
        }
        
        return canMoveTo(tile.getX(), tile.getY());
    }*/
        
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
    public void render(SpriteBatch batch, float tileSize) {
        if (!this.isAlive) {
            return;
        }
        
        if (texture != null) {
            float x = position.x * tileSize;
            float y = position.y * tileSize;
            
            batch.draw(texture, x, y, tileSize, tileSize);
            
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
        System.out.println(getUnitType() + " est morte à la position " + position);
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
    public Vector2 getPosition() {
        return position;
    }
    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
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
    /*  getters des resource a arranger 
    public int getGoldCost() {
        return goldCost;
    }
    public int getFlourCost() {
        return flourCost;
    }
    public int getSugarCost() {
        return sugarCost;
    }*/
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