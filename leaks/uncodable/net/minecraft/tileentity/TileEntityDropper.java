package net.minecraft.tileentity;

public class TileEntityDropper extends TileEntityDispenser {
   @Override
   public String getCommandSenderName() {
      return this.hasCustomName() ? this.customName : "container.dropper";
   }

   @Override
   public String getGuiID() {
      return "minecraft:dropper";
   }
}
