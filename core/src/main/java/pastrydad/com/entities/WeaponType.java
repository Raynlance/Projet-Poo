package pastrydad.com.entities;

public enum WeaponType {
    WHISK("Fouet"),           
    PAN("Casserole"),         
    ROLLING_PIN("Rouleau");  

     private final String displayName;
        
        WeaponType(String displayName) {
            this.displayName = displayName;
        }
        
     public String getDisplayName() {
      return displayName;
   }
}
