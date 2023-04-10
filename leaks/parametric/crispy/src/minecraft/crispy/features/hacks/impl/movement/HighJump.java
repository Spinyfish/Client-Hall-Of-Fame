package crispy.features.hacks.impl.movement;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;

import java.util.ArrayList;

@HackInfo(name = "HighJump", category = Category.MOVEMENT)
public class HighJump extends Hack {

    private long lastDmg;
    ModeValue mode = new ModeValue("Mode", "Verus1", "Verus1", "Verus2");
    BooleanValue bobbing = new BooleanValue("Bobbing", false);
    boolean jumped;
    boolean damagedz;
    double y;
    private final ArrayList<Packet> flyTrap = new ArrayList<>();
    int count;

    @Override
    public void onEnable() {
        if (Minecraft.theWorld != null) {
            count = 0;

            if (mode.getMode().equalsIgnoreCase("Verus1")) {
                this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 4, this.mc.thePlayer.posZ, false));
                this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
            } else {
            }
        }
        super.onEnable();

    }




    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            switch (mode.getMode()) {
                case "Verus1": {
                    if (mc.thePlayer.hurtTime > 0) {
                        damagedz = true;
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY += 9.282, mc.thePlayer.posZ);
                        mc.thePlayer.speedInAir = 0.2f;
                        count++;
                        Crispy.addChatMessage(count + " BBBBB");

                    }
                    break;
                }
            }
        }
    }
}