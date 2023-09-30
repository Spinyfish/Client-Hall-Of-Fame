package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.utils.math.TimeHelper;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "VLReset", category = Category.MISC)
public class VLResetModule extends Module {
    private final TimeHelper timer = new TimeHelper();

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.onGround) {
            event.y = mc.thePlayer.motionY = 0.42f;
            mc.thePlayer.jump();
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.timer.reach(1000) && mc.thePlayer.onGround) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            this.toggle(false);
        }
        if (event.getEventState() == Event.EventState.PRE && !mc.thePlayer.onGround) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            if (this.timer.getMs() % 50 == 0) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
        } else if (event.getEventState() == Event.EventState.POST && mc.thePlayer.getHeldItem() != null) {
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                if (this.timer.getMs() % 250 == 0) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                            new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(),
                            0.0f, 0.0f, 0.0f
                    ));
                }
            }
        }
    };

    @Override
    public void onEnable() {
        this.timer.reset();
    }
}
