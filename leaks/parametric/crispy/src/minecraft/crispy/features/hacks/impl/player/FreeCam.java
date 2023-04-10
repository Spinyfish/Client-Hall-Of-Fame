package crispy.features.hacks.impl.player;

import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventCollide;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.SpeedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

@HackInfo(name = "FreeCam", category = Category.PLAYER)
public class FreeCam extends Hack {
    private double posX, posY, posZ;

    @Override
    public void onEnable() {
        if(Minecraft.theWorld != null) {
            this.posX = mc.thePlayer.posX;
            this.posY = mc.thePlayer.posY;
            this.posZ = mc.thePlayer.posZ;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(Minecraft.theWorld != null) {
            mc.thePlayer.setPosition(posX, posY, posZ);
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
        }
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventPacket) {
            if(((EventPacket) e).getPacket() instanceof C03PacketPlayer) {
                e.setCancelled(true);
            }
        } else if(e instanceof EventCollide) {
            e.setCancelled(true);
        } else if(e instanceof EventUpdate) {
            mc.thePlayer.motionY = 0;
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.motionY += 2;
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                mc.thePlayer.motionY -= 2;
            }
            mc.thePlayer.speedInAir = 0.07f;

            SpeedUtils.setMotion(2);
        }
    }
}
