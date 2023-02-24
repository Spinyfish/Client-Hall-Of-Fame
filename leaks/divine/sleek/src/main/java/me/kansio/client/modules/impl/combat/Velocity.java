package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.moshi.IPacketUtils;
import me.kansio.client.utils.network.TimedPacket;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.ArrayList;
import java.util.LinkedList;

@ModuleData(
        name = "Velocity",
        category = ModuleCategory.COMBAT,
        description = "Allows you to modify your knockback"
)
public class Velocity extends Module implements IPacketUtils {

    private NumberValue<Double> v = new NumberValue<>("Vertical", this, 100.0, 0.0, 100.0, 1.0);
    private NumberValue<Double> h = new NumberValue<>("Horizontal", this, 100.0, 0.0, 100.0, 1.0);
    private ModeValue modeValue = new ModeValue("Mode", this, "Packet", "Push", "Delayed");
    private NumberValue<Integer> delay = new NumberValue<Integer>("Delay (MS)", this, 500, 0, 3000, 50,
            modeValue, "Delayed");
    public BooleanValue explotion = new BooleanValue("Explosion", this, true);

    // Handles delayed velocities
    LinkedList<TimedPacket> velocities = new LinkedList<>();
    LinkedList<TimedPacket> removeVelocities = new LinkedList<>();

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        try {
            if (modeValue.getValue().equalsIgnoreCase("Delayed")) {
                velocities.forEach(vel -> {
                    if (vel.postAddTime() > delay.getValue()) {
                        mc.thePlayer.sendQueue.handleEntityVelocity((S12PacketEntityVelocity) vel.getPacket());
                        removeVelocities.add(vel);
                    }
                });
            }
            velocities.removeIf(timedPacket -> removeVelocities.contains(timedPacket));
            removeVelocities.clear();
        } catch (Exception ev) {
            ev.printStackTrace();
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        switch (modeValue.getValueAsString()) {
            case "Packet": {
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = event.getPacket();
                    event.setCancelled(mc.theWorld != null && mc.thePlayer != null && mc.theWorld.getEntityByID(packet.getEntityID()) == mc.thePlayer);
                } else if (event.getPacket() instanceof S27PacketExplosion) {
                    if (explotion.getValue()) {
                        event.setCancelled(true);
                    }
                }
                break;
            }
            case "Push": {
                onSVelocity(event, p -> {
                    double pushH = h.getValue() / 100;
                    double pushV = v.getValue() / 100;

                    p.motionX *= pushH;
                    p.motionZ *= pushH;
                    p.motionY *= pushV;
                });
                break;
            }
            case "Delayed": {
                onSVelocity(event, p -> {
                    double pushH = h.getValue() / 100;
                    double pushV = v.getValue() / 100;

                    p.motionX *= pushH;
                    p.motionZ *= pushH;
                    p.motionY *= pushV;

                    velocities.add(create(p));
                    event.setCancelled(true);
                });
                break;
            }
        }

    }

    @Override
    public String getSuffix() {
        return " " + modeValue.getValueAsString();
    }
}
