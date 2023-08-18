/* November.lol © 2023 */
package lol.november.feature.module.impl.movement;

import static net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT;
import static org.lwjgl.input.Keyboard.KEY_NONE;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "InventoryWalk",
  description = "Allows you to walk in inventories",
  category = Category.MOVEMENT
)
public class InventoryWalkModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA);

  private final KeyBinding[] binds = new KeyBinding[4];

  @Override
  public void enable() {
    super.enable();

    binds[0] = mc.gameSettings.keyBindForward;
    binds[1] = mc.gameSettings.keyBindBack;
    binds[2] = mc.gameSettings.keyBindLeft;
    binds[3] = mc.gameSettings.keyBindRight;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (!(mc.currentScreen instanceof GuiContainer)) return;

    for (KeyBinding keyBinding : binds) {
      int keyCode = keyBinding.getKeyCode();
      if (keyCode < -100) {
        KeyBinding.setKeyBindState(keyCode, Mouse.isButtonDown(keyCode + 100));
      } else if (keyCode > KEY_NONE) {
        KeyBinding.setKeyBindState(keyCode, Keyboard.isKeyDown(keyCode));
      }
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C16PacketClientStatus packet) {
      if (
        packet.getStatus() == OPEN_INVENTORY_ACHIEVEMENT &&
        mode.getValue() == Mode.CANCEL
      ) event.setCanceled(true);
    }
  };

  private enum Mode {
    VANILLA,
    CANCEL,
  }
}
