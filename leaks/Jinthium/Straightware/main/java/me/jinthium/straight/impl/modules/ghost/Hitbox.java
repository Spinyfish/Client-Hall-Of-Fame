package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.game.MouseOverEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import org.lwjglx.input.Mouse;

public class Hitbox extends Module {

    public final NumberSetting expand = new NumberSetting("Expand Amount", 0, 0, 6, 0.01);
    private final BooleanSetting effectRange = new BooleanSetting("Effect range", true);

    private int exempt;

    public Hitbox(){
        super("Hitbox", Category.GHOST);
        this.addSettings(expand, effectRange);
    }

    @Callback
    final EventCallback<MouseOverEvent> mouseOverEventEventCallback = event -> {
        if (Mouse.isButtonDown(1)) {
            exempt = 1;
        }

        if (exempt > 0) return;

        event.setExpand(this.expand.getValue().floatValue());

        if (!this.effectRange.isEnabled())
            event.setRange(event.getRange() - expand.getValue());
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(event.isPre())
            exempt--;
    };
}
