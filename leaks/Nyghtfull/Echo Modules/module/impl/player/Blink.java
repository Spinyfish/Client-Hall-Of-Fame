package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.ParentAttribute;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Blink extends Module {
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private final BooleanSetting pulse = new BooleanSetting("Pulse",  false);
    private final NumberSetting delayPulse = new NumberSetting("Tick Delay",20, 100, 4, 1);

    private EntityOtherPlayerMP blinkEntity;

    List<Vec3> path = new ArrayList<>();

    public Blink() {
        super("Blink", Category.PLAYER, "holds movement packets");
        delayPulse.addParent(pulse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(pulse, delayPulse);
    }

    @Link
    public Listener<PacketSendEvent> onPacketSend = event -> {
        if (mc.thePlayer == null || mc.thePlayer.isDead || mc.isSingleplayer() || mc.thePlayer.ticksExisted < 50) {
            packets.clear();
            return;
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            packets.add(event.getPacket());
            event.setCancelled(true);
        }

        if (pulse.isEnabled()) {
            if (!packets.isEmpty() && mc.thePlayer.ticksExisted % delayPulse.getValue().intValue() == 0 && Math.random() > 0.1) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
            }
        }
    };

    @Link
    public Listener<MotionEvent> motionEventListener = event -> {
        if(event.isPre()) {
            if (mc.thePlayer.ticksExisted < 50) return;

            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }

            if (pulse.isEnabled()) {
                while (path.size() > delayPulse.getValue().intValue()) {
                    path.remove(0);
                }
            }

            if (pulse.isEnabled() && blinkEntity != null) {
                mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
            }
        }
    };

    @Link
    public Listener<Render3DEvent> render3DEventListener = e -> {
       // Echo.INSTANCE.getModuleCollection().getModule(Breadcrumbs.class).renderLine(path);
    };

    @Override
    public void onEnable() {
        path.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }
}
