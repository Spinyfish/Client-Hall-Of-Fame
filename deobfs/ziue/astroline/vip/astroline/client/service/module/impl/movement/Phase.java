/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockHopper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 *  vip.astroline.client.service.event.impl.move.EventCollide
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vip.astroline.client.service.event.impl.move.EventCollide;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class Phase
extends Module {
    private int reset;

    public Phase() {
        super("Phase", Category.Movement, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        --this.reset;
        double xOff = 0.0;
        double zOff = 0.0;
        double multi = 2.6;
        double mx = Math.cos(Math.toRadians(Phase.mc.thePlayer.rotationYaw + 90.0f));
        double mz = Math.sin(Math.toRadians(Phase.mc.thePlayer.rotationYaw + 90.0f));
        xOff = (double)Phase.mc.thePlayer.moveForward * 1.6 * mx + (double)Phase.mc.thePlayer.moveStrafing * 1.6 * mz;
        zOff = (double)Phase.mc.thePlayer.moveForward * 1.6 * mz + (double)Phase.mc.thePlayer.moveStrafing * 1.6 * mx;
        if (Phase.isInsideBlock() && Phase.mc.thePlayer.isSneaking()) {
            this.reset = 1;
        }
        if (this.reset <= 0) return;
        Phase.mc.thePlayer.boundingBox.offsetAndUpdate(xOff, 0.0, zOff);
    }

    @EventTarget
    public boolean onCollision(EventCollide event) {
        if (!Phase.isInsideBlock() || !Phase.mc.gameSettings.keyBindJump.isKeyDown()) {
            if (Phase.isInsideBlock()) return true;
            if (event.getBoundingBox() == null) return true;
            if (!(event.getBoundingBox().maxY > Phase.mc.thePlayer.boundingBox.minY)) return true;
            if (!Phase.mc.thePlayer.isSneaking()) return true;
        }
        event.setBoundingBox(null);
        return true;
    }

    public static boolean isInsideBlock() {
        int x = MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.minX);
        block0: while (x < MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.maxX) + 1) {
            int y = MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.minY);
            while (true) {
                if (y < MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.maxY) + 1) {
                } else {
                    ++x;
                    continue block0;
                }
                for (int z = MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.minZ); z < MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.boundingBox.maxZ) + 1; ++z) {
                    Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block == null || block instanceof BlockAir) continue;
                    AxisAlignedBB boundingBox = block.getCollisionBoundingBox((World)Minecraft.getMinecraft().theWorld, new BlockPos(x, y, z), Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z)));
                    if (block instanceof BlockHopper) {
                        boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                    }
                    if (boundingBox == null || !Minecraft.getMinecraft().thePlayer.boundingBox.intersectsWith(boundingBox)) continue;
                    return true;
                }
                ++y;
            }
            break;
        }
        return false;
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
