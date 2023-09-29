package alos.stella.module.modules.movement;

import alos.stella.event.EventTarget;
import alos.stella.event.events.PacketEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.PacketUtils;
import alos.stella.utils.timer.TimerUtils;
import alos.stella.value.FloatValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;

@ModuleInfo(name = "AntiFall", description = "Antifall.", category = ModuleCategory.PLAYER)
public class AntiFall extends Module {
    public double[] lastGroundPos = new double[3];
    public static FloatValue pullbackTime = new FloatValue("Pullback Time", 1500f, 1000f, 2000f);
    private TimerUtils pullTimer = new TimerUtils();
    public static TimerUtils timer = new TimerUtils();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();

    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MovementUtils.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100)
            packets.clear();
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = ((C03PacketPlayer) event.getPacket());
            if (isInVoid()) {
                event.cancelEvent();
                packets.add(packet);
                if (timer.isDelayComplete(pullbackTime.get())) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                }
            } else {
                lastGroundPos[0] = mc.thePlayer.posX;
                lastGroundPos[1] = mc.thePlayer.posY;
                lastGroundPos[2] = mc.thePlayer.posZ;

                if (!packets.isEmpty()) {
                    //ChatUtils.debug("Release Packets - " + packets.size());
                    for (Packet p : packets)
                        PacketUtils.sendPacketNoEvent(p);
                    packets.clear();
                }
                timer.reset();
            }
        }
    }

    @EventTarget
    public void onRevPacket(PacketEvent e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook && packets.size() > 1) {
            packets.clear();
        }
    }

    @Override
    public String getTag() {
        return pullbackTime.get().toString();
    }

}