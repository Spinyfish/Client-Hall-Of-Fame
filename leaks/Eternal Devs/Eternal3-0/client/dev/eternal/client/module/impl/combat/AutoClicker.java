package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.time.Stopwatch;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(name = "AutoClicker", category = Module.Category.COMBAT)
public class AutoClicker extends Module {
  private final NumberSetting cps = new NumberSetting(this, "CPS", "Clicks per second.", 12, 1, 50, 0.1);
  private final Stopwatch hitWatch = new Stopwatch();
  private long clk;

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (!(hitWatch.hasElapsed(clk))) return;
    if (mc.gameSettings.keyBindAttack.isKeyDown()) {
      KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
      mc.leftClickCounter = 0;
      clk = (long) (RandomUtils.nextLong(1000 / cps.value().longValue() - 2, 1000 / cps.value().longValue() + 2)
          + (RandomUtils.nextLong(0, (System.currentTimeMillis() % 100) + 1) - 25)
          + (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw));
      clk *= Math.random() + 0.5;
      hitWatch.reset();
    }
  }

}
