package dev.tenacity.module.impl.player;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.Breadcrumbs;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.server.PacketUtils;
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
    private final NumberSetting delayPulse = new NumberSetting("Tick Delay",20, 100, 1, 1);

    private EntityOtherPlayerMP blinkEntity;

    List<Vec3> path = new ArrayList<>();

    public Blink() {
        super("Blink", Category.PLAYER, "holds movement packets");
        delayPulse.addParent(pulse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(pulse, delayPulse);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isDead || mc.isSingleplayer() || mc.thePlayer.ticksExisted < 50) {
            packets.clear();
            return;
        }

        if (event.getPacket() instanceof C03PacketPlayer ) {
            packets.add(event.getPacket());
            event.cancel();
        }

        if (pulse.isEnabled()) {
            if (!packets.isEmpty() && mc.thePlayer.ticksExisted % delayPulse.getValue().intValue() == 0 && Math.random() > 0.1) {
                packets.forEach(PacketUtils::sendPacketNoEvent);
                packets.clear();
            }
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
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
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        Tenacity.INSTANCE.getModuleCollection().getModule(Breadcrumbs.class).renderLine(path);
    }

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
