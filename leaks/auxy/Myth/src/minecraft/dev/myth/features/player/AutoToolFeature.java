/**
 * @project Myth
 * @author CodeMan
 * @at 20.01.23, 11:45
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
@Feature.Info(
        name = "AutoTool",
        description = "Automatically switches to the best tool for the block you're mining",
        category = Feature.Category.PLAYER
)
public class AutoToolFeature extends Feature {

    public final BooleanSetting switchBack = new BooleanSetting("SwitchBack", true);

    private int oldSlot;
    private boolean switched;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE) return;

        if (getPlayer() == null) {
            switched = false;
            return;
        }

        if (MC.objectMouseOver == null || MC.objectMouseOver.getBlockPos() == null || !getGameSettings().keyBindAttack.isKeyDown()) {

            if (!switched) return;

            if (switchBack.getValue()) {
                getPlayer().inventory.currentItem = oldSlot;
            }
            switched = false;
            oldSlot = -1;

            return;
        }

        int bestSlot = -1;
        float bestSpeed = 1f;
        for (int i = 0; i < 9; i++) {
            if (getPlayer().inventory.getStackInSlot(i) == null) continue;
            float speed = getPlayer().inventory.getStackInSlot(i).getStrVsBlock(getWorld().getBlockState(MC.objectMouseOver.getBlockPos()).getBlock());
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        if (bestSlot != -1) {
            if (getPlayer().inventory.currentItem != bestSlot) {
                if (!switched) oldSlot = getPlayer().inventory.currentItem;

                getPlayer().inventory.currentItem = bestSlot;

                switched = true;
            }
        }

    };

}
