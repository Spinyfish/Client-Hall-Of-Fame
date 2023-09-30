package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.var;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "FastBow", description = "Funny", category = Category.PLAYER)
public class FastBowModule extends Module {

    private final NumberSetting packets = new NumberSetting("Packets", 20, 1, 5, 1);

    private int packet;

    public FastBowModule() {
        registerSettings(
                packets
        );
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                if ((mc.gameSettings.keyBindUseItem.isKeyDown())) {
                    var heldItem = mc.thePlayer.inventory.getCurrentItem().getItem();
                    if (heldItem instanceof ItemBow) {
                        if (packet < this.packets.getValue().intValue()) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                            packet++;
                        } else {
                            mc.playerController.onStoppedUsingItem(mc.thePlayer);
                            packet = 0;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onDisable() {
        packet = 0;
    }
}
