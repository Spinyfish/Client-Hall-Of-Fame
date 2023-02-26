package ooo.cpacket.ruby.util;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityHelper
{
    private static Minecraft mc;
    
    static {
        EntityHelper.mc = Minecraft.getMinecraft();
    }
    
    public static int getBestWeapon(final Entity target) {
        final int originalSlot = EntityHelper.mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        for (byte slot = 0; slot < 9; ++slot) {
            EntityHelper.mc.thePlayer.inventory.currentItem = slot;
            final ItemStack itemStack = EntityHelper.mc.thePlayer.getHeldItem();
            if (itemStack != null) {
                float damage = getItemDamage(itemStack);
                damage += EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED);
                if (damage > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }
    
    public static float[] getFacingRotations(final int x, final int y, final int z, final EnumFacing facing) {
        final Entity temp = new EntitySnowball(EntityHelper.mc.theWorld);
        temp.posX = x + 0.5;
        temp.posY = y + 0.5;
        temp.posZ = z + 0.5;
        final Entity entity4;
        final Entity entity = entity4 = temp;
        entity4.posX += facing.getDirectionVec().getX() * 0.25;
        final Entity entity5;
        final Entity entity2 = entity5 = temp;
        entity5.posY += facing.getDirectionVec().getY() * 0.25;
        final Entity entity6;
        final Entity entity3 = entity6 = temp;
        entity6.posZ += facing.getDirectionVec().getZ() * 0.25;
        return EntityHelper.getEntityRotations(mc.thePlayer, temp);
    }
    
    public static float getYawChangeToEntity(final Entity entity) {
        final double deltaX = entity.posX - EntityHelper.mc.thePlayer.posX;
        final double deltaZ = entity.posZ - EntityHelper.mc.thePlayer.posZ;
        double yawToEntity = 0.0;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            final double yawToEntity2 = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else {
            Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(EntityHelper.mc.thePlayer.rotationYaw - (float)yawToEntity));
    }
    
    public static float getPitchChangeToEntity(final Entity entity) {
        final double deltaX = entity.posX - EntityHelper.mc.thePlayer.posX;
        final double deltaZ = entity.posZ - EntityHelper.mc.thePlayer.posZ;
        final double deltaY = entity.posY - 1.6 + entity.getEyeHeight() - EntityHelper.mc.thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(EntityHelper.mc.thePlayer.rotationPitch - (float)pitchToEntity);
    }
    
    public static float[] getAngles(final Entity e) {
        return new float[] { getYawChangeToEntity(e) + EntityHelper.mc.thePlayer.rotationYaw, getPitchChangeToEntity(e) + EntityHelper.mc.thePlayer.rotationPitch };
    }
    
    public static float[] getEntityRotations(final EntityPlayer player, final Entity target) {
        final double posX = target.posX - player.posX;
        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight());
        final double posZ = target.posZ - player.posZ;
        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        final float yaw = (float)(Math.atan2(posZ, posX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(posY, var14) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }
    
    private static float getItemDamage(final ItemStack itemStack) {
        final Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty()) {
            final Iterator iterator = multimap.entries().iterator();
            if (iterator.hasNext()) {
                final Map.Entry entry = (Entry) iterator.next();
                final AttributeModifier attributeModifier = (AttributeModifier) entry.getValue();
                double damage;
                if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
                    damage = attributeModifier.getAmount();
                }
                else {
                    damage = attributeModifier.getAmount() * 100.0;
                }
                if (attributeModifier.getAmount() > 1.0) {
                    return 1.0f + (float)damage;
                }
                return 1.0f;
            }
        }
        return 1.0f;
    }
    
    private static double directionCheck(final double sourceX, final double sourceY, final double sourceZ, final double dirX, final double dirY, final double dirZ, final double targetX, final double targetY, final double targetZ, final double targetWidth, final double targetHeight, final double precision) {
        double dirLength = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (dirLength == 0.0) {
            dirLength = 1.0;
        }
        final double dX = targetX - sourceX;
        final double dY = targetY - sourceY;
        final double dZ = targetZ - sourceZ;
        final double targetDist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        final double xPrediction = targetDist * dirX / dirLength;
        final double yPrediction = targetDist * dirY / dirLength;
        final double zPrediction = targetDist * dirZ / dirLength;
        double off = 0.0;
        off += Math.max(Math.abs(dX - xPrediction) - (targetWidth / 2.0 + precision), 0.0);
        off += Math.max(Math.abs(dZ - zPrediction) - (targetWidth / 2.0 + precision), 0.0);
        off += Math.max(Math.abs(dY - yPrediction) - (targetHeight / 2.0 + precision), 0.0);
        if (off > 1.0) {
            off = Math.sqrt(off);
        }
        return off;
    }
    
    public static double getDirectionCheckVal(final Entity e, final Vec3 lookVec) {
        return directionCheck(EntityHelper.mc.thePlayer.posX, EntityHelper.mc.thePlayer.posY + EntityHelper.mc.thePlayer.getEyeHeight(), EntityHelper.mc.thePlayer.posZ, lookVec.xCoord, lookVec.yCoord, lookVec.zCoord, e.posX, e.posY + e.height / 2.0, e.posZ, e.width, e.height, 5.0);
    }
    
    public static Float[] getEntityRotations2(final EntityPlayer player, final Entity target) {
        final double posX = target.posX - player.posX;
        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight() + 0.5);
        final double posZ = target.posZ - player.posZ;
        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        final float yaw = (float)(Math.atan2(posZ, posX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(posY, var14) * 180.0 / 3.141592653589793));
        return new Float[] { yaw, pitch };
    }
}
