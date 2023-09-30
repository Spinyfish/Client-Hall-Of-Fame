package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.setting.NumberRangeSetting;
import dev.africa.pandaware.utils.client.Timer;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PingSpoofDisabler extends ModuleMode<DisablerModule> {
    private final NumberRangeSetting pingSpoof = new NumberRangeSetting("Range", 10000, 50, 500, 1000);

    private final Timer timer = new Timer();
    private final ConcurrentLinkedQueue<Packet<?>> packetList = new ConcurrentLinkedQueue<>();

    public PingSpoofDisabler(String name, DisablerModule parent) {
        super(name, parent);

        this.registerSettings(this.pingSpoof);
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if ((event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction) && mc.thePlayer != null && mc.theWorld != null) {
            this.packetList.add(event.getPacket());
            event.cancel();
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (timer.hasReached(RandomUtils.nextInt(pingSpoof.getFirstValue().intValue(), pingSpoof.getSecondValue().intValue())) && mc.thePlayer != null && mc.theWorld != null) {
            packetList.forEach(packet1 -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetList.poll()));
            this.packetList.clear();
            this.timer.reset();
        }
    };

    @Override
    public void onDisable() {
        packetList.forEach(packet1 -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetList.poll()));
        this.packetList.clear();
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }
}
