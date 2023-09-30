package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Flight;
import dev.tenacity.module.impl.movement.Step;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C18PacketSpectate;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public final class Criticals extends Module {

    private boolean stage;
    private double offset;
    private int groundTicks;
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Packet", "Test", "Verus");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "Packet", "Packet", "Edit");
    private final NumberSetting delay = new NumberSetting("Delay", 1, 20, 0, 1);
    private final TimerUtil timer = new TimerUtil();

    public Criticals() {
        super("Criticals", Category.COMBAT, "Crit attacks");
        delay.addParent(mode, m -> !(m.is("Verus") || (m.is("Watchdog") && watchdogMode.is("Edit"))));
        watchdogMode.addParent(mode, m -> m.is("Watchdog"));
        this.addSettings(mode, watchdogMode, delay);
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Watchdog":
                if (watchdogMode.is("Packet")) {
                    if (KillAura.attacking && e.isOnGround() && !Step.isStepping) {
                        if (KillAura.target != null && KillAura.target.hurtTime >= delay.getValue().intValue()) {
                            for (double offset : new double[]{0.06f, 0.01f}) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.001), mc.thePlayer.posZ, false));
                            }
                        }
                    }
                }
                if (e.isPre() && watchdogMode.is("Edit") && !Tenacity.INSTANCE.isEnabled(Flight.class) && !Step.isStepping && KillAura.attacking) {
                    if (e.isOnGround()) {
                        groundTicks++;
                        if(groundTicks > 2) {
                            stage = !stage;
                            e.setY(e.getY() + (stage ? 0.015 : 0.01) - Math.random() * 0.0001);
                            e.setOnGround(false);
                        }
                    } else {
                        groundTicks = 0;
                    }
                }
                break;
            case "Packet":
                if (mc.objectMouseOver.entityHit != null && mc.thePlayer.onGround) {
                    if (mc.objectMouseOver.entityHit.hurtResistantTime > delay.getValue().intValue()) {
                        for (double offset : new double[]{0.006253453, 0.002253453, 0.001253453}) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                        }
                    }
                }
                break;
            case "Test":
                if (mc.objectMouseOver.entityHit != null && mc.thePlayer.onGround) {
                    if (mc.objectMouseOver.entityHit.hurtResistantTime > delay.getValue().intValue()) {
                        for (double offset : new double[]{0.06253453, 0.02253453, 0.001253453, 0.0001135346}) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                            PacketUtils.sendPacketNoEvent(new C18PacketSpectate(UUID.randomUUID()));
                        }
                    }
                }
                break;
            case "Verus":
                if (KillAura.attacking && KillAura.target != null && e.isOnGround()) {
                    switch (KillAura.target.hurtResistantTime) {
                        case 17:
                        case 19:
                            e.setOnGround(false);
                            e.setY(e.getY() + ThreadLocalRandom.current().nextDouble(0.001, 0.0011));
                            break;
                        case 18:
                        case 20:
                            e.setOnGround(false);
                            e.setY(e.getY() + 0.03 + ThreadLocalRandom.current().nextDouble(0.001, 0.0011));
                            break;
                    }
                }
        }
    }

}
