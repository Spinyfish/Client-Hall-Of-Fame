package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.utils.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.*;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Disabler extends Mod {

    public ModeSetting mode = new ModeSetting("Mode",this,"Watchdog","Verus Combat","Cancel Keepalive");
    public NumberSetting delay = new NumberSetting("Ping Delay",0,5000,750,1,this);

    public Timer timer = new Timer();

    private final Queue<Packet> packetQueue = new ConcurrentLinkedDeque<>();

    public Disabler() {
        super("Disabler","Tries to weaken anti-cheat checks", Category.PLAYER);
    }

    @EventTarget
    public void onEventRenderHUD(EventRenderHUD e) {
        if(mc.thePlayer.ticksExisted <= 1) {
            if(mc.thePlayer.ticksExisted == 1 && mode.is("Verus Combat")) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }
            packetQueue.clear();
        }

        if(mc.thePlayer.ticksExisted >= 60 && !packetQueue.isEmpty()) {
            if(timer.hasTimeElapsed((long) (delay.getValue()+Math.random()*40), true)) {
                mc.thePlayer.sendQueue.addToSilentSendQueue(packetQueue.poll());
            }
        }
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        Packet p = e.getPacket();

        switch(mode.getMode()) {
            case "Watchdog":

            if (p instanceof C0BPacketEntityAction && mc.thePlayer.ticksExisted > 1) {
                C0BPacketEntityAction C0B = (C0BPacketEntityAction) p;
                C0BPacketEntityAction.Action action = C0B.getAction();
                if (action == C0BPacketEntityAction.Action.START_SPRINTING || action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    e.setCancelled(true);
                }
            }

            if (p instanceof C03PacketPlayer) {
                C03PacketPlayer C03 = (C03PacketPlayer) e.getPacket();
                if(mc.thePlayer.ticksExisted < 40)
                    e.setCancelled(true);

                if(mc.thePlayer.ticksExisted == 80) {
                    for (int i = 0; i <= 20; ++i) {
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C00PacketKeepAlive(-1));
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+20-i,mc.thePlayer.posZ,i % 2 == 0));
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, Short.MAX_VALUE,i % 4 == 0));
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+20-i,mc.thePlayer.posZ,i % 3 == 0));
                    }
                }

                if (mc.thePlayer.ticksExisted < 80) C03.y += 1E+5 - mc.thePlayer.ticksExisted*mc.thePlayer.ticksExisted;
            }
            break;
            case "Cancel Keepalive":
                if (p instanceof C0BPacketEntityAction) {
                    C0BPacketEntityAction C0B = (C0BPacketEntityAction) p;
                    C0BPacketEntityAction.Action action = C0B.getAction();
                    if (action == C0BPacketEntityAction.Action.START_SPRINTING || action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        e.setCancelled(true);
                    }
                }
                break;
            case "Verus Combat":
                if (p instanceof C0BPacketEntityAction && mc.thePlayer.ticksExisted > 1) {
                    C0BPacketEntityAction C0B = (C0BPacketEntityAction) p;
                    C0BPacketEntityAction.Action action = C0B.getAction();
                    if (action == C0BPacketEntityAction.Action.START_SPRINTING || action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        e.setCancelled(true);
                    }
                }
                break;
        }
    }
}
