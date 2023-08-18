/* November.lol © 2023 */
package lol.november.feature.module.impl.combat;

import static lol.november.listener.bus.DefaultEventPriority.HIGH;

import java.util.Comparator;
import java.util.function.Function;
import lol.november.November;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.Stage;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.move.EventWalkingUpdate;
import lol.november.listener.event.render.EventRenderItemUse;
import lol.november.utility.math.MathUtils;
import lol.november.utility.math.RotationUtils;
import lol.november.utility.math.timer.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "KillAura",
  description = "Attacks entities around you",
  category = Category.COMBAT
)
public class KillAuraModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.SINGLE);
  private final Setting<Sort> sort = new Setting<>("Sort", Sort.DISTANCE);

  private final Setting<Click> clicking = new Setting<>(
    "Clicking",
    Click.BASIC
  );
  private final Setting<Integer> minCps = new Setting<>(
    () -> clicking.getValue() != Click.UPDATED,
    "Min CPS",
    12,
    1,
    0,
    20
  );
  private final Setting<Integer> maxCps = new Setting<>(
    () -> clicking.getValue() != Click.UPDATED,
    "Max CPS",
    16,
    1,
    0,
    20
  );

  private final Setting<Double> range = new Setting<>(
    "Range",
    4.0,
    0.01,
    1.0,
    6.0
  );
  private final Setting<Double> wallRange = new Setting<>(
    "Wall Range",
    3.0,
    0.01,
    1.0,
    6.0
  );

  private final Setting<AutoBlock> autoBlock = new Setting<>(
    "Auto Block",
    AutoBlock.VANILLA
  );

  private final Setting<Boolean> rotate = new Setting<>("Rotate", true);

  private final Setting<Boolean> keepSprint = new Setting<>(
    "Keep Sprint",
    false
  );

  private final Timer timer = new Timer();
  private EntityLivingBase attackTarget;
  private float[] rotations;
  private boolean blocking;

  @Override
  public void disable() {
    super.disable();

    attackTarget = null;
    rotations = null;

    if (blocking) unblock();
  }

  @Subscribe(ignoreCanceled = false)
  private final Listener<EventWalkingUpdate> walkingUpdate = event -> {
    if (!validTarget(attackTarget) || mode.getValue() == Mode.SWITCH) {
      attackTarget =
        (EntityLivingBase) mc.theWorld.loadedEntityList
          .stream()
          .filter(entity ->
            entity instanceof EntityLivingBase &&
            validTarget((EntityLivingBase) entity)
          )
          .min(
            Comparator.comparingDouble(entity ->
              sort.getValue().compare((EntityLivingBase) entity)
            )
          )
          .orElse(null);
    }

    if (attackTarget == null) {
      if (blocking && event.getStage() == Stage.PRE) unblock();
      return;
    }

    if (rotate.getValue()) {
      float[] rots = RotationUtils.entity(attackTarget);
      November.instance().rotations().spoof(HIGH, 5, rots);
      rotations = rots;
    }

    if (holdingSword()) {
      if (!blocking) {
        blocking = true;
        switch (autoBlock.getValue()) {
          case HOLD -> mc.gameSettings.keyBindInventory.pressed = true;
          case BASIC, VANILLA -> {
            mc.thePlayer.sendQueue.addToSendQueue(
              new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem())
            );

            // don't attack after blocking - this could be another setting
            if (autoBlock.getValue() == AutoBlock.VANILLA) return;
          }
        }
      }
    } else {
      if (blocking) unblock();
    }

    if (event.getStage() == Stage.PRE) {
      // nov 1.0.0 has this in EventUpdate, this is pretty much the same
      if (autoBlock.getValue() == AutoBlock.WATCHDOG) {
        if (attackTarget.hurtTime > 2) {
          blocking = true;
          mc.thePlayer.sendQueue.addToSendQueue(
            new C08PacketPlayerBlockPlacement(null)
          );
        }
        if (attackTarget.hurtTime == 2) unblock();
      }

      if (timer.passed((long) attackTime())) {
        if (
          autoBlock.getValue() == AutoBlock.BASIC && blocking
        ) mc.thePlayer.sendQueue.addToSendQueue(
          new C07PacketPlayerDigging(
            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
            BlockPos.ORIGIN,
            EnumFacing.DOWN
          )
        );

        mc.thePlayer.swingItem();
        if (keepSprint.getValue()) {
          mc.thePlayer.sendQueue.addToSendQueue(
            new C02PacketUseEntity(
              attackTarget,
              C02PacketUseEntity.Action.ATTACK
            )
          );
        } else {
          mc.playerController.attackEntity(mc.thePlayer, attackTarget);
        }

        timer.reset();

        if (
          autoBlock.getValue() == AutoBlock.BASIC && !blocking
        ) mc.thePlayer.sendQueue.addToSendQueue(
          new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem())
        );
      }
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C02PacketUseEntity packet) {
      if (packet.getAction() != C02PacketUseEntity.Action.ATTACK) return;

      Entity entity = packet.getEntityFromWorld(mc.theWorld);
      if (!(entity instanceof EntityLivingBase livingBase)) return;

      if (
        autoBlock.getValue() == AutoBlock.WATCHDOG && livingBase.hurtTime > 2
      ) event.setCanceled(true);
    } else if (event.get() instanceof C07PacketPlayerDigging packet) {
      if (
        packet.getStatus() != C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
      ) return;

      if (
        autoBlock.getValue() == AutoBlock.VANILLA ||
        autoBlock.getValue() == AutoBlock.WATCHDOG
      ) {
        event.setCanceled(true);
      } else {
        blocking = false;
      }
    }
  };

  @Subscribe
  private final Listener<EventRenderItemUse> renderItemUse = event -> {
    // event.getItemStack() should never be null because of where the event is hooked
    if (blocking && event.getItemStack().getItem() instanceof ItemSword) {
      event.setAction(EnumAction.BLOCK);
      event.setCanceled(true);
    }
  };

  private double attackTime() {
    return switch (clicking.getValue()) {
      case BASIC -> 1000.0 /
      MathUtils.random(minCps.getValue(), maxCps.getValue());
      default -> 0.0;
    };
  }

  private boolean validTarget(EntityLivingBase entity) {
    if (
      entity == null ||
      entity.equals(mc.thePlayer) ||
      entity.isDead ||
      entity.getHealth() <= 0.0f
    ) return false;

    if (entity instanceof EntityArmorStand) return false;

    AntiBotModule antiBot = November
      .instance()
      .modules()
      .get(AntiBotModule.class);
    if (
      antiBot != null && antiBot.toggled() && antiBot.bot(entity.getEntityId())
    ) return false;

    double r = mc.thePlayer.canEntityBeSeen(entity)
      ? range.getValue()
      : wallRange.getValue();
    return mc.thePlayer.getDistanceSqToEntity(entity) <= r * r;
  }

  private void unblock() {
    if (!holdingSword()) {
      blocking = false;
      return;
    }

    switch (autoBlock.getValue()) {
      case HOLD -> mc.gameSettings.keyBindInventory.pressed = false;
      case BASIC, VANILLA, WATCHDOG -> mc.thePlayer.sendQueue.addToSendQueue(
        new C07PacketPlayerDigging(
          C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
          BlockPos.ORIGIN,
          EnumFacing.DOWN
        )
      );
    }

    blocking = false;
  }

  private boolean holdingSword() {
    ItemStack itemStack = mc.thePlayer.getHeldItem();
    return itemStack != null && itemStack.getItem() instanceof ItemSword;
  }

  public EntityLivingBase getAttackTarget() {
    return attackTarget;
  }

  private enum Mode {
    SINGLE,
    SWITCH,
    HURT_TIME,
  }

  private enum Sort {
    DISTANCE(entity -> mc.thePlayer.getDistanceSqToEntity(entity)),
    HEALTH(entity ->
      (double) (entity.getHealth() + entity.getAbsorptionAmount())
    ),
    HURT_TIME(entity -> (double) entity.hurtTime),
    ARMOR(entity -> (double) entity.getTotalArmorValue());

    private final Function<EntityLivingBase, Double> comparator;

    Sort(Function<EntityLivingBase, Double> comparator) {
      this.comparator = comparator;
    }

    public double compare(EntityLivingBase entity) {
      return comparator.apply(entity);
    }
  }

  private enum Click {
    BASIC,
    ADVANCED,
    UPDATED,
  }

  private enum AutoBlock {
    NONE,
    FAKE,
    HOLD,
    BASIC,
    VANILLA,
    WATCHDOG,
  }
}
