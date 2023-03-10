/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.UUID;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.status.client.C01PacketPing;

public class Disabler
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Basic", "Basic", "Keep Sprint", "Watchdog Dev", "Watchdog", "Verus Old", "Verus Combat", "Kauri", "Hycraft Crash", "Ping", "Test");
    public NumberSetting delay = new NumberSetting("Pulse Delay", 60.0, 120.0, 70.0, 5.0);
    private final ArrayList<Packet> packets = new ArrayList();
    public TimeUtils timer = new TimeUtils();
    private boolean timerShouldCancel = true;
    public int currentTrans;
    public int count;

    public Disabler() {
        super("Disabler", 0, Category.OTHER);
        this.addSettings(this.mode, this.delay);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (this.mode.isMode("Watchdog")) {
            this.delay.setVisible(true);
        } else {
            this.delay.setVisible(false);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate event) {
        switch (this.mode.getMode()) {
            case "Watchdog": {
                if (!this.timer.hasReached(5000L)) break;
                this.timerShouldCancel = true;
                this.timer.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        this.packets.clear();
        this.timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.packets.clear();
        super.onDisable();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket p) {
        this.setSuffix(this.mode.getMode());
        switch (this.mode.getMode()) {
            case "Basic": {
                if (!(p.getPacket() instanceof C0FPacketConfirmTransaction) && !(p.getPacket() instanceof C00PacketKeepAlive)) break;
                p.setCancelled(true);
                break;
            }
            case "Kauri": {
                if (!(p.getPacket() instanceof C0FPacketConfirmTransaction)) break;
                p.setCancelled(true);
                break;
            }
            case "Watchdog": {
                if ((double)Disabler.mc.thePlayer.ticksExisted < this.delay.getValue() && p.getPacket() instanceof C03PacketPlayer && Disabler.mc.thePlayer.ticksExisted % 15 != 0) {
                    p.setCancelled(true);
                }
                if (p.getPacket() instanceof C03PacketPlayer && !(p.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) && !(p.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) && !(p.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition)) {
                    p.setCancelled(true);
                }
                if (!(p.getPacket() instanceof C02PacketUseEntity) && !(p.getPacket() instanceof C03PacketPlayer) && !(p.getPacket() instanceof C07PacketPlayerDigging) && !(p.getPacket() instanceof C08PacketPlayerBlockPlacement) && !(p.getPacket() instanceof C0APacketAnimation) && (!(p.getPacket() instanceof C0BPacketEntityAction) || !((double)Disabler.mc.thePlayer.ticksExisted > this.delay.getValue())) || !this.timerShouldCancel) break;
                if (!this.timer.hasReached(350L)) {
                    this.packets.add(p.getPacket());
                    p.setCancelled(true);
                    break;
                }
                this.timer.reset();
                this.timerShouldCancel = false;
                while (!this.packets.isEmpty()) {
                    this.sendPacketSilent(this.packets.get(this.packets.size()));
                }
                break;
            }
            case "Verus Old": {
                this.sendPacketSilent(new C0CPacketInput());
                this.sendPacketSilent(new C18PacketSpectate(UUID.randomUUID()));
                break;
            }
            case "Verus Combat": {
                if (!(p.getPacket() instanceof C0FPacketConfirmTransaction) || this.packets.size() >= 20) break;
                p.setCancelled(true);
                this.packets.add(p.getPacket());
                break;
            }
            case "Hycraft Crash": {
                if (!(p.getPacket() instanceof S27PacketExplosion) || !(p.getPacket() instanceof S2APacketParticles) || !(p.getPacket() instanceof S1DPacketEntityEffect) || !(p.getPacket() instanceof S44PacketWorldBorder)) break;
                p.setCancelled(true);
                break;
            }
            case "Ping": {
                if (p.getPacket() instanceof C01PacketPing) {
                    p.setCancelled(true);
                    break;
                }
            }
            case "Watchdog Dev": {
                if (p.getPacket() instanceof C01PacketChatMessage) {
                    if (p.getPacket() instanceof S12PacketEntityVelocity) {
                        this.packets.add(p.getPacket());
                    }
                    this.packets.add(p.getPacket());
                    p.setCancelled(true);
                }
                if (!(p.getPacket() instanceof C0FPacketConfirmTransaction)) break;
                p.setCancelled(true);
                break;
            }
            case "Test": {
                if (p.getPacket() instanceof C03PacketPlayer && p.getPacket() instanceof C00Handshake) {
                    p.setCancelled(true);
                }
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Disabler.mc.thePlayer.posX, Disabler.mc.thePlayer.posY, Disabler.mc.thePlayer.posZ, true));
                break;
            }
            case "Keep Sprint": {
                if (!(p.getPacket() instanceof C0BPacketEntityAction)) break;
                C0BPacketEntityAction c0B = (C0BPacketEntityAction)p.getPacket();
                if (c0B.getAction().equals((Object)C0BPacketEntityAction.Action.START_SPRINTING)) {
                    if (EntityPlayerSP.serverSprintState) {
                        this.sendPacketSilent(new C0BPacketEntityAction(Disabler.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        EntityPlayerSP.serverSprintState = false;
                    }
                    p.setCancelled(true);
                }
                if (!c0B.getAction().equals((Object)C0BPacketEntityAction.Action.STOP_SPRINTING)) break;
                p.setCancelled(true);
                break;
            }
        }
    }
}

