package me.jinthium.straight.impl.components;

import com.mojang.authlib.GameProfile;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.network.ServerJoinEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import org.checkerframework.checker.units.qual.K;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkComponent extends Component {
    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking, dispatch, funnyThing;
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static TimerUtil exemptionWatch = new TimerUtil();
    private EntityOtherPlayerMP blinkEntity;

    public static void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING)
            return;

        if (mc.thePlayer == null) {
            packets.clear();
            exemptedPackets.clear();
            return;
        }

        if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            blinking = false;
            exemptedPackets.clear();
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                packet instanceof C01PacketEncryptionResponse) {
            return;
        }

        if(event.getPacket() instanceof C03PacketPlayer c03PacketPlayer) {
//            if(Client.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled()){
//                float[] rots = new float[] {MovementUtil.getMovementDirection1(), 0};
//                float[] prevRots = mc.thePlayer.currentEvent.getPrevRots();
//                RotationUtils.applySmoothing(prevRots, 17.5f, rots);
//                c03PacketPlayer.setYaw(rots[0]);
//            }
//
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if(killAura.isEnabled() && killAura.target != null && !Client.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled() && blinking){
                c03PacketPlayer.setYaw(killAura.rotationStore[0]);
                c03PacketPlayer.setPitch(killAura.rotationStore[1]);
            }
//
            if (blinking && !dispatch) {
                spawnEntity(c03PacketPlayer.x, c03PacketPlayer.y, c03PacketPlayer.z, c03PacketPlayer.yaw, c03PacketPlayer.getPitch());
            } else {
                deSpawnEntity();
            }
        }

        if (blinking && !dispatch) {
            if (exemptionWatch.hasTimeElapsed(100, true)) {
                exemptedPackets.clear();
            }

            PingSpoofComponent.spoofing = false;

            if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                    packetClass == packet.getClass())) {
                packets.add(packet);
                event.setCancelled(true);
            }
        } else if (packet instanceof C03PacketPlayer) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            dispatch = false;
        }
    };

    public static void dispatch() {
        dispatch = true;
    }

    public void deSpawnEntity() {
        if (blinkEntity != null) {
            mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
            KillAura.bots.remove(blinkEntity);
            blinkEntity = null;
        }
    }

    public void spawnEntity(double x, double y, double z, float yaw, float pitch) {
        if (blinkEntity == null) {
            blinkEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            blinkEntity.setPositionAndRotation(x, y, z, yaw, pitch);
            blinkEntity.rotationYawHead = yaw;
            blinkEntity.setSprinting(mc.thePlayer.isSprinting());
            blinkEntity.setInvisible(true);
            blinkEntity.setSneaking(mc.thePlayer.isSneaking());
            blinkEntity.inventory = mc.thePlayer.inventory;

            mc.theWorld.addEntityToWorld(blinkEntity.getEntityId(), blinkEntity);
            KillAura.bots.add(blinkEntity);
        }
    }



    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };

    @Callback
    final EventCallback<ServerJoinEvent> serverJoinEventEventCallback = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };
}
