package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat extends BaseModule {

    public FastEat() {
        super("FastEat", "Eat faster", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            if(mc.thePlayer.isUsingItem()) {
                Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
                if(item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk) {
                    for (int i = 0; i < 20; i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                    }
                    mc.thePlayer.stopUsingItem();
                }
            }
        }
    }

}
