package net.optifine.config;

import net.minecraft.src.Config;

public class VillagerProfession {
   private final int profession;
   private final int[] careers;

   public VillagerProfession(int profession, int[] careers) {
      this.profession = profession;
      this.careers = careers;
   }

   public boolean matches(int prof, int car) {
      return this.profession == prof && (this.careers == null || Config.equalsOne(car, this.careers));
   }

   public int[] getCareers() {
      return this.careers;
   }

   @Override
   public String toString() {
      return this.careers == null ? "" + this.profession : "" + this.profession + ":" + Config.arrayToString(this.careers);
   }
}
