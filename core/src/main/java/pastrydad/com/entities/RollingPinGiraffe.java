package pastrydad.com.entities;



//giraffe ave un rouleau de patisserie comme arme
public class RollingPinGiraffe extends Unit {

     public RollingPinGiraffe(int tileX, int tileY, boolean isPlayer) {
        super(tileX, tileY, isPlayer);
        this.texturePath = "units/rolling-pin-giraffe.png";
    }
    
    @Override
    protected void initializeStats() {
        // Stats d'archer
        this.maxHp = 50;
        this.hp = 50;
        this.attack = 18;
        this.defense = 3;
        this.range = 4; // Longue portée
        this.moveSpeed = 3; // Mobile
        
        // Coûts 
        this.goldCost = 75;
        this.foodCost = 15;
        this.stoneCost = 10;
        
        // Arme
        this.weaponType = WeaponType.ROLLING_PIN;
    }
    
    @Override
    public String getUnitType() {
        return "RollingPin Girafe";
    }
    
    @Override
    public boolean useSpecialAbility(Unit target) {
        if (!canAttack(target) || hasAttacked) {
            return false;
        }
        
        // Tir qui ignore la défense
        int preciseDamage = attack + getWeaponBonus();
        target.takeDamage(preciseDamage);
        
        this.hasAttacked = true;
        
        System.out.println(getUnitType() + " utilise Tir de Précision!");
        return true;
    }


}

