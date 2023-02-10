/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 25.09.22, 19:05
 */

/**
 * @project Myth
 * @author CodeMan
 * @at 16.09.22, 19:27
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.NumberSetting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@Feature.Info(
        name = "AutoHead",
        description = "Automatically uses golden heads in the pit",
        category = Feature.Category.COMBAT
)
public class AutoHeadFeature extends Feature {

    public final NumberSetting health = new NumberSetting("Health", 70, 0, 100, 1).setSuffix("%");

    private int tick, oldSlot, delay;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if(event.getState() != EventState.PRE) return;

        if(delay > 0) {
            delay--;
            return;
        }

        if((getPlayer().getHealth() / getPlayer().getMaxHealth()) * 100 >= health.getValue() && tick == 0) return;

        if(tick == 0) {
            int slot = getSlot();
            if(slot == -1) return;
            oldSlot = getPlayer().inventory.currentItem;
            getPlayer().inventory.currentItem = slot - 36;
            tick = 1;
        } else if(tick == 1) {
            sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().inventory.getCurrentItem()));
            tick = 2;
        } else if(tick == 2) {
            getPlayer().inventory.currentItem = oldSlot;
            delay = 20;
            tick = 0;
        }
    };

    public int getSlot() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == Items.skull) {
                return i;
            }
        }
        return -1;
    }

}
