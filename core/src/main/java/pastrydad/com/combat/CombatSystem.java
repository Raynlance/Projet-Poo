package pastrydad.com.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pastrydad.com.entities.Unit;

public class CombatSystem {

    // Générateur de nombres aléatoires pour les combats
    private static final Random random = new Random();
    
    // Historique des combats
    private List<CombatLog> combatHistory;
    
    // Facteur de variabilité des dégâts (0.8 à 1.2) 
    private static final double DAMAGE_VARIANCE = 0.2;
    
    // Probabilité de coup critique (5%) 
    private static final double CRITICAL_HIT_CHANCE = 0.05;
    
    // Multiplicateur de dégâts critiques 
    private static final double CRITICAL_MULTIPLIER = 2.0;
    
    public CombatSystem() {
        this.combatHistory = new ArrayList<>();
    }
    
    //execute le combat et retourne le resultat
    public CombatResult executeCombat(Unit attacker, Unit defender) {
        // Vérifications de base
        if (!canFight(attacker, defender)) {
            return new CombatResult(attacker, defender, 0, false, "Combat impossible");
        }
        
        // Calculer les dégâts de base
        int baseDamage = calculateBaseDamage(attacker, defender);
        
        // Vérifier coup critique
        boolean isCritical = random.nextDouble() < CRITICAL_HIT_CHANCE;
        if (isCritical) {
            baseDamage = (int)(baseDamage * CRITICAL_MULTIPLIER);
        }
        
        // Appliquer les dégâts
        int hpBefore = defender.getHp();
        defender.takeDamage(baseDamage);
        int actualDamage = hpBefore - defender.getHp();
        
        // Créer le résultat
        CombatResult result = new CombatResult(
            attacker, 
            defender, 
            actualDamage, 
            isCritical,
            generateCombatMessage(attacker, defender, actualDamage, isCritical)
        );
        
        // Logger le combat
        logCombat(result);
        
        // Marquer que l'attaquant a attaqué
        attacker.attack(defender); // Ceci met hasAttacked à true
        
        return result;
    }
    
    /**
     * Calcule les dégâts de base avant application
     * 
     * @param attacker L'attaquant
     * @param defender Le défenseur
     * @return Les dégâts calculés
     */
    private int calculateBaseDamage(Unit attacker, Unit defender) {
        // Formule: (Attaque - Défense/2) * Facteur aléatoire + Bonus d'arme
        int baseDamage = attacker.getAttack() - (defender.getDefense() / 2);
        
        // Facteur aléatoire (±20%)
        double variance = 1.0 + (random.nextDouble() * 2 - 1) * DAMAGE_VARIANCE;
        baseDamage = (int)(baseDamage * variance);
        
        // Dégâts minimum de 1
        baseDamage = Math.max(1, baseDamage);
        
        return baseDamage;
    }
    
    /**
     * Vérifie si deux unités peuvent combattre
     */
    private boolean canFight(Unit attacker, Unit defender) {
        if (attacker == null || defender == null) {
            return false;
        }
        
        if (!attacker.isAlive() || !defender.isAlive()) {
            return false;
        }
        
        if (attacker.hasAttacked()) {
            return false;
        }
        
        // Ne peut pas attaquer un allié
        if (attacker.isPlayerUnit() == defender.isPlayerUnit()) {
            return false;
        }
        
        // Vérifier la portée
        float distance = attacker.getPosition().dst(defender.getPosition());
        return distance <= attacker.getRange();
    }
    
    /**
     * Exécute un contre-attaque si possible
     * 
     * @param defender Le défenseur qui contre-attaque
     * @param attacker L'attaquant original
     * @return Le résultat du contre-attaque ou null
     */
    public CombatResult executeCounterAttack(Unit defender, Unit attacker) {
        // Le défenseur doit être vivant et à portée
        if (!defender.isAlive() || defender.hasAttacked()) {
            return null;
        }
        
        float distance = defender.getPosition().dst(attacker.getPosition());
        if (distance > defender.getRange()) {
            return null;
        }
        
        // 50% de chance de contre-attaquer
        if (random.nextDouble() < 0.5) {
            System.out.println(defender.getUnitType() + " contre-attaque!");
            return executeCombat(defender, attacker);
        }
        
        return null;
    }
    
    /**
     * Calcule si une unité peut tuer une autre en un coup
     * 
     * @param attacker L'attaquant
     * @param target La cible
     * @return true si le one-shot est possible
     */
    public boolean canOneShot(Unit attacker, Unit target) {
        int maxPossibleDamage = (int)(attacker.getAttack() * (1 + DAMAGE_VARIANCE) * CRITICAL_MULTIPLIER);
        return maxPossibleDamage >= target.getHp();
    }
    
    /**
     * Génère un message descriptif du combat
     */
    private String generateCombatMessage(Unit attacker, Unit defender, 
                                        int damage, boolean isCritical) {
        StringBuilder msg = new StringBuilder();
        
        msg.append(attacker.getUnitType())
           .append(" (").append(attacker.getWeaponType().getDisplayName()).append(") ");
        
        if (isCritical) {
            msg.append("inflige un COUP CRITIQUE à ");
        } else {
            msg.append("attaque ");
        }
        
        msg.append(defender.getUnitType())
           .append(" pour ").append(damage).append(" dégâts!");
        
        if (!defender.isAlive()) {
            msg.append(" ☠️ ").append(defender.getUnitType()).append(" est éliminé!");
        }
        
        return msg.toString();
    }
    
    /**
     * Enregistre un combat dans l'historique
     */
    private void logCombat(CombatResult result) {
        CombatLog log = new CombatLog(
            result.getAttacker().getUnitType(),
            result.getDefender().getUnitType(),
            result.getDamageDealt(),
            result.isCriticalHit(),
            result.getMessage()
        );
        
        combatHistory.add(log);
        
        // Garder seulement les 100 derniers combats
        if (combatHistory.size() > 100) {
            combatHistory.remove(0);
        }
    }
    
    /**
     * Retourne l'historique des combats
     */
    public List<CombatLog> getCombatHistory() {
        return new ArrayList<>(combatHistory);
    }
    
    /**
     * Efface l'historique des combats
     */
    public void clearHistory() {
        combatHistory.clear();
    }
    
    /**
     * Affiche les derniers combats
     * 
     * @param count Nombre de combats à afficher
     */
    public void printRecentCombats(int count) {
        System.out.println("=== DERNIERS COMBATS ===");
        int start = Math.max(0, combatHistory.size() - count);
        for (int i = start; i < combatHistory.size(); i++) {
            System.out.println(combatHistory.get(i));
        }
    }
    
    /**
     * Calcule les statistiques de combat pour une unité
     * 
     * @param unitType Type d'unité
     * @return Les statistiques
     */
    public CombatStats getStatsForUnit(String unitType) {
        int kills = 0;
        int deaths = 0;
        int totalDamageDealt = 0;
        int totalDamageTaken = 0;
        
        for (CombatLog log : combatHistory) {
            if (log.getAttackerType().equals(unitType)) {
                totalDamageDealt += log.getDamage();
                if (log.wasKill()) {
                    kills++;
                }
            }
            if (log.getDefenderType().equals(unitType)) {
                totalDamageTaken += log.getDamage();
                if (log.wasKill()) {
                    deaths++;
                }
            }
        }
        
        return new CombatStats(unitType, kills, deaths, totalDamageDealt, totalDamageTaken);
    }

}
