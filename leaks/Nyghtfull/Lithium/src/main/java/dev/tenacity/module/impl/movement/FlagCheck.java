package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class FlagCheck extends Module {



    public FlagCheck() {
        super("FlagCheck", Category.MISC, "Disables shit on flag");
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
            if(Tenacity.INSTANCE.isEnabled(Flight.class)) {
                return;
            }
            if(e.getPacket() instanceof S08PacketPlayerPosLook) {
                NotificationManager.post(NotificationType.WARNING,"LagBack","Disabled Speed");

            }




    }



}
