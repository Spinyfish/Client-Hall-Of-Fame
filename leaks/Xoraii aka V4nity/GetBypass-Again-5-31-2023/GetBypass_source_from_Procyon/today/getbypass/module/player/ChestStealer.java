// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import today.getbypass.module.Category;
import today.getbypass.utils.Timer2;
import today.getbypass.module.Module;

public class ChestStealer extends Module
{
    public Timer2 time;
    
    public ChestStealer() {
        super("ChestStealer", 0, "Steals everything from chests.", Category.PLAYER);
        this.time = new Timer2();
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            final ContainerChest chest = (ContainerChest)this.mc.thePlayer.openContainer;
            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
                if (chest.getLowerChestInventory().getStackInSlot(i) != null && Timer2.hasReached(60L)) {
                    this.mc.playerController.windowClick(chest.windowId, i, 0, 1, this.mc.thePlayer);
                    Timer2.reset();
                }
            }
            if (Timer2.hasReached(65L)) {
                this.mc.thePlayer.closeScreen();
            }
        }
    }
}
