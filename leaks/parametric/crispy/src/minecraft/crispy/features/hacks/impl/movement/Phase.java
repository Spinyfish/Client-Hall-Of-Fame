package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventCollide;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.SpeedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.superblaubeere27.valuesystem.ModeValue;

@HackInfo(name = "Phase", category = Category.MOVEMENT)
public class Phase extends Hack {
    ModeValue modeValue = new ModeValue("Phase Mode", "Redesky", "Redesky", "Clip");

    @Override
    public void onEnable() {
        if (modeValue.getMode().equalsIgnoreCase("Redesky") && Minecraft.theWorld != null) {
            if (mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + -0.00000001, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + -0.00000001, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            }
        }
        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + modeValue.getMode());
            EventUpdate event = (EventUpdate) e;
            switch (modeValue.getMode()) {
                case "Clip": {
                    if (mc.thePlayer.isCollidedHorizontally) {
                        event.sneak = true;
                        SpeedUtils.setPositionNoUp(1.5);
                    }
                    break;
                }
            }
        }
        if(e instanceof EventCollide) {

        }


    }
}
