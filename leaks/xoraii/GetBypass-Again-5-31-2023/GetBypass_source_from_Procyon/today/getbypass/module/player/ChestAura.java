// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.events.listeners.WorldChangedEvent;
import java.util.Iterator;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3i;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.gui.inventory.GuiContainer;
import today.getbypass.module.Category;
import net.minecraft.util.BlockPos;
import java.util.ArrayList;
import today.getbypass.module.Module;

public final class ChestAura extends Module
{
    private final ArrayList<BlockPos> clickedChests;
    private BlockPos chestToOpen;
    
    public ChestAura() {
        super("ChestAura", 0, "Opens nearby chests for you", Category.PLAYER);
        this.clickedChests = new ArrayList<BlockPos>();
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (this.mc.currentScreen instanceof GuiContainer) {
                return;
            }
            if (this.chestToOpen != null && this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.inventory.getCurrentItem(), this.chestToOpen, EnumFacing.DOWN, new Vec3(this.chestToOpen))) {
                this.mc.thePlayer.swingItem();
                this.chestToOpen = null;
                return;
            }
            for (final TileEntity tileEntity : this.mc.theWorld.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityChest && this.mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) < 6.0) {
                    if (this.clickedChests.contains(tileEntity.getPos())) {
                        continue;
                    }
                    final TileEntityChest chest = (TileEntityChest)tileEntity;
                    this.clickedChests.add(chest.getPos());
                    this.chestToOpen = chest.getPos();
                }
            }
        }
    }
    
    public void onWorldChanged(final WorldChangedEvent event) {
        this.clickedChests.clear();
    }
    
    @Override
    public void onEnable() {
        this.clickedChests.clear();
        this.chestToOpen = null;
    }
}
