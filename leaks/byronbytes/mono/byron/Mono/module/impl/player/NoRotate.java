package byron.Mono.module.impl.player;

import byron.Mono.event.impl.EventPacket;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInterface(name = "NoRotate", description = "Can't force me!", category = Category.Player)
public class NoRotate extends Module {

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            packet.yaw = mc.thePlayer.rotationYaw;
            packet.pitch = mc.thePlayer.rotationPitch;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
