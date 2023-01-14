package net.optifine.shaders.config;

public class RenderScale {
   private final float scale;
   private final float offsetX;
   private final float offsetY;

   public RenderScale(float scale, float offsetX, float offsetY) {
      this.scale = scale;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
   }

   public float getScale() {
      return this.scale;
   }

   public float getOffsetX() {
      return this.offsetX;
   }

   public float getOffsetY() {
      return this.offsetY;
   }

   @Override
   public String toString() {
      return "" + this.scale + ", " + this.offsetX + ", " + this.offsetY;
   }
}
