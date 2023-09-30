package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModeInfo(name = "Blink", parent = NoFall.class)
public class BlinkNoFall extends ModuleMode<NoFall> {
    private final List<Vec3> vectors = new ArrayList<>();
    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();

    private boolean shouldBlink;

    @Override
    public void onEnable() {
        this.vectors.clear();
        this.packets.clear();

        this.shouldBlink = false;
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> onMotion = event -> {
        if (MovementUtil.canFall() && !mc.isIntegratedServerRunning()) {
            if (FallDistanceComponent.distance > 2.5 && !MovementUtil.inLiquid() && !mc.thePlayer.capabilities.isFlying) {
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

    @Callback
    final EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (this.shouldBlink) {
                if (event.getPacketState() == PacketEvent.PacketState.SENDING && event.getPacket() instanceof C03PacketPlayer) {
                    event.cancel();
                    this.packets.add(event.getPacket());
                }
            }
        }
    };
}
