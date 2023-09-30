package dev.echo.module.impl.combat;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.AttackEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.movement.Flight;
import dev.echo.module.impl.movement.Step;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C18PacketSpectate;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public final class Criticals extends Module {
    int attack = 0;
    private boolean stage;
    private double offset;
    private int groundTicks;
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Packet", "Dev", "Verus", "Vulcan");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "Packet", "Packet", "Edit");
    private final NumberSetting delay = new NumberSetting("Delay", 1, 20, 0, 1);
    private final TimerUtil timer = new TimerUtil();

    public Criticals() {
        super("Criticals", Category.COMBAT, "Crit attacks");
        delay.addParent(mode, m -> !(m.is("Verus") || (m.is("Watchdog") && watchdogMode.is("Edit"))));
        watchdogMode.addParent(mode, m -> m.is("Watchdog"));
        this.addSettings(mode, watchdogMode, delay);
    }

    @Link
    public Listener<AttackEvent> attackEventListener = e -> {
        if (mode.is("Vulcan")) {
            attack++;
            if (attack > 7 && mc.thePlayer.onGround) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.16477328182606651, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.08307781780646721, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0030162615090425808, mc.thePlayer.posZ, false));
                mc.thePlayer.onCriticalHit(e.getTargetEntity());
                attack = 0;
            }
        }
    };
    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        setSuffix(mode.getMode());
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
                if (e.isPre() && watchdogMode.is("Edit") && !Echo.INSTANCE.isEnabled(Flight.class) && !Step.isStepping && KillAura.attacking) {
                    if (e.isOnGround()) {
                        groundTicks++;
                        if (groundTicks > 2) {
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
            case "Dev":
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
    };

    @Override
    public void onEnable() {
        attack = 0;
        super.onEnable();
    }
}
