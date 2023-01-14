package net.optifine.shaders;

public class MultiTexID {
   public final int base;
   public final int norm;
   public final int spec;

   public MultiTexID(int baseTex, int normTex, int specTex) {
      this.base = baseTex;
      this.norm = normTex;
      this.spec = specTex;
   }
}
