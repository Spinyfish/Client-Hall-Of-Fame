package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.WorldEvent;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.Setting;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.player.MoveUtil;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;
import net.minecraft.util.ChatAllowedCharacters;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Matrix", "Tick", "Stack", "C0F Cancel", "Grim", "Legit", "Vulcan", "Strafe");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);
    private final NumberSetting chance = new NumberSetting("Chance", 100, 100, 0, 1);
    private final BooleanSetting onlyWhileMoving = new BooleanSetting("Only while moving", false);
    private final BooleanSetting staffCheck = new BooleanSetting("Staff check", false);

    private long lastDamageTimestamp, lastAlertTimestamp;
    private boolean cancel;
    private int stack;
    private int ticks = 0;
    private final TimerUtil countdown = new TimerUtil();

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your knockback");
        Setting.addParent(mode, m -> m.is("Packet"), horizontal, vertical, staffCheck);
        this.addSettings(mode, horizontal, vertical, chance, onlyWhileMoving, staffCheck);
    }

    @Link
    public Listener<PacketSendEvent> onPacketSend = event -> {
        if (mode.is("C0F Cancel")) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction && mc.thePlayer.hurtTime > 0) {
                event.setCancelled(true);
            }
        }
    };

    @Link
    public Listener<PacketReceiveEvent> onPacketReceive = e -> {
        if (!AlwaysUtil.isPlayerInGame()) {
            return;
        }
        this.setSuffix(mode.getMode());
        if ((onlyWhileMoving.isEnabled() && !MovementUtils.isMoving()) || (chance.getValue() != 100 && MathUtils.getRandomInRange(0, 100) > chance.getValue())) {
            return;
        }
        Packet<?> packet = e.getPacket();
        switch (mode.getMode()) {
            case "Packet":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                } else if (packet instanceof S27PacketExplosion) {
                    S27PacketExplosion s27 = (S27PacketExplosion) e.getPacket();
                    s27.motionX *= horizontal.getValue() / 100.0;
                    s27.motionZ *= horizontal.getValue() / 100.0;
                    s27.motionY *= vertical.getValue() / 100.0;
                }
                break;
            case "C0F Cancel":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        e.setCancelled(true);
                    }
                }
                if (packet instanceof S27PacketExplosion) {
                    e.setCancelled(true);
                }
                break;
            case "Legit":
                if (mc.thePlayer.hurtTime == 9) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                } else {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                }

                break;
            case "Grim":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        if (packet instanceof S32PacketConfirmTransaction) {
                            e.setCancelled(true);
                        }
                        e.setCancelled(true);
                    }
                }
                break;
            case "Stack":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;
                    cancel = !cancel;
                    if (cancel) {
                        e.setCancelled(true);
                    }
                }
                if (packet instanceof S27PacketExplosion) {
                    e.setCancelled(true);
                }
                break;
            case "Matrix":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        s12.motionX *= 5 / 100.0;
                        s12.motionZ *= 5 / 100.0;
                        s12.motionY *= 100 / 100.0;
                    }
                }
                break;
            case "Tick":
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId() && mc.thePlayer.ticksExisted % 3 == 0) {
                        s12.motionX *= 5 / 100.0;
                        s12.motionZ *= 5 / 100.0;
                        s12.motionY *= 100 / 100.0;
                    }
                }
                break;
            case "Vulcan": //probably doesnt work but eh
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                    if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        e.setCancelled(true);
                        if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                            e.setCancelled(true);
                        }
                    }
                }
                break;
            case "Strafe": { // works on almost every non prediction/simulation anticheat
                if (mc.thePlayer.hurtTime == 8) {
                    MoveUtil.strafe(0.025);
                }
            }
            break;
        }
    };

    @Link
    public final Listener<WorldEvent> onWorldEvent = e -> {
        stack = 0;
    };

    private boolean cancel(PacketReceiveEvent e) {
        if (staffCheck.isEnabled() && System.currentTimeMillis() - lastDamageTimestamp > 500) {
            if (System.currentTimeMillis() - lastAlertTimestamp > 250) {
                NotificationManager.post(NotificationType.WARNING, "Velocity", "Suspicious knockback detected!", 2);
                lastAlertTimestamp = System.currentTimeMillis();
            }
            return true;
        }
        if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
            e.setCancelled(true);
            return true;
        }
        return false;
    }

}
