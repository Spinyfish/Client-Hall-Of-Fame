package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.NumberSetting;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 0, 0, 3, 1);

    public FastPlace(){
        super("Fast Place", Category.GHOST);
        this.addSettings(delay);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if (event.isPre() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
            mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, this.delay.getValue().intValue());
    };
}
