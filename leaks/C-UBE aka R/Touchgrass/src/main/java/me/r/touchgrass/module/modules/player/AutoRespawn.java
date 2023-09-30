package me.r.touchgrass.module.modules.player;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.network.play.client.C16PacketClientStatus;
import me.r.touchgrass.events.EventUpdate;

/**
 * Created by r on 11/02/2021
 */
@Info(name = "AutoRespawn", description = "Automatically respawn when dead", category = Category.Player)
public class AutoRespawn extends Module {
    public AutoRespawn() {}

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0) {
            mc.thePlayer.respawnPlayer();
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    }
}
