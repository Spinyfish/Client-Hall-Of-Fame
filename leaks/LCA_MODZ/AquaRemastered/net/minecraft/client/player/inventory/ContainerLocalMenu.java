package net.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu extends InventoryBasic implements ILockableContainer {
   private String guiID;
   private Map<Integer, Integer> field_174895_b = Maps.newHashMap();

   public ContainerLocalMenu(String id, IChatComponent title, int slotCount) {
      super(title, slotCount);
      this.guiID = id;
   }

   @Override
   public int getField(int id) {
      return this.field_174895_b.containsKey(id) ? this.field_174895_b.get(id) : 0;
   }

   @Override
   public void setField(int id, int value) {
      this.field_174895_b.put(id, value);
   }

   @Override
   public int getFieldCount() {
      return this.field_174895_b.size();
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public void setLockCode(LockCode code) {
   }

   @Override
   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   @Override
   public String getGuiID() {
      return this.guiID;
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      throw new UnsupportedOperationException();
   }
}
