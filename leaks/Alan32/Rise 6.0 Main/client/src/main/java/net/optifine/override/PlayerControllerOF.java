package net.optifine.override;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PlayerControllerOF extends PlayerControllerMP {
    private boolean acting = false;
    private BlockPos lastClickBlockPos = null;
    private Entity lastClickEntity = null;

    public PlayerControllerOF(final Minecraft mcIn, final NetHandlerPlayClient netHandler) {
        super(mcIn, netHandler);
    }

    /**
     * Called when the player is hitting a block with an item.
     *
     * @param loc  location of the block being clicked
     * @param face Blockface being clicked
     */
    public boolean clickBlock(final BlockPos loc, final EnumFacing face) {
        this.acting = true;
        this.lastClickBlockPos = loc;
        final boolean flag = super.clickBlock(loc, face);
        this.acting = false;
        return flag;
    }

    public boolean onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing) {
        this.acting = true;
        this.lastClickBlockPos = posBlock;
        final boolean flag = super.onPlayerDamageBlock(posBlock, directionFacing);
        this.acting = false;
        return flag;
    }

    /**
     * Notifies the server of things like consuming food, etc...
     */
    public boolean sendUseItem(final EntityPlayer player, final World worldIn, final ItemStack stack) {
        this.acting = true;
        final boolean flag = super.sendUseItem(player, worldIn, stack);
        this.acting = false;
        return flag;
    }

    public boolean onPlayerRightClick(final EntityPlayerSP p_178890_1, final WorldClient p_178890_2, final ItemStack p_178890_3, final BlockPos p_178890_4, final EnumFacing p_178890_5, final Vec3 p_178890_6) {
        this.acting = true;
        this.lastClickBlockPos = p_178890_4;
        final boolean flag = super.onPlayerRightClick(p_178890_1, p_178890_2, p_178890_3, p_178890_4, p_178890_5, p_178890_6);
        this.acting = false;
        return flag;
    }

    /**
     * Send packet to server - player is interacting with another entity (left click)
     */
    public boolean interactWithEntitySendPacket(final EntityPlayer player, final Entity target) {
        this.lastClickEntity = target;
        return super.interactWithEntitySendPacket(player, target);
    }

    public boolean func_178894_a(final EntityPlayer player, final Entity target, final MovingObjectPosition ray) {
        this.lastClickEntity = target;
        return super.func_178894_a(player, target, ray);
    }

    public boolean isActing() {
        return this.acting;
    }

    public BlockPos getLastClickBlockPos() {
        return this.lastClickBlockPos;
    }

    public Entity getLastClickEntity() {
        return this.lastClickEntity;
    }
}
