package store.femboy.spring.impl.util;

import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import store.femboy.spring.impl.module.impl.combat.KillAura;

public class PlayerUtil extends Util {
    Minecraft mc = Minecraft.getMinecraft();
    private final RotationUtil utilRotation = new RotationUtil();
    private final EntityUtil utilEntity = new EntityUtil();

    public boolean isInLiquid() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            boolean inLiquid = false;
            AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
            int y = (int) playerBB.minY;

            for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
                for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    /*Thanks Error and Thorbn (Azura Devs)*/
    public double getColidingBoundingBoxesHeight() {

        double x = -Math.sin(mc.thePlayer.getDirection()) * 0.4;
        double z = Math.cos(mc.thePlayer.getDirection()) * 0.4;
        double height = 0;
        for (double i = 0.5; i <= 1.5 + 0.1; i += 0.1) {
            boolean step = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                    mc.thePlayer.getEntityBoundingBox().offset(x, i, z)).isEmpty();
            if (step) {
                height = i;
                break;
            }
        }
        return height;
    }

    public int getNearestGround(){
        for(int i = (int) Math.floor(mc.thePlayer.posY); i > -1; i--){
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)){
                return i;
            }
        }
        return -1;
    }

    public boolean onGround(double offset) { // offset is the max offset the player can be above ground
        BlockPos nearestGround = null;

        for (int i = (int) Math.ceil(mc.thePlayer.posY); i > 0; i--) {
            Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock();
            if (block != Blocks.air && block != Blocks.water && block != Blocks.lava && block != Blocks.flowing_water && block != Blocks.flowing_lava) {
                nearestGround = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
                break;
            }
        }

        if (nearestGround != null) {
            if (mc.thePlayer.posY <= (nearestGround.getY() + 1) + offset) {
                return true;
            }
        }

        return false;
    }

    public boolean offsetFormGround(double offset) { // offset is the min offset the player has to be above ground before returning false
        BlockPos nearestGround = null;

        for (int i = (int) Math.ceil(mc.thePlayer.posY); i > 0; i--) {
            Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock();
            if (block != Blocks.air && block != Blocks.water && block != Blocks.lava && block != Blocks.flowing_water && block != Blocks.flowing_lava) {
                nearestGround = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
                break;
            }
        }

        if (nearestGround != null) {
            if (mc.thePlayer.posY >= (nearestGround.getY() + 1) + offset) {
                return true;
            }
        }

        return false;
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public double getBaseJumpHeight() {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return 0.419999986886978d + 0.1 * (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1);
        }
        return 0.419999986886978d;
    }

    public String isWinningAgainst(@NotNull EntityLivingBase e) {
        if (utilEntity.getHealthPercentage(e) == utilEntity.getHealthPercentage(mc.thePlayer)) {
            return "Tied";
        } else if (utilEntity.getHealthPercentage(e) < utilEntity.getHealthPercentage(mc.thePlayer)) {
            return "Winning";
        }
        return "Loosing";
    }

    public boolean isOverVoid() {
        for (double posY = mc.thePlayer.posY; posY > 0.0; --posY) {
            if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInVoid() {
        double yaw = Math.toRadians(utilRotation.getYaw(KillAura.auraTarget.getPositionVector()));
        double xValue = -Math.sin(yaw) * 2;
        double zValue = Math.cos(yaw) * 2;
        for (int i = 0; i <= 256; i++) {
            BlockPos b = new BlockPos(mc.thePlayer.posX + xValue, mc.thePlayer.posY - i, mc.thePlayer.posZ + zValue);
            if (mc.theWorld.getBlockState(b).getBlock() instanceof BlockAir) {
                if (b.getY() == 0) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return !mc.thePlayer.isCollidedVertically && !mc.thePlayer.onGround && mc.thePlayer.fallDistance != 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.isAirBorne && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isPotionActive(Potion.invisibility.id);
    }

    public boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public boolean isOnHypixel() {
        if (mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null) {
            return false;
        }

        String ip = mc.getCurrentServerData().serverIP.toLowerCase();
        return ip.contains("hypixel.net") || ip.contains("2606:4700::6810:4e15");
    }
}
