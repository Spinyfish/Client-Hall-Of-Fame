package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.ArrayDeque;

public class Blink extends Module {

    private final ArrayDeque<Packet> packetDeque = new ArrayDeque<>();
    private final Stopwatch fakeLagTimer = new Stopwatch();
    private int enabledTicks;

    private final NumberValue pulseDelay = new NumberValue("Pulse Delay", this, 150, 50, 5000, true);
    private final BooleanValue pulse = new BooleanValue("Pulse", this, false);

    public Blink(ModuleData moduleData) {
        super(moduleData);
        register(pulseDelay, pulse);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        enabledTicks++;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (pulse.getValue()) {
                packetDeque.add(event.getPacket());
                if (fakeLagTimer.timeElapsed(pulseDelay.getValue().longValue())) {
                    while (!packetDeque.isEmpty()) {
                        PacketUtil.sendPacketNoEvent(packetDeque.poll());
                    }
                    enabledTicks = 0;
                    fakeLagTimer.resetTime();
                }
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        while (!packetDeque.isEmpty()) {
            PacketUtil.sendPacketNoEvent(packetDeque.poll());
        }
        enabledTicks = 0;
        super.onDisable();
    }

    @Override
    public String getSuffix() {
        return " \2477" + enabledTicks;
    }
}
