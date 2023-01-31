package intent.AquaDev.aqua.modules.player;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventClick;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class InvManager extends Module {
   private static int[] bestArmorDamageReducement;
   TimeUtil time = new TimeUtil();
   TimeUtil timeUtil = new TimeUtil();
   private int[] bestArmorSlots;
   private float bestSwordDamage;
   private int bestSwordSlot;
   private final List<Integer> trash = new ArrayList<>();
   private boolean canFake;

   public InvManager() {
      super("InvManager", Module.Type.Player, "InvManager", 0, Category.Player);
      Aqua.setmgr.register(new Setting("Delay", this, 50.0, 0.0, 2000.0, false));
      Aqua.setmgr.register(new Setting("PrevSwords", this, true));
      Aqua.setmgr.register(new Setting("OpenInv", this, false));
      Aqua.setmgr.register(new Setting("FakeInv", this, true));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventClick) {
         this.searchForItems();

         for(int i = 0; i < 4; ++i) {
            if (this.bestArmorSlots[i] != -1) {
               int bestSlot = this.bestArmorSlots[i];
               float DelayY = (float)Aqua.setmgr.getSetting("InvManagerDelay").getCurrentNumber();
               ItemStack oldArmor = mc.thePlayer.inventory.armorItemInSlot(i);
               if (Aqua.setmgr.getSetting("InvManagerOpenInv").isState()
                  && mc.currentScreen instanceof GuiInventory
                  && !Aqua.setmgr.getSetting("InvManagerFakeInv").isState()
                  && oldArmor != null
                  && oldArmor.getItem() != null
                  && this.time.hasReached((long)DelayY)) {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - i, 0, 1, mc.thePlayer);
                  this.time.reset();
               }

               if (Aqua.setmgr.getSetting("InvManagerFakeInv").isState() && this.canFakeInv()) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
                  if (oldArmor != null && oldArmor.getItem() != null && this.time.hasReached((long)DelayY)) {
                     mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - i, 0, 1, mc.thePlayer);
                     this.time.reset();
                  }
               }

               if (Aqua.setmgr.getSetting("InvManagerOpenInv").isState()
                  && mc.currentScreen instanceof GuiInventory
                  && !Aqua.setmgr.getSetting("InvManagerFakeInv").isState()
                  && this.time.hasReached((long)DelayY)) {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestSlot < 9 ? bestSlot + 36 : bestSlot, 0, 1, mc.thePlayer);
                  this.time.reset();
               }

               if (Aqua.setmgr.getSetting("InvManagerFakeInv").isState() && this.canFakeInv()) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
                  if (this.time.hasReached((long)DelayY)) {
                     mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestSlot < 9 ? bestSlot + 36 : bestSlot, 0, 1, mc.thePlayer);
                     this.time.reset();
                  }
               }
            }
         }

         if (Aqua.setmgr.getSetting("InvManagerOpenInv").isState()
            && mc.currentScreen instanceof GuiInventory
            && !Aqua.setmgr.getSetting("InvManagerFakeInv").isState()
            && this.bestSwordSlot != -1
            && this.bestSwordDamage != -1.0F) {
            float DelayY = (float)Aqua.setmgr.getSetting("InvManagerDelay").getCurrentNumber();
            if (this.time.hasReached((long)DelayY)) {
               mc.playerController
                  .windowClick(
                     mc.thePlayer.inventoryContainer.windowId, this.bestSwordSlot < 9 ? this.bestSwordSlot + 36 : this.bestSwordSlot, 0, 2, mc.thePlayer
                  );
               this.time.reset();
            }

            mc.playerController.syncCurrentPlayItem();
         }

         if (Aqua.setmgr.getSetting("InvManagerFakeInv").isState() && this.canFakeInv()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
            if (this.bestSwordSlot != -1 && this.bestSwordDamage != -1.0F) {
               float DelayY = (float)Aqua.setmgr.getSetting("InvManagerDelay").getCurrentNumber();
               if (this.time.hasReached((long)DelayY)) {
                  mc.playerController
                     .windowClick(
                        mc.thePlayer.inventoryContainer.windowId, this.bestSwordSlot < 9 ? this.bestSwordSlot + 36 : this.bestSwordSlot, 0, 2, mc.thePlayer
                     );
                  this.time.reset();
               }
            }
         }

         this.searchForTrash();
         Collections.shuffle(this.trash);
         if (Aqua.setmgr.getSetting("InvManagerOpenInv").isState()
            && mc.currentScreen instanceof GuiInventory
            && !Aqua.setmgr.getSetting("InvManagerFakeInv").isState()) {
            for(Integer integer : this.trash) {
               float DelayY = (float)Aqua.setmgr.getSetting("InvManagerDelay").getCurrentNumber();
               if (this.timeUtil.hasReached((long)DelayY)) {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, integer < 9 ? integer + 36 : integer, 1, 4, mc.thePlayer);
                  this.timeUtil.reset();
               }
            }
         }

         if (Aqua.setmgr.getSetting("InvManagerFakeInv").isState() && this.canFakeInv()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));

            for(Integer integer : this.trash) {
               float DelayY = (float)Aqua.setmgr.getSetting("InvManagerDelay").getCurrentNumber();
               if (this.timeUtil.hasReached((long)DelayY)) {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, integer < 9 ? integer + 36 : integer, 1, 4, mc.thePlayer);
                  this.timeUtil.reset();
               }
            }
         }
      }
   }

   public boolean canFakeInv() {
      return !mc.thePlayer.isUsingItem()
         && !mc.thePlayer.isEating()
         && mc.currentScreen == null
         && !mc.gameSettings.keyBindUseItem.isKeyDown()
         && !mc.gameSettings.keyBindAttack.isKeyDown()
         && !mc.gameSettings.keyBindJump.isKeyDown()
         && (double)mc.thePlayer.swingProgress == 0.0;
   }

   private void searchForItems() {
      bestArmorDamageReducement = new int[4];
      this.bestArmorSlots = new int[4];
      this.bestSwordDamage = -1.0F;
      this.bestSwordSlot = -1;
      Arrays.fill(bestArmorDamageReducement, -1);
      Arrays.fill(this.bestArmorSlots, -1);

      for(int i = 0; i < this.bestArmorSlots.length; ++i) {
         ItemStack itemStack = mc.thePlayer.inventory.armorItemInSlot(i);
         if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)itemStack.getItem();
            bestArmorDamageReducement[i] = armor.damageReduceAmount;
         }
      }

      for(int i = 0; i < 36; ++i) {
         ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
         if (itemStack != null && itemStack.getItem() != null) {
            if (itemStack.getItem() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)itemStack.getItem();
               int armorType = 3 - armor.armorType;
               if (bestArmorDamageReducement[armorType] < armor.damageReduceAmount) {
                  bestArmorDamageReducement[armorType] = armor.damageReduceAmount;
                  this.bestArmorSlots[armorType] = i;
               }
            }

            if (itemStack.getItem() instanceof ItemSword) {
               ItemSword sword = (ItemSword)itemStack.getItem();
               if (this.bestSwordDamage < sword.getDamageVsEntity() + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack)) {
                  this.bestSwordDamage = sword.getDamageVsEntity() + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
                  this.bestSwordSlot = i;
               }
            }

            if (itemStack.getItem() instanceof ItemTool) {
               ItemTool sword = (ItemTool)itemStack.getItem();
               float damage = sword.getToolMaterial().getDamageVsEntity()
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);

               try {
                  if (Aqua.setmgr.getSetting("InvManagerPrefSwords").isState()) {
                     --damage;
                  }

                  if (this.bestSwordDamage < damage) {
                     this.bestSwordDamage = damage;
                     this.bestSwordSlot = i;
                  }
               } catch (NullPointerException var6) {
               }
            }
         }
      }
   }

   private void searchForTrash() {
      this.trash.clear();
      bestArmorDamageReducement = new int[4];
      this.bestArmorSlots = new int[4];
      this.bestSwordDamage = -1.0F;
      this.bestSwordSlot = -1;
      Arrays.fill(bestArmorDamageReducement, -1);
      Arrays.fill(this.bestArmorSlots, -1);
      List<Integer>[] allItems = new List[4];
      List<Integer> allSwords = new ArrayList<>();

      for(int i = 0; i < this.bestArmorSlots.length; ++i) {
         ItemStack itemStack = mc.thePlayer.inventory.armorItemInSlot(i);
         allItems[i] = new ArrayList<>();
         if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)itemStack.getItem();
            bestArmorDamageReducement[i] = armor.damageReduceAmount;
            this.bestArmorSlots[i] = 8 + i;
         }
      }

      for(int i = 9; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i) {
         ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
         if (itemStack != null && itemStack.getItem() != null) {
            if (itemStack.getItem() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)itemStack.getItem();
               int armorType = 3 - armor.armorType;
               allItems[armorType].add(i);
               if (bestArmorDamageReducement[armorType] < armor.damageReduceAmount) {
                  bestArmorDamageReducement[armorType] = armor.damageReduceAmount;
                  this.bestArmorSlots[armorType] = i;
               }
            }

            if (itemStack.getItem() instanceof ItemSword) {
               ItemSword sword = (ItemSword)itemStack.getItem();
               allSwords.add(i);
               if (this.bestSwordDamage < sword.getDamageVsEntity() + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack)) {
                  this.bestSwordDamage = sword.getDamageVsEntity() + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
                  this.bestSwordSlot = i;
               }
            }

            if (itemStack.getItem() instanceof ItemTool) {
               ItemTool sword = (ItemTool)itemStack.getItem();
               float damage = sword.getToolMaterial().getDamageVsEntity()
                  + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);

               try {
                  if (Aqua.setmgr.getSetting("InvManagerPrefSwords").isState()) {
                     --damage;
                  }

                  if (this.bestSwordDamage < damage) {
                     this.bestSwordDamage = damage;
                     this.bestSwordSlot = i;
                  }
               } catch (NullPointerException var8) {
               }
            }
         }
      }

      for(int i = 0; i < allItems.length; ++i) {
         List<Integer> allItem = allItems[i];
         int finalI = i;
         allItem.stream().filter(slot -> slot != this.bestArmorSlots[finalI]).forEach(this.trash::add);
      }

      allSwords.stream().filter(slot -> slot != this.bestSwordSlot).forEach(this.trash::add);
   }
}
