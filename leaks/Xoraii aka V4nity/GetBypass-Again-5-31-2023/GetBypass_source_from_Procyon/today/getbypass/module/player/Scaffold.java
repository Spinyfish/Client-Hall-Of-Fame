// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Scaffold extends Module
{
    private static boolean cooldown;
    
    static {
        Scaffold.cooldown = false;
    }
    
    public Scaffold() {
        super("Scaffold", 0, "Place block under you automatically", Category.PLAYER);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            final BlockPos playerBlock = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY, this.mc.thePlayer.posZ);
            if (this.mc.theWorld.isAirBlock(playerBlock.add(0, -1, 0))) {
                if (this.isValidBlock(playerBlock.add(0, -2, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.UP);
                }
                else if (this.isValidBlock(playerBlock.add(-1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
                }
                else if (this.isValidBlock(playerBlock.add(1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
                }
                else if (this.isValidBlock(playerBlock.add(0, -1, -1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
                }
                else if (this.isValidBlock(playerBlock.add(0, -1, -1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
                }
                else if (this.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (this.isValidBlock(playerBlock.add(0, -1, -1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
                    }
                    this.place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
                }
                else if (this.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (this.isValidBlock(playerBlock.add(-1, -1, 0))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.WEST);
                    }
                    this.place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
                }
                else if (this.isValidBlock(playerBlock.add(-1, -1, -1))) {
                    if (this.isValidBlock(playerBlock.add(0, -1, -1))) {
                        this.place(playerBlock.add(0, -1, -1), EnumFacing.SOUTH);
                    }
                    this.place(playerBlock.add(-1, -1, 1), EnumFacing.WEST);
                }
                else if (this.isValidBlock(playerBlock.add(1, -1, -1))) {
                    if (this.isValidBlock(playerBlock.add(1, -1, 0))) {
                        this.place(playerBlock.add(1, -1, 0), EnumFacing.EAST);
                    }
                    this.place(playerBlock.add(1, -1, -1), EnumFacing.NORTH);
                }
            }
        }
    }
    
    private boolean isValidBlock(final BlockPos pos) {
        final Block b = this.mc.theWorld.getBlockState(pos).getBlock();
        return !(b instanceof BlockLiquid) && b.getMaterial() != Material.air;
    }
    
    private void place(BlockPos pos, final EnumFacing face) {
        Scaffold.cooldown = true;
        if (face == EnumFacing.UP) {
            pos = pos.add(0, -1, 0);
        }
        else if (face == EnumFacing.NORTH) {
            pos = pos.add(0, 0, 1);
        }
        else if (face == EnumFacing.EAST) {
            pos = pos.add(-1, 0, 0);
        }
        else if (face == EnumFacing.SOUTH) {
            pos = pos.add(0, 0, -1);
        }
        else if (face == EnumFacing.WEST) {
            pos.add(1, 0, 0);
        }
        if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            this.mc.thePlayer.swingItem();
            this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.getHeldItem(), pos, face, new Vec3(0.5, 0.5, 0.5));
            final double var4 = pos.getX() + 0.25 - this.mc.thePlayer.posX;
            final double var5 = pos.getZ() + 0.25 - this.mc.thePlayer.posZ;
            final double var6 = pos.getY() + 0.25 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
            final double var7 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
            final float yaw = (float)(Math.atan2(var5, var4) * 180.0 / 3.141592653689793) - 90.0f;
            final float pitch = (float)(Math.atan2(var6, var7) * 180.0 / 3.141592653689793);
            int ticks = 0;
            if (++ticks >= 1000) {
                ticks = 0;
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, yaw, pitch, this.mc.thePlayer.onGround));
            }
        }
    }
}
