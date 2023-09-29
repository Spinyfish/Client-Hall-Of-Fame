package wtf.automn.module.impl.world;


import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.player.EventMotion;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;

@ModuleInfo(name = "eagle", displayName = "Eagle", category = Category.WORLD)
public class ModuleEagle extends Module {
    @Override
    protected void onDisable() {
        this.MC.gameSettings.keyBindSneak.pressed = false;
    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onPre(EventMotion eventMotion){

    }

    @EventHandler
    public void onUpdate(final EventPlayerUpdate e) {
        this.MC.rightClickMouse();
        final BlockPos blockPos = new BlockPos(this.MC.thePlayer.posX, this.MC.thePlayer.posY - 1, this.MC.thePlayer.posZ);
        if (this.MC.theWorld.getBlockState(blockPos).getBlock() == Blocks.air)
            this.MC.gameSettings.keyBindSneak.pressed = true;
        else this.MC.gameSettings.keyBindSneak.pressed = false;
    }
}
