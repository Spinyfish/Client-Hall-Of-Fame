/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import com.google.common.collect.Lists;
import java.util.List;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.EventUpdate;
import lol.november.utility.math.timer.Timer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "ChestStealer",
  description = "Steals items from a chest",
  category = Category.PLAYER
)
public class ChestStealerModule extends Module {

  private static final List<String> chestTranslatable = Lists.newArrayList(
    "container.chest",
    "container.chestDouble"
  );

  private final Setting<Double> delay = new Setting<>(
    "Min Delay",
    50.0,
    0.01,
    0.0,
    2000.0
  );
  private final Setting<Boolean> nameCheck = new Setting<>("Name Check", true);
  private final Setting<Boolean> noJunk = new Setting<>("Ignore Junk", true);
  private final Setting<Boolean> autoClose = new Setting<>("Auto Close", true);

  private final Timer timer = new Timer();

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (
      !(mc.thePlayer.openContainer instanceof ContainerChest container)
    ) return;

    IInventory lower = container.getLowerChestInventory();
    if (
      (nameCheck.getValue() && !validName(lower)) ||
      !timer.passed(delay.getValue().longValue())
    ) return;

    for (int slot = 0; slot < lower.getSizeInventory(); ++slot) {
      ItemStack itemStack = lower.getStackInSlot(slot);
      if (itemStack == null || (junk(itemStack) && noJunk.getValue())) continue;

      mc.playerController.windowClick(
        container.windowId,
        slot,
        0,
        1,
        mc.thePlayer
      );

      timer.reset();
      return;
    }

    if (autoClose.getValue()) mc.thePlayer.closeScreen();
  };

  private boolean validName(IInventory inventory) {
    String name = inventory.getName();
    for (String translation : chestTranslatable) {
      if (name.equals(I18n.format(translation))) {
        return true;
      }
    }

    return false;
  }

  private boolean junk(ItemStack itemStack) {
    return false;
  }
}
