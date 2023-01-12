package dev.eternal.client.util.combat;

import dev.eternal.client.util.movement.data.Rotation;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class CombatUtil {

  private final Minecraft minecraftInstance = Minecraft.getMinecraft();

  /**
   * @param itemStack  Item to check
   * @param responsive Idk lol
   * @return The hit delay in 1.9.
   * @author Hacking_Lord
   */
  public static long getAttackSpeed(final ItemStack itemStack, final boolean responsive) {
    double baseSpeed = 250;
    if (!responsive) {
      return Long.MAX_VALUE;
    } else if (itemStack != null) {
      if (itemStack.getItem() instanceof ItemSword) {
        baseSpeed = 625;
      }
      if (itemStack.getItem() instanceof ItemSpade) {
        baseSpeed = 1000;
      }
      if (itemStack.getItem() instanceof ItemPickaxe) {
        baseSpeed = 833.333333333333333;
      }
      if (itemStack.getItem() instanceof ItemAxe) {
        if (itemStack.getItem() == Items.wooden_axe) {
          baseSpeed = 1250;
        }
        if (itemStack.getItem() == Items.stone_axe) {
          baseSpeed = 1250;
        }
        if (itemStack.getItem() == Items.iron_axe) {
          baseSpeed = 1111.111111111111111;
        }
        if (itemStack.getItem() == Items.diamond_axe) {
          baseSpeed = 1000;
        }
        if (itemStack.getItem() == Items.golden_axe) {
          baseSpeed = 1000;
        }
      }
      if (itemStack.getItem() instanceof ItemHoe) {
        if (itemStack.getItem() == Items.wooden_hoe) {
          baseSpeed = 1000;
        }
        if (itemStack.getItem() == Items.stone_hoe) {
          baseSpeed = 500;
        }
        if (itemStack.getItem() == Items.iron_hoe) {
          baseSpeed = 333.333333333333333;
        }
        if (itemStack.getItem() == Items.diamond_hoe) {
          baseSpeed = 250;
        }
        if (itemStack.getItem() == Items.golden_hoe) {
          baseSpeed = 1000;
        }
      }
    }
    if (minecraftInstance.thePlayer.isPotionActive(Potion.digSpeed)) {
      baseSpeed *= 1.0 + 0.1 * (minecraftInstance.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1);
    }
    return Math.round(baseSpeed);
  }

  public Rotation getAngleForEntityStatic(EntityLivingBase entityLivingBase) {
    EntityPlayerSP playerInstance = minecraftInstance.thePlayer;
    double diffX = entityLivingBase.posX - playerInstance.posX;
    double diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.8 - (playerInstance.posY + playerInstance.getEyeHeight());
    double diffZ = entityLivingBase.posZ - playerInstance.posZ;
    double dist = Math.hypot(diffX, diffZ);
    float sensitivity = minecraftInstance.gameSettings.mouseSensitivity * 0.6F + 0.2F;
    float gcd = sensitivity * sensitivity * sensitivity * 1.2F;
    float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI - 90F);
    float pitch = MathHelper.clamp_float((float) -(Math.atan2(diffY, dist) * 180D / Math.PI), -90F, 90F);
    yaw -= yaw % gcd;
    pitch -= pitch % gcd;
    return new Rotation(yaw, pitch);
  }

  public Rotation getAngleForEntityRandomized(EntityLivingBase entityLivingBase) {
    EntityPlayerSP playerInstance = minecraftInstance.thePlayer;
    double diffX = entityLivingBase.posX - playerInstance.posX + ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
    double diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.8 - (playerInstance.posY + playerInstance.getEyeHeight()) + ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
    double diffZ = entityLivingBase.posZ - playerInstance.posZ + ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
    double dist = Math.hypot(diffX, diffZ);
    float sensitivity = minecraftInstance.gameSettings.mouseSensitivity * 0.6F + 0.2F;
    float gcd = sensitivity * sensitivity * sensitivity * 1.2F;
    float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI - 90F);
    float pitch = MathHelper.clamp_float((float) -(Math.atan2(diffY, dist) * 180D / Math.PI), -90F, 90F);
    yaw -= yaw % gcd;
    pitch -= pitch % gcd;
    return new Rotation(yaw, pitch);
  }

  public Rotation getAngleForEntityRandomized(EntityLivingBase entityLivingBase, MouseFilter yawFilter, MouseFilter pitchFilter) {
    EntityPlayerSP playerInstance = minecraftInstance.thePlayer;
    double diffX = entityLivingBase.posX - playerInstance.posX;
    double diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.8 - (playerInstance.posY + playerInstance.getEyeHeight());
    double diffZ = entityLivingBase.posZ - playerInstance.posZ;
    double dist = Math.hypot(diffX, diffZ);
    var atan2 = Math.atan2(diffZ, diffX);
    float yaw = (float) (atan2 * 180D / Math.PI - 90F);
    float pitch = MathHelper.clamp_float((float) -(Math.atan2(diffY, dist) * 180D / Math.PI + Math.random()), -90F, 90F);
    return new Rotation(yawFilter.smooth(yaw, (float) (minecraftInstance.gameSettings.mouseSensitivity + Math.random() * 0.2)), pitchFilter.smooth(pitch, (float) (minecraftInstance.gameSettings.mouseSensitivity + Math.random() * 0.3)));
  }
}
