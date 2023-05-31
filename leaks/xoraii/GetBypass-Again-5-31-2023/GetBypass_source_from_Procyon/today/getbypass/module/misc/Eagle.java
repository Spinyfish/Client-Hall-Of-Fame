// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.misc;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.block.BlockAir;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public final class Eagle extends Module
{
    private boolean sneaking;
    
    public Eagle() {
        super("Eagle", 0, "Sneaks on the edge of blocks", Category.MISC);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && this.mc.thePlayer.onGround) {
                this.sneaking = true;
                this.mc.gameSettings.keyBindSneak.setKeyPressed(true);
            }
            else if (this.sneaking) {
                this.mc.gameSettings.keyBindSneak.setKeyPressed(false);
                this.sneaking = false;
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(this.mc.gameSettings.keyBindSneak)) {
            this.mc.gameSettings.keyBindSneak.setKeyPressed(false);
        }
    }
}
