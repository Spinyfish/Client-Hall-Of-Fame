package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", "Don't take knockback, let all the legits hackusate you!", 0, Category.COMBAT, AntiCheat.VANILLA, AntiCheat.AAC, AntiCheat.VULCAN);
    }

    @Override
    public void setup() {
        setToggled(false);
    }

    @Override
    public void onPacketReceived(PacketReceivedEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity)event.getPacket();
            if(s12.getEntityID() == mc.thePlayer.getEntityId()) {
                event.setCancelled(true);
                
                if(this.antiCheat == AntiCheat.AAC) {
                	mc.thePlayer.jump();
                	mc.thePlayer.motionX *= mc.thePlayer.motionX/2;
                	mc.thePlayer.motionZ *= mc.thePlayer.motionZ/2;
            }
                
                if(this.antiCheat == AntiCheat.VULCAN) {
                	Utils.message = "Recieved Velocity (" + mc.thePlayer.posX + "-" + mc.thePlayer.posY + "-" + mc.thePlayer.posZ;
                	mc.thePlayer.motionX = -mc.thePlayer.motionX;
                	mc.thePlayer.motionZ = -mc.thePlayer.motionZ;
                	mc.timer.timerSpeed = 0.5;
                	mc.timer.timerSpeed = 1;
            }
                
                
                
            }
        }

        if (event.getPacket() instanceof S27PacketExplosion) {
            event.setCancelled(true);
        }
    }
}