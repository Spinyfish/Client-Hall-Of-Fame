package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Jesus", description = "Walk On Water", category = Module.Category.PLAYER)
public class Jesus extends Module {

  @Getter
  public final EnumSetting<JesusMode> enumSetting = new EnumSetting<>(this, "Mode", JesusMode.values());

  private double prevX, prevY, prevZ;

  @Subscribe
  public void handleMove(EventAbsoluteMove eventAbsoluteMove) {
    switch (enumSetting.value()) {
      case MOTION -> {
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer)).getBlock() instanceof BlockStaticLiquid) {
          mc.thePlayer.motionY = 0.15;
        }
      }
      case LEGIT -> {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
          mc.gameSettings.keyBindJump.pressed(true);
          mc.thePlayer.onGround = true;
        } else
          mc.gameSettings.keyBindJump.pressed(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
      }
    }
  }

  @Getter
  @AllArgsConstructor
  public enum JesusMode implements INameable {
    COLLIDE("Collide"),
    MOTION("Motion"),
    LEGIT("Legit");
    private final String getName;
  }

}
