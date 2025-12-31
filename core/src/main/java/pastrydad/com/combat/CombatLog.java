package pastrydad.com.combat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CombatLog {

     // Type de l'unité attaquante 
    private final String attackerType;
    
    // Type de l'unité défenseure 
    private final String defenderType;
    
    // Dégâts infligés 
    private final int damage;
    
    // Était-ce un coup critique ? 
    private final boolean critical;
    
    // Y a-t-il eu une mort ? 
    private final boolean kill;
    
    // Message du combat 
    private final String message;
    
    // Horodatage 
    private final long timestamp;
    
    // Formateur de date 
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    public CombatLog(String attackerType, String defenderType, int damage, 
                    boolean critical, String message) {
        this.attackerType = attackerType;
        this.defenderType = defenderType;
        this.damage = damage;
        this.critical = critical;
        this.kill = message.contains("éliminé") || message.contains("☠️");
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // getters
    
    public String getAttackerType() {
        return attackerType;
    }
    
    public String getDefenderType() {
        return defenderType;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public boolean isCritical() {
        return critical;
    }
    
    public boolean wasKill() {
        return kill;
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    // Retourne l'heure du combat formatée
     
    public String getFormattedTime() {
        return dateFormat.format(new Date(timestamp));
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", getFormattedTime(), message);
    }
}
