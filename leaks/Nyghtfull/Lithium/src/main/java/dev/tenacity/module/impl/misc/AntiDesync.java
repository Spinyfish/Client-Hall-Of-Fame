package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@SuppressWarnings("unused")
public final class AntiDesync extends Module {

    private int slot;

    public AntiDesync() {
        super("AntiDesync", Category.MISC, "pervents desync client side");
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            slot = ((C09PacketHeldItemChange) event.getPacket()).getSlotId();
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (slot != mc.thePlayer.inventory.currentItem && slot != -1) {
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

}
