package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventStep;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ModuleInfo(name = "Step", description = "Step up blocks.", category = Module.Category.MOVEMENT)
public class Step extends Module {

  private final Executor executor = Executors.newSingleThreadExecutor();
  private final BooleanSetting ncpStep = new BooleanSetting(this, "NCP", "NoCheatPlus step.", true);

  @Subscribe
  public void handleStep(EventStep eventStep) {
    if (eventStep.pre()) {
      eventStep.height(1F);
    } else if (ncpStep.value()) {
      if (eventStep.height() >= 0.8) {
        float[] heights = {0.42F, 0.75F};
        for (float h : heights)
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + h, mc.thePlayer.posZ, false));
        mc.timer.timerSpeed = 0.33F;
        executor.execute(() -> {
          try {
            Thread.sleep(150);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          mc.timer.timerSpeed = 1;
        });
      }
    }
  }

}
