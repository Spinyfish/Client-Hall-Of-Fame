package optifine;

import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.util.*;

public class PlayerControllerOF extends PlayerControllerMP
{
    private boolean acting;
    
    public PlayerControllerOF(final Minecraft p_i73_1_, final NetHandlerPlayClient p_i73_2_) {
        super(p_i73_1_, p_i73_2_);
        this.acting = false;
    }
    
    @Override
    public boolean clickBlock(final BlockPos loc, final EnumFacing face) {
        this.acting = true;
        final boolean flag = super.clickBlock(loc, face);
        this.acting = false;
        return flag;
    }
    
    @Override
    public boolean onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing) {
        this.acting = true;
        final boolean flag = super.onPlayerDamageBlock(posBlock, directionFacing);
        this.acting = false;
        return flag;
    }
    
    @Override
    public boolean sendUseItem(final EntityPlayer playerIn, final World worldIn, final ItemStack itemStackIn) {
        this.acting = true;
        final boolean flag = super.sendUseItem(playerIn, worldIn, itemStackIn);
        this.acting = false;
        return flag;
    }
    
    @Override
    public boolean onPlayerRightClick(final EntityPlayerSP player, final WorldClient worldIn, final ItemStack heldStack, final BlockPos hitPos, final EnumFacing side, final Vec3 hitVec) {
        this.acting = true;
        final boolean flag = super.onPlayerRightClick(player, worldIn, heldStack, hitPos, side, hitVec);
        this.acting = false;
        return flag;
    }
    
    public boolean isActing() {
        return this.acting;
    }
}
