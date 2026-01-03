package pastrydad.com.entities;



//giraffe avec une casserole de cuisine comme arme
public class PanGiraffe extends Unit {

      public PanGiraffe(int tileX, int tileY, boolean isPlayer) {
        super(tileX, tileY, isPlayer);
        this.texturePath = "units/pan-giraffe.png";
    }
    
    @Override
    protected void initializeStats() {
        // Stats de tank
        this.maxHp = 120;
        this.hp = 120;
        this.attack = 20;
        this.defense = 12;
        this.range = 1; // Corps à corps
        this.moveSpeed = 2; // Lente
        
        //  Coûts élevés a arranger en fonction des resource
        this.goldCost = 100;
        this.foodCost = 20;
        this.stoneCost = 15;
        
        // Arme
        this.weaponType = WeaponType.PAN;
    }
    
    @Override
    public String getUnitType() {
        return "Pan Girafe";
    }
    
    @Override
    public boolean useSpecialAbility(Unit target) {
        if (!canAttack(target) || hasAttacked) {
            return false;
        }
        
        // Coup de casserole: gros dégâts (150% de l'attaque)
        int heavyDamage = (int)(attack * 1.5) + getWeaponBonus();
        target.takeDamage(heavyDamage);
        
        this.hasAttacked = true;
        
        System.out.println(getUnitType() + " utilise Coup de Casserole ");
        return true;
    }
}