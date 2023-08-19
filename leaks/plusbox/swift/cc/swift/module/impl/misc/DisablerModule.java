/**
 * @project hakarware
 * @author CodeMan, Spectre
 * @at 25.07.23, 19:31
 */

package cc.swift.module.impl.misc;

import cc.swift.events.EventState;
import cc.swift.events.PacketEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.PacketUtil;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Event;
import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.var;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2APacketParticles;

import java.util.ArrayList;
import java.util.List;

public final class DisablerModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public DisablerModule() {
        super("Disabler", Module.Category.MISC);
        this.registerValues(mode);
    }

    public static final int TX_PER_TICK = 3;
    public static final int TX_MIN_LAG = 2;

    private final List<Packet<?>> chokedPackets = new ArrayList<>();
    private final List<Packet<?>> chokedMovements = new ArrayList<>();
    private int ticksPassed;


    private long nextTime;
    private final ArrayList<Packet<?>> packetsList = new ArrayList<>();

    // Disables Vulcan Reach, Vulcan Timer A (gamespeed), and 90% of Cubecraft Sentinel
    @Handler(EventPriority.LOWEST)
    public final Listener<PacketEvent> packetEventListener = event -> {

        // For sparky disabler because staff will try to crash you with particles
        if(mode.getValue() == Mode.SPARKY) {
            if (event.getPacket() instanceof S2APacketParticles) event.setCancelled(true);
        }

        // doesnt work because of singleplayer being a local server and networkmanager being silly
        if (mc.isSingleplayer()) return;

        if (event.getDirection() != EventState.SEND || event.getState() != EventState.PRE) return;
        if (event.isCancelled()) return;


        /**
        * toilet splunger disabala $$
        * @author Dort
        */
        switch (mode.getValue()) {
            case SPARKY:
                if (event.getPacket() instanceof C0BPacketEntityAction) {
                    C0BPacketEntityAction entityAction = event.getPacket();
                    if (entityAction.getAction() == C0BPacketEntityAction.Action.START_SPRINTING ||
                            entityAction.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)
                        event.setCancelled(true);
                }
                Packet<?> packet = event.getPacket();
                if ((mc.thePlayer != null && mc.thePlayer.ticksExisted == 0) || (mc.thePlayer == null && packet instanceof C00PacketKeepAlive)) {
                    chokedMovements.clear();
                    chokedPackets.clear();
                    return;
                }
                if (packet instanceof C0FPacketConfirmTransaction) {
                    event.setCancelled(true);
                    chokedPackets.add(packet);
                }
                if (packet instanceof C00PacketKeepAlive && chokedPackets.size() > 0) {
                    event.setCancelled(true);
                    chokedPackets.add(packet);
                    ChatUtil.printChatMessage("sent back: " + chokedPackets.size() + " tx");
                }
                if (packet instanceof C03PacketPlayer && chokedPackets.size() > 0) {
                    ((C03PacketPlayer) event.getPacket()).setPositionY(((C03PacketPlayer) event.getPacket()).getPositionY() - 0.062499999999999985F);
                    chokedMovements.add(packet);
                    event.setCancelled(true);
                }
                break;
            case FUNNY:
                PacketUtil.sendPacketNoEvent(new C0CPacketInput());
                break;
            case VULCAN:
                if(!(event.getPacket() instanceof C03PacketPlayer) || (((C03PacketPlayer) event.getPacket()).isMoving())) {
                    packetsList.add(event.getPacket());
                }
                event.setCancelled(true);

                if (System.currentTimeMillis() > nextTime) {
                    if (!packetsList.isEmpty()) {
                        try {
                            packetsList.forEach(p -> mc.getNetHandler().getNetworkManager().sendPacket(p, null));
                            packetsList.clear();
                        } catch (Exception ignore) {}
                    }

                    nextTime = System.currentTimeMillis() + 250;
                }
                break;
            case REPLACE:
                if (event.getPacket() instanceof C03PacketPlayer) {
                    event.setCancelled(true);
                    C03PacketPlayer p = event.getPacket();
                    //mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(p.getPositionX(), p.getPositionY(), p.getPositionZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, p.isOnGround()), null);
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, p.isOnGround()), null);

                }
                break;

            case PVPLAND:
                if(event.getPacket() instanceof C0FPacketConfirmTransaction){
                    event.setCancelled(true);
                    PacketUtil.sendPacketNoEventDelayed(event.getPacket(), 1000l);
                }
                break;
        }
    };

    public Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        if(event.getState() != EventState.PRE) return;
        if (ticksPassed >= 2) {
            while (chokedPackets.size() > TX_MIN_LAG) {
                var pkt = chokedPackets.remove(0); // remove first elem
                // dupe packet to fake low enough latency
                for (int j = 0; j < TX_PER_TICK; j++) {
                    PacketUtil.sendPacketNoEvent(pkt);
                }
            }
            // keep 1 c03 behind, sparky loves when you show him this trick, but the admins get really mad :/
            while (chokedMovements.size() > 1) {
                PacketUtil.sendPacketNoEvent(chokedMovements.remove(0));
            }
            ticksPassed = 0;
        }
        ticksPassed++;
    };

    @Override
    public void onEnable() {
        nextTime = System.currentTimeMillis();
        packetsList.clear();

        if (mc.isSingleplayer())
            ChatUtil.printChatMessage("Disabler doesn't work in singleplayer.");
    }

    @Override
    public void onDisable() {
        if (!packetsList.isEmpty()) {
            packetsList.forEach(p -> mc.getNetHandler().getNetworkManager().sendPacket(p, null));
            packetsList.clear();
        }
    }

    enum Mode {
        VULCAN,
        REPLACE,
        FUNNY,
        PVPLAND,
        SPARKY
    }
}
