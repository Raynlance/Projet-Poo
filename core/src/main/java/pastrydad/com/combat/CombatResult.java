package pastrydad.com.combat;

import pastrydad.com.entities.Unit;

public class CombatResult {
     //L'unité attaquante 
    private final Unit attacker;
    
    // L'unité défenseure 
    private final Unit defender;
    
    // Dégâts infligés 
    private final int damageDealt;
    
    // Était-ce un coup critique ? 
    private final boolean criticalHit;
    
    // Le défenseur est-il mort ? 
    private final boolean defenderKilled;
    
    // Message décrivant le combat 
    private final String message;
    
    // Timestamp du combat 
    private final long timestamp;
    
    /**
     * Constructeur
     */
    public CombatResult(Unit attacker, Unit defender, 
                       int damageDealt, boolean criticalHit, String message) {
        this.attacker = attacker;
        this.defender = defender;
        this.damageDealt = damageDealt;
        this.criticalHit = criticalHit;
        this.defenderKilled = !defender.isAlive();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // getters
    
    public Unit getAttacker() {
        return attacker;
    }
    public Unit getDefender() {
        return defender;
    }
    public int getDamageDealt() {
        return damageDealt;
    }
    public boolean isCriticalHit() {
        return criticalHit;
    }
    public boolean isDefenderKilled() {
        return defenderKilled;
    }
    public String getMessage() {
        return message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    
    // Retourne un résumé du combat
    public String getSummary() {
        return String.format(
            "%s vs %s: %d dégâts%s%s",
            attacker.getUnitType(),
            defender.getUnitType(),
            damageDealt,
            criticalHit ? " (CRITIQUE!)" : "",
            defenderKilled ? " [KILL]" : ""
        );
    }
    
    @Override
    public String toString() {
        return message;
    }

}
