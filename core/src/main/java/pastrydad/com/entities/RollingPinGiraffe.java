package pastrydad.com.entities;



//giraffe ave un rouleau de patisserie comme arme
public class RollingPinGiraffe extends Unit {

     public RollingPinGiraffe(float startX, float startY, boolean isPlayer) {
        super(startX, startY, isPlayer);
        this.texturePath = "assets/units/rolingpin_giraffe.png";
    }
    
    @Override
    protected void initializeStats() {
        // Stats d'archer
        this.maxHp = 50;
        this.hp = 50;
        this.attack = 18;
        this.defense = 3;
        this.range = 4; // Longue portée!
        this.moveSpeed = 3; // Mobile
        
        /* Coûts moyens
        this.goldCost = 75;
        this.flourCost = 15;
        this.sugarCost = 10;*/
        
        // Arme
        this.weaponType = WeaponType.ROLLING_PIN;
    }
    
    @Override
    public String getUnitType() {
        return "Ranger Girafe";
    }
    
    @Override
    public boolean useSpecialAbility(Unit target) {
        if (!canAttack(target) || hasAttacked) {
            return false;
        }
        
        // Tir de précision: ignore la défense
        int preciseDamage = attack + getWeaponBonus();
        target.takeDamage(preciseDamage);
        
        this.hasAttacked = true;
        
        System.out.println(getUnitType() + " utilise Tir de Précision!");
        return true;
    }


}

