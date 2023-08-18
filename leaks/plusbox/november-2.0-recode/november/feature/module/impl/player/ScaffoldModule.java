/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import static lol.november.listener.bus.DefaultEventPriority.MEDIUM;

import lol.november.November;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.module.impl.player.scaffold.Placement;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.EventUpdate;
import lol.november.listener.event.player.move.EventWalkingUpdate;
import lol.november.utility.math.RotationUtils;
import lol.november.utility.net.PacketUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Scaffold",
  description = "Rapidly places blocks at your feet",
  category = Category.PLAYER
)
public class ScaffoldModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.WATCHDOG);
  private final Setting<Rotate> rotate = new Setting<>(
    "Rotate",
    Rotate.WATCHDOG
  );
  private final Setting<Tower> tower = new Setting<>("Tower", Tower.NONE);
  private final Setting<Boolean> swing = new Setting<>("Swing", true);

  private float[] rotations;

  @Override
  public void disable() {
    super.disable();

    rotations = null;
    November.instance().rotations().reset(MEDIUM);
    November.instance().inventory().sync();
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    Placement placement = placement();
    if (placement == null) return;
    hitvec(placement);

    if (rotate.getValue() != Rotate.NONE) {
      switch (rotate.getValue()) {
        case BASIC -> rotations =
          RotationUtils.block(placement.getPos(), placement.getFacing());
        case REVERSE -> rotations =
          new float[] { mc.thePlayer.rotationYaw - 180.0f, 86.0f };
        case WATCHDOG -> {
          rotations =
            RotationUtils.middleBlock(
              placement.getPos(),
              placement.getFacing()
            );
          rotations[1] = (float) (85.0f + Math.random());
        }
      }
    } else {
      rotations = null;
    }

    int slot = -1;
    for (int i = 0; i < 9; ++i) {
      ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
      if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
        slot = i;
        break;
      }
    }

    if (slot == -1) return;
    if (November.instance().inventory().slot() != slot) {
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
    }

    boolean result = mc.playerController.onPlayerRightClick(
      mc.thePlayer,
      mc.theWorld,
      November.instance().inventory().itemStack(),
      placement.getPos(),
      placement.getFacing(),
      placement.getHitVec()
    );
    if (result) {
      if (swing.getValue()) {
        mc.thePlayer.swingItem();
      } else {
        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
      }

      if (mode.getValue() == Mode.WATCHDOG) {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
          // todo
        } else {
          mc.thePlayer.motionX *= 0.95;
          mc.thePlayer.motionZ *= 0.95;
        }
      }

      if (mc.gameSettings.keyBindJump.pressed) {
        switch (tower.getValue()) {
          case JUMP -> mc.thePlayer.jump();
          case MOTION -> mc.thePlayer.motionY = 0.42f;
          case NEW_NCP -> {
            if (
              (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.1) ||
              mc.thePlayer.motionY <= 0.16477328182606651
            ) {
              mc.thePlayer.motionY = 0.42f;
            }
          }
          case AGC -> {
            // idk if this works
            if (mc.thePlayer.ticksExisted % 2 == 0) {
              mc.thePlayer.motionY = 0.42f;
            }
          }
          case VULCAN -> {
            PacketUtils.repeated(new C08PacketPlayerBlockPlacement(null), 2);
            mc.thePlayer.motionY = 0.42f;
          }
          case WATCHDOG -> {
            // todo
            // btw plus PLEASE dont add that retarded C04 tower i want a motion tower if possible
          }
        }
      }
    }
  };

  @Subscribe
  private final Listener<EventWalkingUpdate> walkingUpdate = event -> {
    if (rotations == null) return;

    November.instance().rotations().spoof(MEDIUM, 100, rotations);
  };

  private void hitvec(Placement placement) {
    placement.setHitVec(new Vec3(placement.getPos()).addVector(0.5, 0.5, 0.5));
  }

  private Placement placement() {
    BlockPos pos = new BlockPos(mc.thePlayer.getPositionVector()).down();

    for (EnumFacing facing : EnumFacing.values()) {
      BlockPos n = pos.offset(facing);
      if (
        !mc.theWorld.getBlockState(n).getBlock().getMaterial().isReplaceable()
      ) {
        return new Placement(n, facing.getOpposite());
      }
    }

    for (EnumFacing facing : EnumFacing.values()) {
      BlockPos n = pos.offset(facing);
      if (
        mc.theWorld.getBlockState(n).getBlock().getMaterial().isReplaceable()
      ) {
        for (EnumFacing f : EnumFacing.values()) {
          BlockPos nn = n.offset(f);
          if (
            !mc.theWorld
              .getBlockState(nn)
              .getBlock()
              .getMaterial()
              .isReplaceable()
          ) {
            return new Placement(nn, f.getOpposite());
          }
        }
      }
    }

    return null;
  }

  private enum Mode {
    NORMAL,
    WATCHDOG,
    RAYTRACE,
  }

  private enum Rotate {
    NONE,
    BASIC,
    REVERSE,
    WATCHDOG,
  }

  private enum Tower {
    NONE,
    MOTION,
    JUMP,
    NEW_NCP,
    AGC,
    VULCAN,
    WATCHDOG,
  }
}
