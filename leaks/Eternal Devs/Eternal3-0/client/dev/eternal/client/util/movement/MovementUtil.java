package dev.eternal.client.util.movement;

import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventAbsoluteMove;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.impl.combat.Aura;
import dev.eternal.client.module.impl.combat.TargetStrafe;
import dev.eternal.client.util.combat.CombatUtil;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.movement.data.Motion;
import dev.eternal.client.util.movement.data.Position;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.network.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class MovementUtil {

  private final Client client = Client.singleton();
  private final Minecraft MC = Minecraft.getMinecraft();

  public static float getInputAngle() {
    float dX = MathHelper.sin(MC.thePlayer.rotationYaw * MathUtil.DEG_TO_RAD);
    float dZ = MathHelper.cos(MC.thePlayer.rotationYaw * MathUtil.DEG_TO_RAD);
    float velX = MC.thePlayer.moveStrafing * dZ - MC.thePlayer.moveForward * dX;
    float velZ = MC.thePlayer.moveForward * dZ + MC.thePlayer.moveStrafing * dX;
    return (float) (Math.atan2(velZ, velX) * MathUtil.RAD_TO_DEG - 90.0F);
  }

  private static float lastStrafe;
  private static float lastForward;

  public static BlockPos getGroundPos() {
    Minecraft mc = MC;
    BlockPos returns = null;
    for (double i = MC.thePlayer.posY; i > 0; i--) {
      if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
        returns = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
      }
    }
    return returns;
  }

  public static boolean isOverVoid() {
    Minecraft mc = MC;
    boolean toReturn = true;
    for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
      if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
        toReturn = false;
      }
    }
    return toReturn;
  }

  /**
   * balls
   */
  public static void setTeleportForward(double hDist) {
    EntityPlayerSP sp = MC.thePlayer;
    float yaw = sp.rotationYaw;
    double strafe = sp.moveStrafing, forward = sp.moveForward;
    yaw = getTransformedYaw(yaw, forward, strafe);
    double cos = Math.cos(Math.toRadians(yaw + 90.0F));
    double sin = Math.sin(Math.toRadians(yaw + 90.0F));
    sp.setPosition(sp.posX + cos * hDist, sp.posY, sp.posZ + sin * hDist);
  }

  public static void setVClip(double vDist) {
    EntityPlayerSP sp = MC.thePlayer;
    sp.setPosition(sp.posX, sp.posY + vDist, sp.posZ);
  }

  public static void setHClip(double hDist) {
    EntityPlayerSP sp = MC.thePlayer;
    float yaw = sp.rotationYaw;
    double cos = Math.cos(Math.toRadians(yaw + 90.0F));
    double sin = Math.sin(Math.toRadians(yaw + 90.0F));
    sp.setPosition(sp.posX + cos * hDist, sp.posY, sp.posZ + sin * hDist);
  }

  public static void sendPacketForward(double hDist, double posAdd) {
    EntityPlayerSP sp = MC.thePlayer;
    float yaw = sp.rotationYaw;
    double strafe = sp.moveStrafing, forward = sp.moveForward;
    yaw = getTransformedYaw(yaw, forward, strafe);
    double cos = Math.cos(Math.toRadians(yaw + 90.0F));
    double sin = Math.sin(Math.toRadians(yaw + 90.0F));
    if (MovementUtil.isMoving())
      PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(sp.posX + cos * hDist, sp.posY + posAdd, sp.posZ + sin * hDist, sp.onGround));
  }

  public static void setCustomMotion(EventMove eventMove, double hDist, float yaw, double strafe, double forward) {
    EntityPlayerSP sp = MC.thePlayer;
    Motion motion = getMotion(hDist, forward, strafe, yaw);
    sp.motionX = motion.motionX();
    sp.motionZ = motion.motionZ();
  }

  public float getTransformedYaw(float rotationYaw, double moveForward, double moveStrafe) {
    if (moveStrafe > 0) {
      moveStrafe = 1;
    } else if (moveStrafe < 0) {
      moveStrafe = -1;
    }
    if (moveForward != 0.0D) {
      if (moveStrafe > 0.0D) {
        rotationYaw += moveForward > 0.0D ? -45 : 45;
      } else if (moveStrafe < 0.0D) {
        rotationYaw += moveForward > 0.0D ? 45 : -45;
      }
      moveStrafe = 0.0D;
      if (moveForward > 0.0D) {
        moveForward = 1.0D;
      } else if (moveForward < 0.0D) {
        moveForward = -1.0D;
      }
    }
    return rotationYaw;
  }

  /**
   * Performs move fix on event.
   * The event must already have been given the current serverside rotationYaw through
   * {@link EventMove#yaw(float)}
   *
   * @param event event to perform move fix on
   * @return whether sprinting is possible
   */
  public static boolean performMoveFix(EventMove event) {
    final float walkAngle = getInputAngle();
    final float diff = MathHelper.wrapAngleTo180_float(event.yaw() - walkAngle);

    final int n = Math.round(Math.abs(diff) / 45.0F);
    final int m = Math.round(diff / 45.0F);

    // whether strafing keys are pressed
    final int strafeMag = n != 0 && n != 4 ? 1 : 0;
    final int forwardMag = n != 2 ? 1 : 0;

    // whether it's the positive of negative one of those (W or S, A or D)
    final int forwardSign = n < 2 ? 1 : -1;
    final int strafeSign = m > 0 && m < 4 ? 1 : -1;

    final float mag = Math.max(Math.abs(event.forward()), Math.abs(event.strafe()));

    lastStrafe = MathHelper.cap_mag_float(lastStrafe + strafeSign, 1.0F);
    lastForward = MathHelper.cap_mag_float(lastForward + forwardSign, 1.0F);

    event.strafe(lastStrafe * strafeMag * mag);
    event.forward(lastForward * forwardMag * mag);

    return forwardSign > 0;
  }

  public static boolean checkMoveFix(EventMove event) {
    final float walkAngle = getInputAngle();
    final float diff = MathHelper.wrapAngleTo180_float(event.yaw() - walkAngle);

    final int n = Math.round(Math.abs(diff) / 45.0F);
    final int forwardSign = n < 2 ? 1 : -1;

    return forwardSign > 0;
  }


  /**
   * @param ticks self-explanatory
   * @return The predicted position of the plays after n ticks
   */
  public static Vec3 getFuturePosition(int ticks) {
    final EntityPlayerSP player = MC.thePlayer;
    final double futureX = (player.posX - player.prevPosX) * ticks;
    final double futureY = (player.posY - player.prevPosY) * ticks;
    final double futureZ = (player.posZ - player.prevPosZ) * ticks;
    return new Vec3(0, 0, 0)
        .add(player.getPositionFeet(1.0F))
        .addVector(futureX, futureY, futureZ);
  }

  public Motion getMotion(double hDist, double moveForward, double moveStrafe, float rotationYaw) {
    if (moveForward != 0.0D || moveStrafe != 0.0D) {
      if (moveStrafe > 0) {
        moveStrafe = 1;
      } else if (moveStrafe < 0) {
        moveStrafe = -1;
      }
      if (moveForward != 0.0D) {
        if (moveStrafe > 0.0D) {
          rotationYaw += moveForward > 0.0D ? -45 : 45;
        } else if (moveStrafe < 0.0D) {
          rotationYaw += moveForward > 0.0D ? 45 : -45;
        }
        moveStrafe = 0.0D;
        if (moveForward > 0.0D) {
          moveForward = 1.0D;
        } else if (moveForward < 0.0D) {
          moveForward = -1.0D;
        }
      }
      double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
      double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
      return new Motion(moveForward * hDist * cos
          + moveStrafe * hDist * sin, 0, moveForward * hDist * sin
          - moveStrafe * hDist * cos);
    }
    return new Motion(0, 0, 0);
  }

  public Motion getMotion(double hDist) {
    EntityPlayerSP playerInstance = MC.thePlayer;

    MovementInput movementInput = playerInstance.movementInput;

    double moveForward = movementInput.moveForward;
    double moveStrafe = movementInput.moveStrafe;

    float rotationYaw = playerInstance.rotationYaw;

    if (moveForward != 0.0D || moveStrafe != 0.0D) {
      if (moveStrafe > 0) {
        moveStrafe = 1;
      } else if (moveStrafe < 0) {
        moveStrafe = -1;
      }
      if (moveForward != 0.0D) {
        if (moveStrafe > 0.0D) {
          rotationYaw += moveForward > 0.0D ? -45 : 45;
        } else if (moveStrafe < 0.0D) {
          rotationYaw += moveForward > 0.0D ? 45 : -45;
        }
        moveStrafe = 0.0D;
        if (moveForward > 0.0D) {
          moveForward = 1.0D;
        } else if (moveForward < 0.0D) {
          moveForward = -1.0D;
        }
      }
      double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
      double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
      return new Motion(moveForward * hDist * cos
          + moveStrafe * hDist * sin, 0, moveForward * hDist * sin
          - moveStrafe * hDist * cos);
    }
    return new Motion(0, 0, 0);
  }

  public void setMotion(double hDist) {
    EntityPlayerSP playerInstance = MC.thePlayer;

    MovementInput movementInput = playerInstance.movementInput;
    double moveForward = movementInput.moveForward;
    double moveStrafe = movementInput.moveStrafe;

    final Aura aura = client.moduleManager().getByClass(Aura.class);
    final TargetStrafe targetStrafe = client.moduleManager().getByClass(TargetStrafe.class);
    final EntityLivingBase currentTarget = targetStrafe.target();

    Motion motion;

    if (aura.isEnabled() && targetStrafe.isEnabled() && currentTarget != null) {
      final Rotation rotation = CombatUtil.getAngleForEntityRandomized(currentTarget);
      final float distance = currentTarget.getDistanceToEntity(playerInstance);
      final float currentForwardInput = distance >= aura.reach().value() * 0.65 ? 1F : 0F;
      motion = getMotion(hDist, currentForwardInput, targetStrafe.currentStrafeInput(), rotation.rotationYaw());
    } else motion = getMotion(hDist, moveForward, moveStrafe, playerInstance.rotationYaw);
    playerInstance.motionX = motion.motionX();
    playerInstance.motionZ = motion.motionZ();
  }

  public void setMotionPacket(double hDist, int packets, double incrementPerPacket) {
    setMotion(hDist);
    EntityPlayerSP playerInstance = MC.thePlayer;

    final Aura aura = client.moduleManager().getByClass(Aura.class);
    final TargetStrafe targetStrafe = client.moduleManager().getByClass(TargetStrafe.class);
    final EntityLivingBase currentTarget = (EntityLivingBase) aura.target();

    for (int i = 0; i < packets; i++) {
      if (i % 4 == 0) {
        PacketUtil.sendSilent(new C08PacketPlayerBlockPlacement(new BlockPos(playerInstance).down(12), 256, new ItemStack(Blocks.stone), 0, 0, 0));
      }
      Motion motion = getMotion(hDist);
      if (aura.isEnabled() && targetStrafe.isEnabled() && currentTarget != null) {
        final Rotation rotation = CombatUtil.getAngleForEntityRandomized(currentTarget);
        final float distance = currentTarget.getDistanceToEntity(playerInstance);
        final float currentForwardInput = distance >= aura.reach().value() * 0.6F ? 1F : 0F;
        motion = getMotion(hDist, currentForwardInput, targetStrafe.currentStrafeInput(), rotation.rotationYaw());
      }
      playerInstance.moveEntity(motion.motionX(), 0, motion.motionZ());
      PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(playerInstance.posX,
          playerInstance.posY, playerInstance.posZ, playerInstance.rotationYaw, playerInstance.rotationPitch, true));
    }
  }

  public List<Position> getPathToPos(Position pos) {
    return Position.findPath(100, new Position(MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ), pos, false);
  }

  public void teleportAttack(EntityLivingBase entityLivingBase, double hDist, boolean noFallCompat) {
    List<Position> positionList = Position.findPath(hDist,
        new Position(MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ),
        new Position(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ), false);
    int i = 0;
    for (Position position : positionList) {
      PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(position.posX(), position.posY(), position.posZ(), i++ % 2 == 0 && noFallCompat));
    }
    MC.thePlayer.swingItem();
    PacketUtil.sendSilent(new C02PacketUseEntity(entityLivingBase, C02PacketUseEntity.Action.ATTACK));
    Collections.reverse(positionList);
    for (Position position : positionList) {
      PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(position.posX(), position.posY(), position.posZ(), i++ % 2 == 0 && noFallCompat));
    }
  }

  public void setMotion(EventMove handler, double hDist) {
    EntityPlayerSP playerInstance = MC.thePlayer;
    playerInstance.motionX = 0;
    playerInstance.motionZ = 0;
    handler.friction((float) hDist);
  }

  public void setMotion(EventAbsoluteMove handler, double hDist) {
    EntityPlayerSP playerInstance = MC.thePlayer;

    MovementInput movementInput = playerInstance.movementInput;
    double moveForward = movementInput.moveForward;
    double moveStrafe = movementInput.moveStrafe;

    final Aura aura = client.moduleManager().getByClass(Aura.class);
    final TargetStrafe targetStrafe = client.moduleManager().getByClass(TargetStrafe.class);
    final EntityLivingBase currentTarget = (EntityLivingBase) aura.target();

    Motion motion;

    if (aura.isEnabled() && targetStrafe.isEnabled() && currentTarget != null) {
      final Rotation rotation = CombatUtil.getAngleForEntityRandomized(currentTarget);
      final float distance = currentTarget.getDistanceToEntity(playerInstance);
      final float currentForwardInput = distance >= aura.reach().value() * 0.6F ? 1F : 0F;
      motion = getMotion(hDist, currentForwardInput, targetStrafe.currentStrafeInput(), rotation.rotationYaw());
    } else motion = getMotion(hDist, moveForward, moveStrafe, playerInstance.rotationYaw);
    handler.x(motion.motionX());
    handler.z(motion.motionZ());
  }

  /**
   * Sets your motion to specified speed with a custom strafe component.
   *
   * @author Dort.
   */
  public void setMotionCustomStrafe(EventMove handler, double hDist, double strafeComponent, boolean safe) {
    float remainder = 1.0F - (float) strafeComponent;
    if (safe && isMovingOnGround())
      setMotion(hDist);
    else {
      MC.thePlayer.motionX *= strafeComponent;
      MC.thePlayer.motionZ *= strafeComponent;
      handler.friction((float) (hDist * remainder));
    }
  }

  public boolean isMoving() {
    final Aura aura = client.moduleManager().getByClass(Aura.class);
    final TargetStrafe targetStrafe = client.moduleManager().getByClass(TargetStrafe.class);
    final EntityLivingBase currentTarget = (EntityLivingBase) aura.target();
    MovementInput movementInputInstance = MC.thePlayer.movementInput;
    return movementInputInstance.moveForward != 0 || movementInputInstance.moveStrafe != 0 || (aura.isEnabled() && targetStrafe.isEnabled() && currentTarget != null);
  }

  public boolean isMovingOnGround() {
    EntityPlayerSP playerInstance = MC.thePlayer;
    return isMoving() && playerInstance.onGround;
  }


  public double getGroundPosition() {
    EntityPlayerSP playerInstance = MC.thePlayer;
    WorldClient worldInstance = MC.theWorld;
    for (double yPosition = playerInstance.posY; yPosition > 0; yPosition--) {
      BlockPos blockPos = new BlockPos(playerInstance);
      if (worldInstance.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
        return yPosition;
      }
    }
    throw new RuntimeException("haha get fucked");
  }

  /**
   * @author People who made NCP even worse.
   */
  public double getUpdatedNCPBaseSpeed() {
    double base = 0.2873;
    EntityPlayerSP playerInstance = MC.thePlayer;
    Potion speedPotion = Potion.moveSpeed;
    if (playerInstance.isPotionActive(speedPotion)) {
      PotionEffect speedPotionEffect = playerInstance.getActivePotionEffect(speedPotion);
      base *= 1.0 + 0.05 * (speedPotionEffect.getAmplifier() + 1);
    }
    return base;
  }


  /**
   * @author NCP Devs.
   */
  public double getBaseGroundSpeed() {
    if (MC.thePlayer == null)
      return -1;
    double base = 0.2873;
    EntityPlayerSP playerInstance = MC.thePlayer;
    Potion speedPotion = Potion.moveSpeed;
    if (playerInstance.isPotionActive(speedPotion)) {
      PotionEffect speedPotionEffect = playerInstance.getActivePotionEffect(speedPotion);
      base *= 1.0 + 0.2 * (speedPotionEffect.getAmplifier() + 1);
    }
    return base;
  }

  public double getJumpMotion(double motionY) {
    EntityPlayerSP playerInstance = MC.thePlayer;
    Potion jumpPotion = Potion.jump;
    if (playerInstance.isPotionActive(jumpPotion)) {
      PotionEffect jumpPotionEffect = playerInstance.getActivePotionEffect(jumpPotion);
      motionY += (jumpPotionEffect.getAmplifier() + 1) * 0.1F;
    }
    return motionY;
  }

  public double getPotionSpeed(double base) {
    EntityPlayerSP playerInstance = MC.thePlayer;
    Potion speedPotion = Potion.moveSpeed;
    if (playerInstance.isPotionActive(speedPotion)) {
      PotionEffect speedPotionEffect = playerInstance.getActivePotionEffect(speedPotion);
      base *= 1.0 + 0.2 * (speedPotionEffect.getAmplifier() + 1);
    }
    return base;
  }
}
