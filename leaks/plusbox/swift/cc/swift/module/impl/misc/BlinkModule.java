package cc.swift.module.impl.misc;

import cc.swift.events.PacketEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.util.PacketUtil;
import cc.swift.util.TimerUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkModule extends Module {
    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private final TimerUtil timerUtil = new TimerUtil();

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.NORMAL);
    private final DoubleValue delay = new DoubleValue("Delay", 500D, 50, 5000, 50).setDependency(() -> mode.getValue() == Mode.DELAY);
    private final DoubleValue time = new DoubleValue("Time", 500D, 50, 5000, 50).setDependency(() -> mode.getValue() == Mode.PULSE);
    private final BooleanValue attack = new BooleanValue("Attack", false);
    private final BooleanValue update = new BooleanValue("Player Update", false);
    private final BooleanValue move = new BooleanValue("Player Move", false);
    private final BooleanValue look = new BooleanValue("Player Look", false);
    private final BooleanValue moveLook = new BooleanValue("Player Move/Look", false);
    private final BooleanValue digRelease = new BooleanValue("Dig/Release", false);
    private final BooleanValue placeUse = new BooleanValue("Place/Use", false);
    private final BooleanValue changeSlot = new BooleanValue("Change Slot", false);

    public BlinkModule() {
        super("Blink", Category.MISC);
        registerValues(mode, delay, time, attack, update, move, look, moveLook, digRelease, placeUse, changeSlot);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timerUtil.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (!packets.isEmpty()) {
            handlePackets();
        }
    }

    @Handler(EventPriority.DEFAULT)
    public Listener<UpdateEvent> updateEventListener = updateEvent -> {
        if (mode.getValue() == Mode.PULSE && timerUtil.hasTimeElapsed(time.getValue().longValue()) && !packets.isEmpty()) {
            handlePackets();
            timerUtil.reset();
        }
    };

    @Handler(EventPriority.DEFAULT)
    public Listener<PacketEvent> packetEventListener = packetEvent -> {
        if ((attack.getValue() && packetEvent.getPacket() instanceof C02PacketUseEntity) || (update.getValue() && packetEvent.getPacket() instanceof C03PacketPlayer && !(packetEvent.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || packetEvent.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || packetEvent.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) || (move.getValue() && packetEvent.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) || (look.getValue() && packetEvent.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) || (moveLook.getValue() && packetEvent.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) || (digRelease.getValue() && packetEvent.getPacket() instanceof C07PacketPlayerDigging) || (placeUse.getValue() && packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) || (changeSlot.getValue() && packetEvent.getPacket() instanceof C09PacketHeldItemChange)) {
            if (mode.getValue() != Mode.DELAY) {
                packets.add(packetEvent.getPacket());
            } else {
                PacketUtil.sendPacketNoEventDelayed(packetEvent.getPacket(), delay.getValue().longValue());
            }
            packetEvent.setCancelled(true);
        }
    };

    private void handlePackets() {
        packets.forEach(PacketUtil::sendPacketNoEvent);
        packets.clear();
    }

    public enum Mode {
        NORMAL,
        DELAY,
        PULSE
    }
}
