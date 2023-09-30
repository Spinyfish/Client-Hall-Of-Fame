package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.world.SafeWalkEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import net.minecraft.item.ItemBlock;

public class SafeWalk extends Module {

    private final BooleanSetting blocksOnly = new BooleanSetting("Blocks Only", false);
    private final BooleanSetting backwardsOnly = new BooleanSetting("Backwards Only", false);

    public SafeWalk(){
        super("Safe Walk", Category.GHOST);
        this.addSettings(blocksOnly, backwardsOnly);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(event.isPre()) {
            mc.thePlayer.safeWalk = mc.thePlayer.onGround && (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.isEnabled()) &&
                    ((mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) ||
                            !this.blocksOnly.isEnabled());
        }
    };

    @Callback
    final EventCallback<SafeWalkEvent> safeWalkEventEventCallback = event -> {
        if(mc.thePlayer.safeWalk)
            event.cancel();
    };
}
