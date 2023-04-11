package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.MovementUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;


@ModuleInterface(name = "SafeWalk", description = "Walk without falling.", category = Category.Movement)
public class SafeWalk extends Module {

    @Subscribe
public void onUpdate(EventUpdate e) {
    if (mc.thePlayer.ticksExisted > 0 && MovementUtils.isOnGround(1.0E-9D) && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() != null && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
        mc.gameSettings.keyBindSneak.pressed = true;
    } else {
        mc.gameSettings.keyBindSneak.pressed = false;
    }

}


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
