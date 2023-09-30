/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package wtf.monsoon.impl.module.combat;

import com.mojang.authlib.GameProfile;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;

public class Velocity
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.CANCEL).describedBy("The mode of velocity.");
    private final Setting<Integer> horMod = new Setting<Integer>("Horizontal", 0).minimum(0).maximum(100).incrementation(1).describedBy("Horizontal velocity modifier.").visibleWhen(() -> this.mode.getValue().equals((Object)Mode.CANCEL));
    private final Setting<Integer> vertMod = new Setting<Integer>("Vertical", 0).minimum(0).maximum(100).incrementation(1).describedBy("Vertical velocity modifier.").visibleWhen(() -> this.mode.getValue().equals((Object)Mode.CANCEL));
    private final Setting<Double> strength = new Setting<Double>("Strength", 3.0).minimum(1.0).maximum(10.0).incrementation(1.0).describedBy("The strength of velocity.").visibleWhen(() -> this.mode.getValue().equals((Object)Mode.DRAG_CLICK));
    private boolean receivedVelocity;
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity)e.getPacket();
            if (packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                int amount = (packet.getMotionX() + packet.getMotionY() + packet.getMotionZ()) / 3 / 200;
                Wrapper.getSexToyManager().vibrate(amount);
            }
            if (packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                this.receivedVelocity = true;
            }
            switch (this.mode.getValue()) {
                case CANCEL: {
                    packet.setMotionX((int)((double)packet.getMotionX() * 0.01 * (double)this.horMod.getValue().intValue()));
                    packet.setMotionY((int)((double)packet.getMotionY() * 0.01 * (double)this.vertMod.getValue().intValue()));
                    packet.setMotionZ((int)((double)packet.getMotionZ() * 0.01 * (double)this.horMod.getValue().intValue()));
                    break;
                }
                case WATCHDOG: {
                    if (packet.getEntityID() != this.mc.thePlayer.getEntityId()) break;
                    e.setCancelled(true);
                    double motionX = (double)packet.getMotionX() / 8000.0;
                    double motionY = (double)packet.getMotionY() / 8000.0;
                    double motionZ = (double)packet.getMotionZ() / 8000.0;
                    double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                    this.mc.thePlayer.motionY = motionY;
                    this.player.setSpeed(Math.max(speed, (double)this.player.getSpeed()));
                }
            }
        } else if (e.getPacket() instanceof S27PacketExplosion) {
            switch (this.mode.getValue()) {
                case CANCEL: {
                    e.setCancelled(true);
                    break;
                }
                case WATCHDOG: {
                    if (this.mc.thePlayer.hurtTime <= 0) break;
                    e.setCancelled(true);
                    this.mc.thePlayer.motionY += 0.001 - Math.random() / 100.0;
                }
            }
        }
    };
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (this.mode.getValue() == Mode.DRAG_CLICK && this.mc.thePlayer.hurtTime == 10 && this.receivedVelocity) {
            for (int i = 0; i < 10; ++i) {
                EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(UUID.randomUUID(), "a"));
                PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                PacketUtil.sendPacketNoEvent(new C02PacketUseEntity((Entity)fakePlayer, C02PacketUseEntity.Action.ATTACK));
                this.mc.thePlayer.motionX *= 1.0 / this.strength.getValue();
                this.mc.thePlayer.motionZ *= 1.0 / this.strength.getValue();
            }
        }
        if (this.mc.thePlayer.hurtTime == 0) {
            this.receivedVelocity = false;
        }
    };

    public Velocity() {
        super("Velocity", "Take no knockback.", Category.COMBAT);
        this.setMetadata(() -> {
            switch (this.mode.getValue()) {
                case DRAG_CLICK: {
                    return StringUtil.formatEnum(this.mode.getValue()) + ", " + this.strength.getValue();
                }
                case WATCHDOG: {
                    return "Watchdog";
                }
                case CANCEL: {
                    return this.horMod.getValue() + "% " + this.vertMod.getValue() + "%";
                }
            }
            return "";
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.receivedVelocity = false;
    }

    static enum Mode {
        CANCEL,
        WATCHDOG,
        DRAG_CLICK;

    }
}

