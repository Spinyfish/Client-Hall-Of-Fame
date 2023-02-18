/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C07PacketPlayerDigging
 *  net.minecraft.network.play.client.C07PacketPlayerDigging$Action
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.other.DelayTimer
 */
package vip.astroline.client.service.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.other.DelayTimer;

public class Nuker
extends Module {
    public DelayTimer delay = new DelayTimer();

    public Nuker() {
        super("Nuker", Category.Player, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (Nuker.mc.theWorld == null) {
            return;
        }
        int radius = 1;
        int y = 1;
        while (y >= -radius) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos = new BlockPos(Nuker.mc.thePlayer.posX - 0.5 + (double)x, Nuker.mc.thePlayer.posY - 0.5 + (double)y, Nuker.mc.thePlayer.posZ - 0.5 + (double)z);
                    Block block = Nuker.mc.theWorld.getBlockState(pos).getBlock();
                    if (this.getFacingDirection(pos) == null || block instanceof BlockAir) continue;
                    this.eraseBlock(pos, this.getFacingDirection(pos));
                }
            }
            --y;
        }
    }

    private void eraseBlock(BlockPos pos, EnumFacing facing) {
        Nuker.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
        Nuker.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!Nuker.mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.UP;
        } else if (!Nuker.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.DOWN;
        } else if (!Nuker.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.EAST;
        } else if (!Nuker.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.WEST;
        } else if (!Nuker.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.SOUTH;
        } else {
            if (Nuker.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube()) return direction;
            direction = EnumFacing.NORTH;
        }
        return direction;
    }
}
