package net.minecraft.block.material;

public class MaterialTransparent extends Material {
   public MaterialTransparent(MapColor color) {
      super(color);
      this.setReplaceable();
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean blocksLight() {
      return false;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }
}
