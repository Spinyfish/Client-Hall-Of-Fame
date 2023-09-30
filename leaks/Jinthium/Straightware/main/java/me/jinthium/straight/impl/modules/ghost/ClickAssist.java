package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.NumberSetting;

public class ClickAssist extends Module {

    public final NumberSetting extraLeftClicks = new NumberSetting("Extra Left Clicks",1, 0, 3, 1);
    public final NumberSetting extraRightClicks = new NumberSetting("Extra Right Clicks", 1, 0, 3, 1);

    public int leftClicks, rightClicks;
    private boolean leftClick, rightClick;

    public ClickAssist(){
        super("Click Assist", Category.GHOST);
        this.addSettings(extraLeftClicks, extraRightClicks);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(!event.isPre())
            return;

        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (!leftClick) {
                leftClicks = extraLeftClicks.getValue().intValue();
            }

            leftClick = true;
        } else {
            leftClick = false;
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (!rightClick) {
                rightClicks = extraRightClicks.getValue().intValue();
            }

            rightClick = true;
        } else {
            rightClick = false;
        }

        if (leftClicks > 0 && Math.random() > 0.2) {
            leftClicks--;

            if (!mc.thePlayer.isUsingItem() && !BadPacketsComponent.bad()) {
                mc.clickMouse();
            }
        } else if (rightClicks > 0 && Math.random() > 0.2) {
            rightClicks--;

            if (!mc.thePlayer.isUsingItem() && !BadPacketsComponent.bad()) {
                mc.rightClickMouse();
            }
        }
    };
}
