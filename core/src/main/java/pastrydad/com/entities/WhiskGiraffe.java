package pastrydad.com.entities;


//giraffe avec un fouet de cuisine comme arme
public class WhiskGiraffe extends Unit {
    
    public WhiskGiraffe(float startX, float startY, boolean isPlayer) {
        super(startX, startY, isPlayer);
        this.texturePath = "assets/units/whisk_giraffe.png";
    }
    
    @Override
    protected void initializeStats() {
        // Stats de base
        this.maxHp = 60;
        this.hp = 60;
        this.attack = 12;
        this.defense = 5;
        this.range = 1; 
        this.moveSpeed = 3;

        /*cout a arranger d'apres les resource 
        this.goldCost = 50;
        this.flourCost = 10;
        this.sugarCost = 5;*/
        
        // Arme
        this.weaponType = WeaponType.WHISK;
    }
    
    @Override
    public String getUnitType() {
        return "Whisk Girafe";
    }
    
    @Override
    public boolean useSpecialAbility(Unit target) {
        if (!canAttack(target) || hasAttacked) {
            return false;
        }

        int damage1 = (int)(attack * 0.6);
        int damage2 = (int)(attack * 0.6);
        
        target.takeDamage(damage1);
        
        if (target.isAlive()) {
            target.takeDamage(damage2);
        }
        
        this.hasAttacked = true;
        
       // System.out.println(getUnitType() + " utilise Coup de Fouet Rapide!");
        return true;
    }
}

