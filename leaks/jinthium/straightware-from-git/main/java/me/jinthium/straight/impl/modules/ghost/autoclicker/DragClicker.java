package me.jinthium.straight.impl.modules.ghost.autoclicker;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.TickEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.ghost.AutoClicker;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.player.PlayerUtil;

@ModeInfo(name = "Drag", parent = AutoClicker.class)
public class DragClicker extends ModuleMode<AutoClicker> {

    private final NumberSetting minDragLength = new NumberSetting("Min Drag Length", 17, 1, 50, 1);

    private final NumberSetting maxDragLength = new NumberSetting("Max Drag Length", 18, 1, 50, 1);

    private final NumberSetting minDragDelay = new NumberSetting("Min Drag Delay", 6, 1, 20, 1);

    private final NumberSetting maxDragDelay = new NumberSetting("Max Drag Delay", 6, 1, 20, 1);
    private int nextLength, nextDelay;

    public DragClicker(){

        this.registerSettings(minDragLength, maxDragLength, minDragDelay, maxDragDelay);
    }

    @Callback
    final EventCallback<TickEvent> tickEventEventCallback = event -> {
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (nextLength < 0) {
                nextDelay--;

                if (nextDelay < 0) {
                    nextDelay = MathUtils.getRandomInRange(minDragDelay.getValue().intValue(), maxDragDelay.getValue().intValue());
                    nextLength = MathUtils.getRandomInRange(minDragLength.getValue().intValue(), maxDragLength.getValue().intValue());
                }
            } else if (Math.random() < 0.95) {
                nextLength--;
                PlayerUtil.sendClick(0, true);
            }
        }
    };
}
