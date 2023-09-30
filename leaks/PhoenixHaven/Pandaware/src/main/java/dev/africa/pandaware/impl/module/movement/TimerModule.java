package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "Timer", category = Category.MOVEMENT)
public class TimerModule extends Module {
    private final ConcurrentLinkedQueue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();

    private final NumberSetting speed = new NumberSetting("Speed", 10, 0.1, 2, 0.01);
    private final BooleanSetting timerTicksBool = new BooleanSetting("Use Skip Ticks", false);
    private final NumberSetting timerTicks = new NumberSetting("Skip Ticks", 20, 2, 5, 1, this.timerTicksBool::getValue);
    private final BooleanSetting poll = new BooleanSetting("Poll", false);

    public TimerModule() {
        this.registerSettings(
                this.speed,
                //this.poll, TODO: fix poll
                this.timerTicksBool,
                this.timerTicks
        );
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (this.poll.getValue()) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                event.cancel();
                this.packetQueue.add(event.getPacket());
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.poll.getValue() && !this.packetQueue.isEmpty()) {
            if (event.getEventState() == Event.EventState.PRE) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetQueue.poll());
                    Printer.chat("sent");
                }
            }
        }
        if (this.timerTicksBool.getValue()) {
            if (mc.thePlayer.ticksExisted % this.timerTicks.getValue().floatValue() == 0) {
                mc.timer.timerSpeed = this.speed.getValue().floatValue();
            } else {
                mc.timer.timerSpeed = 1f;
            }
        } else if (mc.theWorld != null && mc.thePlayer != null) {
            mc.timer.timerSpeed = this.speed.getValue().floatValue();
        } else {
            mc.timer.timerSpeed = 1f;
        }
    };

    @Override
    public void onEnable() {
        if (!this.timerTicksBool.getValue()) {
            mc.timer.timerSpeed = this.speed.getValue().floatValue();
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        if (!this.packetQueue.isEmpty()) {
            this.packetQueue.forEach(packet -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetQueue.poll()));
        }
    }
}