package pastrydad.com.combat;



public class CombatStats {
    // Type d'unité */
    private final String unitType;
    
    // Nombre de kills */
    private int kills;
    
    // Nombre de morts
    private int deaths;
    
    // Dégâts totaux infligés */
    private int totalDamageDealt;
    
    // Dégâts totaux reçus */
    private int totalDamageTaken;
    
    public CombatStats(String unitType, int kills, int deaths, 
                      int totalDamageDealt, int totalDamageTaken) {
        this.unitType = unitType;
        this.kills = kills;
        this.deaths = deaths;
        this.totalDamageDealt = totalDamageDealt;
        this.totalDamageTaken = totalDamageTaken;
    }
    
    // Calcule le ratio K/D
     
    public double getKDRatio() {
        if (deaths == 0) {
            return kills > 0 ? Double.POSITIVE_INFINITY : 0.0;
        }
        return (double) kills / deaths;
    }
    
    // Calcule les dégâts moyens infligés par kill
     
    public double getAverageDamagePerKill() {
        if (kills == 0) {
            return 0.0;
        }
        return (double) totalDamageDealt / kills;
    }
    
    // Retourne un résumé des statistiques
     public String getSummary() {
        return String.format(
            "%s - K/D: %d/%d (%.2f) | Dégâts: %d infligés / %d reçus",
            unitType, kills, deaths, getKDRatio(), 
            totalDamageDealt, totalDamageTaken
        );
    }
    
    // getters
    public String getUnitType() {
        return unitType;
    }
    public int getKills() {
        return kills;
    }
    public int getDeaths() {
        return deaths;
    }
    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }
    public int getTotalDamageTaken() {
        return totalDamageTaken;
    }
    
    @Override
    public String toString() {
        return getSummary();
    }


}
