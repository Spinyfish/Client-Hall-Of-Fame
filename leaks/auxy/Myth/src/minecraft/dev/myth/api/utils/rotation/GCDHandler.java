/**
 * @project Myth
 * @author CodeMan
 * @at 04.01.23, 15:27
 */
package dev.myth.api.utils.rotation;

import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.interfaces.IMethods;
import dev.myth.api.utils.EvictingList;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class GCDHandler implements IMethods {

    public float lastYawGCD, lastPitchGCD;
    public float averageYawGCD, averagePitchGCD;
    public EvictingList<Float> yawGCDList = new EvictingList<>(20);
    public EvictingList<Float> pitchGCDList = new EvictingList<>(20);
    public int ignoreTicks;

    @Handler(EventPriority.HIGHEST)
    public final Listener<UpdateEvent> packetEventListener = event -> {
        if(event.getState() != EventState.PRE) return;

        if(ignoreTicks > 0) {
            ignoreTicks--;
            return;
        }

        float yaw = event.getYaw();
        float pitch = event.getPitch();

        float lastYaw = event.getLastYaw();
        float lastPitch = event.getLastPitch();

        if(getPlayer().ticksExisted < 10 || lastPitch == pitch || lastYaw == yaw) return;

        float deltaYaw = Math.abs(yaw - lastYaw);
        float deltaPitch = Math.abs(pitch - lastPitch);

        float yawGCD = getGCD(yaw, lastYaw);
        float pitchGCD = getGCD(pitch, lastPitch);

        float deltaYawGCD = Math.abs(yawGCD - lastYawGCD);
        float deltaPitchGCD = Math.abs(pitchGCD - lastPitchGCD);

        yawGCDList.add(yawGCD);
        pitchGCDList.add(pitchGCD);

        averageYawGCD = getAverage(yawGCDList);
        averagePitchGCD = getAverage(pitchGCDList);

//        doLog("yawGCD: " + yawGCD + " pitchGCD: " + pitchGCD + " deltaYawGCD: " + deltaYawGCD + " deltaPitchGCD: " + deltaPitchGCD);

        if(deltaYawGCD > 0) lastYawGCD = yawGCD;
        if(deltaPitchGCD > 0) lastPitchGCD = pitchGCD;
    };

    @Handler(EventPriority.LOWEST)
    public final Listener<UpdateEvent> packetEventListener2 = event -> {
        if(event.getState() != EventState.PRE) return;

        if(ignoreTicks > 0) {
            return;
        }

        if(getPlayer().ticksExisted < 10) return;

        float[] deltaRots = {
                event.getYaw() - event.getLastYaw(),
                event.getPitch() - event.getLastPitch()
        };

        event.setYaw(event.getYaw() - deltaRots[0] % lastYawGCD);
        event.setPitch(event.getPitch() - deltaRots[1] % lastPitchGCD);
        event.setPitch(MathHelper.clamp_float(event.getPitch(), -90, 90));

//        event.setYaw(applyGCD(event.getYaw(), lastYawGCD));
//        event.setPitch(applyGCD(event.getPitch(), lastPitchGCD));

//        doLog(event.getYaw() + " " + event.getPitch());
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener3 = packetEvent -> {
        if(packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            ignoreTicks = 4;
        }
    };

    public float applyGCD(float value, float gcd) {
        return Math.round(value / gcd) * gcd;
    }

    public float getGCD(float a, float b) {
        if (b == 0) return a;
        return getGCD(b, a % b);
    }

    public float getAverage(List<Float> list) {
        float total = 0;
        for (Float f : list) {
            total += f;
        }
        return total / list.size();
    }

}
