package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlinkNoFall extends ModuleMode<NoFallModule> {
    private final List<Vec3> vectors = new ArrayList<>();
    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();

    public BlinkNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    private boolean shouldBlink;

    @Override
    public void onEnable() {
        this.vectors.clear();
        this.packets.clear();

        this.shouldBlink = false;
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.getParent().canFall() && !mc.isIntegratedServerRunning()) {
            if (mc.thePlayer.fallDistance > 2.5 && !PlayerUtils.inLiquid() && !mc.thePlayer.capabilities.isFlying) {
                this.shouldBlink = true;

                if (this.packets.size() > 0) {
                    event.setOnGround(true);

                    double posX = mc.thePlayer.posX;
                    double posY = mc.thePlayer.posY;
                    double posZ = mc.thePlayer.posZ;

                    if (posX != mc.thePlayer.lastTickPosX
                            || posY != mc.thePlayer.lastTickPosY
                            || posZ != mc.thePlayer.lastTickPosZ) {
                        this.vectors.add(new Vec3(posX, posY, posZ));
                    }
                }
            } else {
                if (this.shouldBlink) {
                    this.packets.forEach(packet -> {
                        this.packets.remove(packet);
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
                    });

                    this.vectors.clear();
                    this.shouldBlink = false;
                }
            }
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (this.shouldBlink) {
                if (event.getState() == PacketEvent.State.SEND) {
                    event.cancel();
                    this.packets.add(event.getPacket());
                }
            }
        }
    };
}
