/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.EventUpdate;
import lol.november.management.inventory.OpenInventory;
import lol.november.utility.math.timer.Timer;
import lol.november.utility.player.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "InventoryManager",
  description = "Manages your inventory",
  category = Category.PLAYER
)
public class InventoryManagerModule extends Module {

  private static final List<Class<? extends Item>> toolTypes =
    Lists.newArrayList(
      ItemSword.class,
      ItemPickaxe.class,
      ItemAxe.class,
      ItemSpade.class,
      ItemHoe.class,
      ItemBow.class
    );

  private final Setting<OpenInventory> inventory = new Setting<>(
    "Inventory",
    OpenInventory.OPEN
  );
  private final Setting<Double> delay = new Setting<>(
    "Delay",
    50.0,
    0.01,
    0.0,
    500.0
  );
  private final Setting<Boolean> organizeHotbar = new Setting<>(
    "Organize Hotbar",
    true
  );

  private final Map<Integer, Class<? extends Item>> layout = new HashMap<>();
  private final Map<Class<? extends Item>, Integer> bestItemSlots =
    new HashMap<>();

  private final Timer timer = new Timer();
  private final int[] bestArmor = new int[4];
  private boolean inventorySpoof;

  public InventoryManagerModule() {
    layout.put(0, ItemSword.class);
    layout.put(1, ItemAppleGold.class);
    layout.put(2, ItemPickaxe.class);
    layout.put(3, ItemBow.class);
    layout.put(4, ItemEnderPearl.class);
    layout.put(5, ItemPotion.class);
    layout.put(6, ItemBlock.class);
    layout.put(7, ItemBlock.class);
    layout.put(8, null);
  }

  @Override
  public void enable() {
    super.enable();

    bestItemSlots.clear();
    Arrays.fill(bestArmor, -1);

    spoofClose();
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    cacheBestArmor();
    cacheBestItems();

    if (
      inventory.getValue() == OpenInventory.OPEN &&
      !(mc.currentScreen instanceof GuiInventory)
    ) return;

    if (!timer.passed(delay.getValue().longValue(), false)) return;

    for (int i = 0; i < 36; ++i) {
      ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
      if (itemStack == null) continue;

      // the correct slot
      int packetSlot = InventoryUtils.fix(i);

      // replace armor
      if (itemStack.getItem() instanceof ItemArmor itemArmor) {
        int armorIndex = 3 - itemArmor.armorType;
        int slot = bestArmor[armorIndex];
        if (slot != -1 && slot == i) {
          replaceArmor(armorIndex);
          timer.reset();
          break;
        }

        throwOut(packetSlot);
        timer.reset();
        return;
      } else if (bestItemSlots.containsKey(itemStack.getItem().getClass())) {
        int slot = bestItemSlots.getOrDefault(
          itemStack.getItem().getClass(),
          -1
        );
        if (slot != -1 && slot != i) {
          throwOut(packetSlot);
          timer.reset();
          return;
        }
      } else if (itemStack.getItem() instanceof ItemPotion itemPotion) {
        boolean valid = false;
        List<PotionEffect> effects = itemPotion.getEffects(itemStack);
        for (PotionEffect potionEffect : effects) {
          for (Potion potion : InventoryUtils.validPotions) {
            if (potion.getId() == potionEffect.getPotionID()) {
              valid = true;
              break;
            }
          }
        }

        if (!valid) {
          throwOut(packetSlot);
          timer.reset();
          return;
        }
      } else if (!InventoryUtils.useful(itemStack.getItem())) {
        throwOut(packetSlot);
        timer.reset();
        return;
      }
    }

    if (organizeHotbar.getValue()) {
      // todo
    }
  };

  private void spoofOpen() {
    if (!inventorySpoof && !(mc.currentScreen instanceof GuiInventory)) {
      inventorySpoof = true;
      InventoryUtils.openInventory();
    }
  }

  private void spoofClose() {
    if (inventorySpoof && !(mc.currentScreen instanceof GuiContainer)) {
      inventorySpoof = false;
      InventoryUtils.closeCurrentInventory();
    }
  }

  private void replaceArmor(int armorIndex) {
    int bestSlot = bestArmor[armorIndex];
    if (bestSlot == -1) return;

    int windowId = mc.thePlayer.openContainer.windowId;

    if (mc.thePlayer.inventory.armorInventory[armorIndex] != null) throwOut(
      8 - armorIndex
    );
    mc.playerController.windowClick(
      windowId,
      InventoryUtils.fix(bestSlot),
      0,
      1,
      mc.thePlayer
    );

    bestArmor[armorIndex] = -1;
  }

  private void throwOut(int slot) {
    mc.playerController.windowClick(
      InventoryUtils.windowId(),
      slot,
      1,
      4,
      mc.thePlayer
    );
  }

  private void cacheBestArmor() {
    float[] scores = new float[4];
    for (int i = 0; i < bestArmor.length; ++i) {
      ItemStack equippedStack = mc.thePlayer.inventory.armorInventory[i];
      if (
        equippedStack != null && equippedStack.getItem() instanceof ItemArmor
      ) {
        scores[i] = InventoryUtils.score(equippedStack);
      }
    }

    for (int i = 0; i < 36; ++i) {
      ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
      if (
        itemStack == null || !(itemStack.getItem() instanceof ItemArmor)
      ) continue;

      int armorIndex = 3 - ((ItemArmor) itemStack.getItem()).armorType;

      float score = InventoryUtils.score(itemStack);
      if (score > scores[armorIndex]) {
        bestArmor[armorIndex] = i;
        scores[armorIndex] = score;
      }
    }
  }

  private void cacheBestItems() {
    for (int i = 0; i < 36; ++i) {
      ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
      if (itemStack == null) continue;

      for (Class<? extends Item> toolClass : toolTypes) {
        if (toolClass.isInstance(itemStack.getItem())) {
          float score = InventoryUtils.score(itemStack);
          float lastBestScore = 0.0f;

          int slot = bestItemSlots.getOrDefault(toolClass, -1);
          if (slot != -1) {
            lastBestScore =
              InventoryUtils.score(mc.thePlayer.inventory.getStackInSlot(slot));
          }

          if (score > lastBestScore) bestItemSlots.put(toolClass, i);
        }
      }
    }
  }
}
