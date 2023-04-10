package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.superblaubeere27.valuesystem.ModeValue;

@HackInfo(name = "Criticals", category = Category.COMBAT)
public class Criticals extends Hack {
    ModeValue mode = new ModeValue("Crit Modes", "Packet", "Packet", "Ground");

    boolean ground;

    @Override
    public void onEnable() {

        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setDisplayName(getName() + " \2477" + mode.getMode());
            if (mode.getMode().equalsIgnoreCase("Ground")) {
                if (!mc.gameSettings.keyBindJump.pressed) {
                    event.ground = false;
                }
                if (Aura.target == null)
                    ground = false;
            }

        }
    }

    public void critical() {

        if (mode.getMode().equalsIgnoreCase("Ground")) {
            if (!ground) {
                ground = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, true));
                }
            }
        } else if (mode.getMode().equalsIgnoreCase("Packet")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, true));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.1E-5, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

    }
}
