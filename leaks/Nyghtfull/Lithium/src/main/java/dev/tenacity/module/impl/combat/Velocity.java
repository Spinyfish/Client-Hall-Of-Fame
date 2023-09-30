package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MovingObjectPosition;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Packet","Minemen", "Intave", "Grim", "Packet", "Reverse", "Polar", "Minemen");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your velocity.");
        this.addSettings(mode, horizontal, vertical);
    }
    private boolean attacked;

    /* Grim Velocity variables */
    private static int grim_ticks = 0;


    @Override
    public void onEnable() {
        grim_ticks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {

        // Nyghtfull's
        if(mode.is("Cock")) {
            if(mc.thePlayer.hurtTime>1) {
                if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                    event.cancel();
                    ChatUtil.print("sex");
                }
            }
        }

        // Nyghtfull's
        if(mc.thePlayer.hurtTime>1) {
            if (mode.is("Grim") && event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (grim_ticks < 6) {
                    event.cancel();
                }
                if (grim_ticks > 6) {
                    grim_ticks = 0;
                }
                ChatUtil.print(grim_ticks);
                grim_ticks++;
            }
        }

        super.onPacketSendEvent(event);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {

        Packet <?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;

            switch (mode.getMode()) {
                case "Packet": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                case "Reverse": {
                    s12.motionX *= -s12.motionX;
                  //  s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                case"Cock": {
                    s12.motionX *= 100;
                    s12.motionY *= 100;
                    s12.motionZ *= 100;
                    break;
                }
                case "Grim": {
                    if(grim_ticks < 6) {
                        event.cancel();
                    }

                    break;
                }
                case "Minemen": {
                    // Code removed. Made by Liticane.
                    break;
                }

                default: {
                    break;
                }
            }
        }

        if (packet instanceof S27PacketExplosion) {
            S27PacketExplosion s12 = (S27PacketExplosion) packet;

            switch (mode.getMode()) {
                case "Packet": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                case "Reverse": {
                    s12.motionX *= -s12.motionX;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                case "Minemen": {
                    // Code removed. Made by Liticane.
                    break;
                }
                default: {
                    break;
                }
            }
        }

        super.onPacketReceiveEvent(event);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {


        if (mode.is("Intave")) {
            // Code removed. Made by Liticane.
        }

        // Fun Fact: This is a 0 100% POLAR velocity.
        if (mode.is("Polar")) {
            // Code removed. Made by Liticane.
        }

        super.onUpdateEvent(event);
    }
}
